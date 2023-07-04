/*
 *	Copyright 2022-2023 cufy.org
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

import java.math.BigDecimal

/* ============= ------------------ ============= */

/**
 * A global instance of [BsonArray] that has no items.
 *
 * @since 2.0.0
 */
@Deprecated("Use BsonArray.Empty instead", ReplaceWith(
    "BsonArray.Empty",
    "org.cufy.bson.BsonArray"
))
val EmptyBsonArray = BsonArray.Empty

/**
 * A global instance of [BsonDocument] that has no entries.
 *
 * @since 2.0.0
 */
@Deprecated("Use BsonDocument.Empty instead", ReplaceWith(
    "BsonDocument.Empty",
    "org.cufy.bson.BsonDocument"
))
val EmptyBsonDocument = BsonDocument.Empty

/**
 * The global instance of bson true.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "true.bson",
    "org.cufy.bson.bson"
))
@BsonKeywordMarker
val btrue = BsonBoolean.True

/**
 * The global instance of bson false.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "false.bson",
    "org.cufy.bson.bson"
))
@BsonKeywordMarker
val bfalse = BsonBoolean.False

/**
 * The global instance of bson null.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "null.bson",
    "org.cufy.bson.bson"
))
@BsonKeywordMarker
val bnull = BsonNull

/**
 * A global instance of bson undefined.
 */
@Deprecated("Use Unit.bson instead", ReplaceWith(
    "Unit.bson",
    "org.cufy.bson.bson"
))
@BsonKeywordMarker
val bundefined = BsonUndefined

/* ============= ------------------ ============= */

/**
 * Construct a new bson array from this list.
 */
fun Iterable<BsonElement>.toBsonArray(): BsonArray {
    return BsonArray(toList())
}

/**
 * Construct a new mutable bson list from this list.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see toMutableList
 * @since 2.0.0
 */
fun Iterable<BsonElement>.toMutableBsonList(): MutableBsonList {
    return toMutableList().asMutableBsonList()
}

/**
 * Obtain a mutable bson list backed by this list.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @since 2.0.0
 */
fun MutableList<BsonElement>.asMutableBsonList(): MutableBsonList {
    val content = this
    return object : MutableBsonList, MutableList<BsonElement> by content {
        override fun equals(other: Any?) =
            content == other

        override fun hashCode() =
            content.hashCode()

        override fun toString() =
            content.joinToString(",", "[", "]")
    }
}

/**
 * Construct a new bson document from this map.
 */
fun Map<String, BsonElement>.toBsonDocument(): BsonDocument {
    return BsonDocument(toMap())
}

/**
 * Construct a new mutable bson map from this map.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see toMutableMap
 * @since 2.0.0
 */
fun Map<String, BsonElement>.toMutableBsonMap(): MutableBsonMap {
    return toMutableMap().asMutableBsonMap()
}

/**
 * Obtain a mutable bson map backed by this map.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @since 2.0.0
 */
fun MutableMap<String, BsonElement>.asMutableBsonMap(): MutableBsonMap {
    val content = this
    return object : MutableBsonMap, MutableMap<String, BsonElement> by content {
        override fun equals(other: Any?) =
            content == other

        override fun hashCode() =
            content.hashCode()

        override fun toString() =
            content.entries.joinToString(",", "{", "}") {
                """"${it.key}":${it.value}"""
            }
    }
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonInt32] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val Int.b: BsonInt32 get() = BsonInt32(this)

/**
 * Return a [BsonInt64] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val Long.b: BsonInt64 get() = BsonInt64(this)

/**
 * Return a [BsonDouble] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val Double.b: BsonDouble get() = BsonDouble(this)

/**
 * Return a [BsonDecimal128] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val Decimal128.b: BsonDecimal128 get() = BsonDecimal128(this)

/**
 * Return a [BsonDecimal128] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val BigDecimal.b: BsonDecimal128 get() = toDecimal128().bson

/**
 * Return a [BsonString] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val String.b: BsonString get() = BsonString(this)

/**
 * Return a [BsonObjectId] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val ObjectId.b: BsonObjectId get() = BsonObjectId(this)

/**
 * Return the best fitting native wrapper for
 * this id.
 * A [BsonObjectId] if it is a valid object id
 * and a [BsonString] if it is not.
 *
 * @since 2.0.0
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val Id<*>.b: BsonElement
    get() {
        if (ObjectId.isValid(value))
            return BsonObjectId(ObjectId(value))

        return BsonString(value)
    }

/**
 * Return a [BsonBoolean] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith(
    "this.bson",
    "org.cufy.bson.bson"
))
val Boolean.b: BsonBoolean get() = BsonBoolean(this)

/* ============= ------------------ ============= */

