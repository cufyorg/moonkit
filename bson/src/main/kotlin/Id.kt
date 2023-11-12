/*
 *	Copyright 2022-2023 cufy.org and meemer.com
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
package org.cufy.bson

import kotlinx.serialization.Serializable
import org.cufy.bson.internal.IdSerializer

/* ============= ------------------ ============= */

/**
 * An id wrapper.
 *
 * @author LSafer
 * @since 2.0.0
 */
@Serializable(IdSerializer::class)
data class Id<T>(val value: String) : CharSequence by value {
    constructor() : this(ObjectId())
    constructor(value: ObjectId) : this(value.toHexString())
    constructor(value: Id<*>) : this(value.value)

    override fun toString(): String = value
}

/**
 * A typealias for generic-less [Id].
 *
 * @author LSafer
 * @since 2.0.0
 */
typealias AnyId = Id<out Any?>

/* ============= ------------------ ============= */

/**
 * Return the best fitting native wrapper for
 * this id.
 * A [BsonObjectId] if it is a valid object id
 * and a [BsonString] if it is not.
 *
 * @since 2.0.0
 */
val Id<*>.bson: BsonElement
    get() {
        if (ObjectId.isValid(value))
            return BsonObjectId(ObjectId(value))

        return BsonString(value)
    }

/**
 * Return the best fitting native wrapper for
 * this id.
 * A [BsonObjectId] if it is a valid object id
 * and a [BsonString] if it is not
 * or [BsonNull] if this is `null`.
 *
 * @since 2.0.0
 */
@get:JvmName("bsonNullable")
val Id<*>?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return the best fitting native wrapper for
 * this id.
 * A [BsonObjectId] if it is a valid object id
 * and a [BsonString] if it is not.
 *
 * @since 2.0.0
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val Id<*>.b: BsonElement get() = bson

/* ============= ------------------ ============= */

/**
 * Return an [ObjectId] with the value of this.
 *
 * @throws IllegalArgumentException if the string is not a valid hex string representation of an ObjectId.
 * @since 2.0.0
 */
fun Id<*>.toObjectId(): ObjectId {
    return ObjectId(value)
}

/**
 * Return an [ObjectId] with the value of this.
 * Or `null` if the value is not a valid [ObjectId].
 *
 * @since 2.0.0
 */
fun Id<*>.toObjectIdOrNull(): ObjectId? {
    if (ObjectId.isValid(value))
        return ObjectId(value)

    return null
}

/**
 * Return an [Id] instance from the value of this.
 */
fun <T> String.toId(): Id<T> {
    return Id(this)
}

/**
 * Return an [AnyId] instance from the value of this.
 */
@JvmName("toAnyId")
fun String.toId(): AnyId {
    return AnyId(this)
}

/**
 * Return an [Id] instance from the value of this.
 */
fun <T> ObjectId.toId(): Id<T> {
    return Id(this)
}

/**
 * Return an [AnyId] instance from the value of this.
 */
@JvmName("toAnyId")
fun ObjectId.toId(): AnyId {
    return AnyId(this)
}

/**
 * Cast this [Id] to an id of [T].
 */
fun <T> AnyId.toId(): Id<T> {
    return Id(this)
}

/**
 * Cast this [Id] to [AnyId].
 */
@JvmName("toAnyId")
fun AnyId.toId(): AnyId {
    return AnyId(this)
}

/* ============= ------------------ ============= */
