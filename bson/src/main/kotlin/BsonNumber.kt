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

import java.math.BigDecimal

/* ============= ------------------ ============= */

/**
 * A binary integer decimal representation of a
 * 128-bit decimal value, supporting 34 decimal
 * digits of significand and an exponent range
 * of -6143 to +6144.
 *
 * @see org.bson.types.Decimal128
 * @since 2.0.0
 */
typealias Decimal128 = org.bson.types.Decimal128

/**
 * Base class for the numeric BSON types.
 * This class mirrors the functionality provided by [Number].
 *
 * @see org.bson.BsonNumber
 * @since 2.0.0
 */
sealed interface BsonNumber : BsonElement {
    val value: Number

    /**
     * Returns the value of the specified number
     * as an [Int], which may involve rounding or
     * truncation.
     */
    fun toInt(): Int

    /**
     * Returns the value of the specified number
     * as an [Long], which may involve rounding or
     * truncation.
     */
    fun toLong(): Long

    /**
     * Returns the value of the specified number
     * as a [Double], which may involve rounding.
     */
    fun toDouble(): Double

    /**
     * Returns the value of the specified number
     * as a [Decimal128], which may involve rounding.
     */
    fun toDecimal128(): Decimal128
}

/**
 * A representation of the BSON Int32 type.
 *
 * @see org.bson.BsonInt32
 * @since 2.0.0
 */
data class BsonInt32(override val value: Int) : BsonElement, BsonNumber {
    override val type: BsonType get() = BsonType.Int32

    override fun toInt() = value
    override fun toLong() = value.toLong()
    override fun toDouble() = value.toDouble()
    override fun toDecimal128() = value.toDecimal128()

    override fun equals(other: Any?) =
        other is BsonInt32 && other.value == value

    override fun hashCode() =
        value

    override fun toString() =
        value.toString()
}

/**
 * A representation of the BSON Int64 type.
 *
 * @see org.bson.BsonInt64
 * @since 2.0.0
 */
data class BsonInt64(override val value: Long) : BsonElement, BsonNumber {
    override val type: BsonType get() = BsonType.Int64

    override fun toInt() = value.toInt()
    override fun toLong() = value
    override fun toDouble() = value.toDouble()
    override fun toDecimal128() = value.toDecimal128()

    override fun equals(other: Any?) =
        other is BsonInt64 && other.value == value

    override fun hashCode() =
        value.hashCode()

    override fun toString() =
        value.toString()
}

/**
 * A representation of the BSON Double type.
 *
 * @see org.bson.BsonDouble
 * @since 2.0.0
 */
data class BsonDouble(override val value: Double) : BsonElement, BsonNumber {
    override val type: BsonType get() = BsonType.Double

    override fun toInt() = value.toInt()
    override fun toLong() = value.toLong()
    override fun toDouble() = value
    override fun toDecimal128() = value.toDecimal128()

    override fun equals(other: Any?) =
        other is BsonDouble && other.value == value

    override fun hashCode() =
        value.hashCode()

    override fun toString() =
        value.toString()
}

/**
 * A representation of the BSON Decimal128 type.
 *
 * @see org.bson.BsonDecimal128
 * @since 2.0.0
 */
data class BsonDecimal128(override val value: Decimal128) : BsonElement, BsonNumber {
    override val type: BsonType get() = BsonType.Decimal128

    override fun toInt() = value.toBigDecimal().toInt()
    override fun toLong() = value.toBigDecimal().toLong()
    override fun toDouble() = value.toBigDecimal().toDouble()
    override fun toDecimal128() = value

    override fun equals(other: Any?) =
        other is BsonDecimal128 && other.value == value

    override fun hashCode() =
        value.hashCode()

    override fun toString() =
        value.toString()
}

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
 * Return a [BsonInt32] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val Int.b: BsonInt32 get() = bson

/**
 * Return a [BsonInt64] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val Long.b: BsonInt64 get() = bson

/**
 * Return a [BsonDouble] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val Double.b: BsonDouble get() = bson

/**
 * Return a [BsonDecimal128] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val Decimal128.b: BsonDecimal128 get() = bson

/**
 * Return a [BsonDecimal128] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val BigDecimal.b: BsonDecimal128 get() = bson

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

/* ============= ------------------ ============= */
