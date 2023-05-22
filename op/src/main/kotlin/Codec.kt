/*
 *	Copyright 2023 cufy.org
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
package org.cufy.monop

import kotlinx.coroutines.Deferred
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonElement
import org.cufy.bson.Id
import org.cufy.codec.*
import org.cufy.mongodb.`$set`
import kotlin.Result.Companion.success
import kotlin.properties.ReadOnlyProperty

/*
These extensions are expected to be used as follows:

val foreign by "foreignId" be { Id } from map foreign MyCollection decode {
    it decode MyCollectionCodec
} createOperation Monop
*/

/**
 * Compose a new [Lazy] instance that uses [findOneById]
 * of the given [collection] with the value of [this] as
 * the argument.
 */
@OperationKeywordMarker
@ExperimentalMonopApi
infix fun Lazy<Id<*>?>.foreign(collection: OpCollection): Lazy<Op<BsonDocument?>> {
    return lazy {
        val value = value ?: return@lazy op { null }
        collection.findOneById(value)
    }
}

/**
 * Compose a new [Lazy] instance that uses [findOneById]
 * of the given [collection] with the value of [this] as
 * the argument.
 */
@JvmName("foreignWithCodec")
@OperationKeywordMarker
@ExperimentalMonopApi
infix fun <T, C> Lazy<Id<*>?>.foreign(collection: C): Lazy<Op<T?>> where C : OpCollection, C : Codec<T, BsonDocument> {
    return lazy {
        val value = value ?: return@lazy op { null }
        collection.findOneById(value).mapCatching {
            it?.let { decode(it, collection) }
        }
    }
}

/**
 * Compose a new [Lazy] instance that uses [mapCatching]
 * on the [Op] value of [then] with the given [block] as
 * the argument.
 *
 * @param block the decoding block
 */
@OperationKeywordMarker
@ExperimentalMonopApi
infix fun <T> Lazy<Op<BsonDocument?>>.decode(block: (BsonDocument) -> T): Lazy<Op<T?>> {
    return lazy { value.mapCatching { it?.let(block) } }
}

/**
 * Compose a new [Lazy] instance that creates a
 * new operation from the [Op] value of [this] and
 * returns it.
 *
 * If the given [monop] is not null, enqueue the
 * created operation to it.
 */
@ExperimentalMonopApi
infix fun <T> Lazy<Op<T>>.createOperation(monop: Monop?): Lazy<Operation<T>> {
    return lazy {
        value.createOperation().also {
            monop?.enqueue(it)
        }
    }
}

@OperationKeywordMarker
@ExperimentalMonopApi
infix fun <T, C> C.lookup(id: Lazy<Id<*>?>): Lazy<Deferred<T?>> where C : OpCollection, C : Codec<T, BsonDocument> {
    return lazy {
        val op: Op<T?> = when (val idValue = id.value) {
            null -> op { null }
            else -> findOneById(idValue).map {
                when (it) {
                    null -> success(null)
                    else -> tryDecode(it, this@lookup)
                }
            }
        }
        op.enqueue()
    }
}

@OperationKeywordMarker
@ExperimentalMonopApi
infix fun <T, C> C.lookup(id: () -> Id<*>?): Lazy<Deferred<T?>> where C : OpCollection, C : Codec<T, BsonDocument> {
    return this lookup lazy(id)
}

@OperationKeywordMarker
@ExperimentalMonopApi
infix fun <I, O : BsonElement> FieldCodec<I, O>.into(
    collection: OpCollection
): ReadOnlyProperty<DocumentProjection, (I) -> UpdateOneOp> {
    val field = this
    return ReadOnlyProperty { projection, _ ->
        val id = decodeAny(projection.element["_id"]) { Id }

        return@ReadOnlyProperty { value ->
            collection.updateOneById(id, {
                `$set` by {
                    field.name by encode(value, field)
                }
            })
        }
    }
}
