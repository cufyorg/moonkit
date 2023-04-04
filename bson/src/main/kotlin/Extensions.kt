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
 * Return the type of this element.
 *
 * @since 2.0.0
 */
val BsonElement.type: BsonType
    get() = when (this) {
        is BsonDocument -> BsonType.Document
        is BsonArray -> BsonType.Array
        is BsonString -> BsonType.String
        is BsonRegExp -> BsonType.RegExp
        is BsonObjectId -> BsonType.ObjectId
        is BsonInt32 -> BsonType.Int32
        is BsonInt64 -> BsonType.Int64
        is BsonDouble -> BsonType.Double
        is BsonDecimal128 -> BsonType.Decimal128
        is BsonBoolean -> BsonType.Boolean
        is BsonUndefined -> BsonType.Undefined
        is BsonNull -> BsonType.Null
    }

/* ============= ------------------ ============= */

/**
 * A global instance of [BsonArray] that has no items.
 *
 * @since 2.0.0
 */
val EmptyBsonArray = BsonArray()

/**
 * A global instance of [BsonDocument] that has no entries.
 *
 * @since 2.0.0
 */
val EmptyBsonDocument = BsonDocument()

/**
 * The global instance of bson true.
 */
@BsonKeywordMarker
val btrue = BsonBoolean.True

/**
 * The global instance of bson false.
 */
@BsonKeywordMarker
val bfalse = BsonBoolean.False

/**
 * The global instance of bson null.
 */
@BsonKeywordMarker
val bnull = BsonNull

/**
 * A global instance of bson undefined.
 */
@BsonKeywordMarker
val bundefined = BsonUndefined

/* ============= ------------------ ============= */

/**
 * Construct a new bson array from this list.
 */
fun List<BsonElement>.toBsonArray(): BsonArray {
    return BsonArray(toList())
}

/**
 * Construct a new mutable bson array from this list.
 */
fun List<BsonElement>.toMutableBsonArray(): MutableBsonArray {
    return MutableBsonArray(toMutableList())
}

/**
 * Construct a new bson document from this map.
 */
fun Map<String, BsonElement>.toBsonDocument(): BsonDocument {
    return BsonDocument(toMap())
}

/**
 * Construct a new mutable bson document from this map.
 */
fun Map<String, BsonElement>.toMutableBsonDocument(): MutableBsonDocument {
    return MutableBsonDocument(toMutableMap())
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonInt32] with the value of this.
 */
val Int.b: BsonInt32 get() = BsonInt32(this)

/**
 * Return a [BsonInt64] with the value of this.
 */
val Long.b: BsonInt64 get() = BsonInt64(this)

/**
 * Return a [BsonDouble] with the value of this.
 */
val Double.b: BsonDouble get() = BsonDouble(this)

/**
 * Return a [BsonDecimal128] with the value of this.
 */
val Decimal128.b: BsonDecimal128 get() = BsonDecimal128(this)

/**
 * Return a [BsonDecimal128] with the value of this.
 */
val BigDecimal.b: BsonDecimal128 get() = toDecimal128().b

/**
 * Return a [BsonString] with the value of this.
 */
val String.b: BsonString get() = BsonString(this)

/**
 * Return a [BsonObjectId] with the value of this.
 */
val ObjectId.b: BsonObjectId get() = BsonObjectId(this)

/**
 * Return the best fitting native wrapper for
 * this id.
 * A [BsonObjectId] if it is a valid object id
 * and a [BsonString] if it is not.
 *
 * @since 2.0.0
 */
val Id<*>.b: BsonElement
    get() {
        if (ObjectId.isValid(value))
            return BsonObjectId(ObjectId(value))

        return BsonString(value)
    }

/**
 * Return a [BsonBoolean] with the value of this.
 */
val Boolean.b: BsonBoolean get() = BsonBoolean(this)

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
