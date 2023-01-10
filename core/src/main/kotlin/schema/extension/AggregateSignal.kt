/*
 *	Copyright 2022 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.cufy.monkt.schema.extension

import org.bson.BsonNumber
import org.cufy.bson.*
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

/* ============= ---- Protocol ---- ============= */

/**
 * A signal to aggregate some pipeline and return
 * the result.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class AggregateSignal(
    /**
     * The model to aggregate on.
     */
    val model: Model<*>,
    /**
     * The aggregation pipeline.
     */
    val pipeline: List<BsonDocument>
) : Signal<List<BsonDocument>>

/* ============= - Implementation - ============= */

/**
 * A signal handler for [AggregateSignal].
 *
 * @author LSafer
 * @since 2.0.0
 */
object AggregateSignalHandler : SignalHandler {
    @AdvancedMonktApi
    override fun canHandle(signal: Signal<*>): Boolean {
        return signal is AggregateSignal
    }

    @AdvancedMonktApi
    override suspend fun handle(signals: List<Signal<*>>): List<Any?> {
        return signals
            .map { it as AggregateSignal }
            .withIndex()
            .groupBy { (_, signal) -> signal.model }
            .map { (model, signalsWithIndex) ->
                model.aggregate({
                    `$facet` by {
                        signalsWithIndex.forEach { (index, signal) ->
                            "$index" by signal.pipeline
                        }
                    }
                })
            }
            .flatMap { it.first().entries }
            .sortedBy { (key, _) -> key.toInt() }
            .map { it.value }
    }
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * Enqueue a signal to perform an aggregation with
 * the given [pipeline].
 *
 * @param model the model to aggregate on.
 * @param pipeline the aggregation pipeline.
 * @return a delegate to the response of the signal.
 * @see Model.aggregate
 * @see OptionScope.enqueue
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any> OptionScope<*, *, *>.aggregate(
    model: Model<T>,
    pipeline: List<BsonDocument>,
): SignalProperty<List<BsonDocument>> {
    val message = AggregateSignal(model, pipeline)
    return enqueue(message)
}

/**
 * Enqueue a signal to perform an aggregation with
 * the given [pipeline].
 *
 * @param model the model to aggregate on.
 * @param pipeline the aggregation pipeline.
 * @return a delegate to the response of the signal.
 * @see Model.aggregate
 * @see OptionScope.enqueue
 */
fun <T : Any> OptionScope<*, *, *>.aggregate(
    model: Model<T>,
    vararg pipeline: BsonDocumentBlock
): SignalProperty<List<BsonDocument>> {
    return aggregate(model, pipeline.map { document(it) })
}

/**
 * Enqueue a signal to find the documents that
 * matches the given [filter].
 *
 * @param model the model to find on.
 * @param filter the find filter.
 * @return a delegate to the response of the signal.
 * @see Model.findImpl
 * @see OptionScope.enqueue
 */
fun <T : Any> OptionScope<*, *, *>.find(
    model: Model<T>,
    filter: BsonDocument
): SignalProperty<List<T>> {
    val property = aggregate(model,
        { `$match` by filter }
    )
    return property.then {
        model(it) { isNew = false }
    }
}

/**
 * Enqueue a signal to find the documents that
 * matches the given [filter].
 *
 * @param model the model to find on.
 * @param filter the find filter.
 * @return a delegate to the response of the signal.
 * @see Model.findImpl
 * @see OptionScope.enqueue
 */
fun <T : Any> OptionScope<*, *, *>.find(
    model: Model<T>,
    filter: BsonDocumentBlock,
): SignalProperty<List<T>> {
    return find(model, document(filter))
}

/**
 * Enqueue a signal to count the documents that
 * matches the given [filter].
 *
 * @param model the model to count on.
 * @param filter the find filter.
 * @return a delegate to the response of the signal.
 * @see Model.count
 * @see OptionScope.enqueue
 */
fun <T : Any> OptionScope<*, *, *>.count(
    model: Model<T>,
    filter: BsonDocument,
): SignalProperty<Long> {
    val property = aggregate(model,
        { `$match` by filter },
        { `$count` by "count" }
    )
    return property.then {
        val count = it[0]["count"] as BsonNumber

        count.longValue()
    }
}

/**
 * Enqueue a signal to count the documents that
 * matches the given [filter].
 *
 * @param model the model to count on.
 * @param filter the find filter.
 * @return a delegate to the response of the signal.
 * @see Model.count
 * @see OptionScope.enqueue
 */
fun <T : Any> OptionScope<*, *, *>.count(
    model: Model<T>,
    filter: BsonDocumentBlock
): SignalProperty<Long> {
    return count(model, document(filter))
}

/**
 * Enqueue a signal to check if any document
 * matches the given [filter].
 *
 * @param model the model to check on.
 * @param filter the find filter.
 * @return a delegate to the response of the signal.
 * @see Model.exists
 * @see OptionScope.enqueue
 */
fun <T : Any> OptionScope<*, *, *>.exists(
    model: Model<T>,
    filter: BsonDocument
): SignalProperty<Boolean> {
    val property = count(model, filter)
    return property.then {
        it > 0
    }
}

/**
 * Enqueue a signal to check if any document
 * matches the given [filter].
 *
 * @param model the model to check on.
 * @param filter the find filter.
 * @return a delegate to the response of the signal.
 * @see Model.exists
 * @see OptionScope.enqueue
 */
fun <T : Any> OptionScope<*, *, *>.exists(
    model: Model<T>,
    filter: BsonDocumentBlock
): SignalProperty<Boolean> {
    return exists(model, document(filter))
}
