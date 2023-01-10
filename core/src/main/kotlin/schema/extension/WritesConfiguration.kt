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

import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.WriteModel
import org.cufy.bson.*
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

/* ============= ---- Protocol ---- ============= */

/**
 * Options to tweak the behaviour of writes
 * performance.
 */
class WritesTweak :
    WithFiltersTweak<WritesConfiguration> {

    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    override val filters: MutableList<ReturnOptionDataBlock<*, *, WritesConfiguration, Boolean>> =
        mutableListOf()
}

/**
 * The writes option configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
class WritesConfiguration {
    /**
     * The writes to be added.
     */
    val writes: MutableList<WriteModel<BsonDocument>> = mutableListOf()
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * Add the given [write].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, WritesConfiguration>.write(
    write: WriteModel<BsonDocument>
) {
    configuration.writes += write
}

/**
 * Add an update write.
 *
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation.
 * @since 2.0.0
 * @see UpdateOneModel
 */
fun <T : Any, M> OptionScope<T, M, WritesConfiguration>.update(
    update: Bson,
    options: UpdateOptions
) {
    val id = Document.getId(root)
    val filter = document { "_id" by id }
    val model = UpdateOneModel<BsonDocument>(filter, update, options)
    write(model)
}

/**
 * Add an update write
 *
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param block the options block to apply to the update operation.
 * @since 2.0.0
 * @see UpdateOneModel
 */
fun <T : Any, M> OptionScope<T, M, WritesConfiguration>.update(
    update: Bson,
    block: UpdateOptionsScope.() -> Unit = {}
) {
    var options = UpdateOptions()
    options = options.configure(block)
    update(update, options)
}

/**
 * Add an update write
 *
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param block the options block to apply to the update operation.
 * @since 2.0.0
 * @see UpdateOneModel
 */
fun <T : Any, M> OptionScope<T, M, WritesConfiguration>.update(
    update: BsonDocumentBlock,
    block: UpdateOptionsScope.() -> Unit = {}
) {
    update(document(update), block)
}

// builder

/**
 * Add the given writes [block].
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.writes(
    block: OptionBlock<T, M, WritesConfiguration>
) {
    val configuration = WritesConfiguration()
    option(configuration, block)
}

// monkt

/**
 * Run the writes options for the given [instances].
 *
 * @return the writes by model.
 */
@AdvancedMonktApi("This is a part of the instance save process")
suspend fun Monkt.performWrites(
    instances: List<Any>,
    tweak: WritesTweak
): Map<Model<*>, List<WriteModel<BsonDocument>>> {
    var options = instances.flatMap {
        Document.obtainOptions<WritesConfiguration>(it)
    }

    options = options.filter {
        tweak.filters.all { filter ->
            filter(it)
        }
    }

    performOption(options)

    return options
        .groupBy { it.model }
        .mapValues { (_, value) ->
            value.flatMap { it.configuration.writes }
        }
}