/**
 * Return a [BsonInt32] with the value of this.
 */
inline val Int.bson: BsonInt32 get() = BsonInt32(this)

/**
 * Return a [BsonInt32] with the value of this or [BsonNull] if this is `null`.
 */
val Int?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonInt64] with the value of this.
 */
inline val Long.bson: BsonInt64 get() = BsonInt64(this)

/**
 * Return a [BsonInt64] with the value of this or [BsonNull] if this is `null`.
 */
val Long?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonDouble] with the value of this.
 */
inline val Double.bson: BsonDouble get() = BsonDouble(this)

/**
 * Return a [BsonDouble] with the value of this or [BsonNull] if this is `null`.
 */
val Double?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonDecimal128] with the value of this.
 */
inline val Decimal128.bson: BsonDecimal128 get() = BsonDecimal128(this)

/**
 * Return a [BsonDecimal128] with the value of this or [BsonNull] if this is `null`.
 */
val Decimal128?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonDecimal128] with the value of this.
 */
val BigDecimal.bson: BsonDecimal128 get() = toDecimal128().bson

/**
 * Return a [BsonDecimal128] with the value of this or [BsonNull] if this is `null`.
 */
val BigDecimal?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonString] with the value of this.
 */
inline val String.bson: BsonString get() = BsonString(this)

/**
 * Return a [BsonString] with the value of this or [BsonNull] if this is `null`.
 */
val String?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonObjectId] with the value of this.
 */
inline val ObjectId.bson: BsonObjectId get() = BsonObjectId(this)

/**
 * Return a [BsonObjectId] with the value of this or [BsonNull] if this is `null`.
 */
val ObjectId?.bson: BsonElement get() = this?.let { bson } ?: null.bson

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
 * Return a [BsonBoolean] with the value of this.
 */
inline val Boolean.bson: BsonBoolean get() = BsonBoolean(this)

/**
 * Return a [BsonBoolean] with the value of this or [BsonNull] if this is `null`.
 */
val Boolean?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return [BsonNull].
 *
 * Usage:
 * ```kotlin
 * null.bson
 * ```
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Nothing?.bson: BsonNull get() = BsonNull

/**
 * Return [BsonUndefined].
 *
 * Usage:
 * ```kotlin
 * Unit.bson
 * ```
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Unit.bson: BsonUndefined get() = BsonUndefined

/* ============= ------------------ ============= */

/**
 * Return a [Decimal128] with the value of this.
 */
fun Int.toDecimal128(): Decimal128 {
    return toLong().toDecimal128()
}

/**
 * Return a [Decimal128] with the value of this.
 */
fun Long.toDecimal128(): Decimal128 {
    return Decimal128(this)
}

/**
 * Return a [Decimal128] with the value of this.
 */
fun Double.toDecimal128(): Decimal128 {
    return when {
        isNaN() -> Decimal128.NaN
        isInfinite() -> when {
            this > 0 -> Decimal128.POSITIVE_INFINITY
            else -> Decimal128.NEGATIVE_INFINITY
        }
        else -> toBigDecimal().toDecimal128()
    }
}

/**
 * Return a [Decimal128] with the value of this.
 */
fun BigDecimal.toDecimal128(): Decimal128 {
    return Decimal128(this)
}

/**
 * Return a [BigDecimal] with the value of this.
 */
fun Decimal128.toBigDecimal(): BigDecimal {
    return bigDecimalValue()
}

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

/* ============= ------------------ ============= */

/**
 * Invoke this function with the given [block] as the argument.
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Use Constructor(BsonDocument { }) instead")
@JvmName("invokeWithBsonDocumentBlock")
operator fun <T> ((BsonDocument) -> T).invoke(block: BsonDocumentBlock): T {
    return this(BsonDocument(block))
}

/**
 * Invoke this function with the given [block] as the argument.
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Use Constructor(BsonArray { }) instead")
@JvmName("invokeWithBsonArrayBlock")
operator fun <T> ((BsonArray) -> T).invoke(block: BsonArrayBlock): T {
    return this(BsonArray(block))
}

/* ============= ------------------ ============= */
