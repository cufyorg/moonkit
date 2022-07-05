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
package org.cufy.mangaka

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.BsonObjectId
import org.bson.BsonString
import org.bson.BsonValue
import org.bson.types.ObjectId

/**
 * Construct a new id.
 *
 * @since 1.0.0
 */
fun <T> Id(): Id<T> {
    return Id(ObjectId())
}

/**
 * Construct a new id from the given [value].
 *
 * @since 1.0.0
 */
fun <T> Id(value: ObjectId): Id<T> {
    return Id(value.toHexString())
}

/**
 * Case the given id into an id of [T].
 */
fun <T> Id(value: Id<*>): Id<T> {
    @Suppress("UNCHECKED_CAST")
    return value as Id<T>
}

/**
 * Construct a new id from normalizing the
 * given [value].
 *
 * @param value the value to normalize.
 * @since 1.0.0
 */
fun <T> Id(value: Any): Id<T> {
    return when (value) {
        is Id<*> -> Id(value)
        is String -> Id(value)
        is BsonString -> Id(value.value)
        is ObjectId -> Id(value)
        is BsonObjectId -> Id(value.value)
        else -> throw MangakaException("Unsupported id type: $value")
    }
}

/**
 * An id wrapper.
 *
 * TODO documentation for class Id
 *
 * @author LSafer
 * @since 1.0.0
 */
@Serializable(IdSerializer::class)
data class Id<T>(
    /**
     * The string representation of the id.
     *
     * @since 1.0.0
     */
    val value: String
) : CharSequence by value {
    override fun toString() = value
}

/**
 * Return the best fitting native wrapper for
 * this id.
 * A [BsonObjectId] if it is a valid object id
 * and a [BsonString] if it is not.
 *
 * @since 1.0.0
 */
val Id<*>.bson: BsonValue
    get() = when {
        ObjectId.isValid(value) -> BsonObjectId(ObjectId(value))
        else -> BsonString(value)
    }

/**
 * The serializer for [Id].
 *
 * @author LSafer
 * @since 1.0.0
 */
internal object IdSerializer : KSerializer<Id<*>> {
    override val descriptor = String.serializer().descriptor

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Id<*>) {
        encoder.encodeInline(descriptor).encodeString(value.value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Id<*> {
        return Id<Any?>(decoder.decodeInline(descriptor).decodeString())
    }
}
