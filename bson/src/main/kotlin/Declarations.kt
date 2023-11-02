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

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.cufy.bson.internal.IdSerializer
import org.intellij.lang.annotations.Language
import java.util.*

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
 * A globally unique identifier for objects.
 *
 * @see org.bson.types.ObjectId
 * @since 2.0.0
 */
typealias ObjectId = org.bson.types.ObjectId

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

/**
 * Enumeration of the supported [BsonElement] types.
 *
 * @see org.bson.BsonType
 * @since 2.0.0
 */
enum class BsonType(val value: Int) {
    //    EndOfDocument(0x00),

    /**
     * The type for [BsonDouble].
     *
     * @see org.bson.BsonType.DOUBLE
     * @since 2.0.0
     */
    Double(0x01),

    /**
     * The type for [BsonString].
     *
     * @see org.bson.BsonType.STRING
     * @since 2.0.0
     */
    String(0x02),

    /**
     * The type for [BsonDocument].
     *
     * @see org.bson.BsonType.DOCUMENT
     * @since 2.0.0
     */
    Document(0x03),

    /**
     * The type for [BsonArray].
     *
     * @see org.bson.BsonType.ARRAY
     * @since 2.0.0
     */
    Array(0x04),

    //    Binary(0x05),

    /**
     * The type for [BsonUndefined].
     *
     * @see org.bson.BsonType.UNDEFINED
     * @since 2.0.0
     */
    Undefined(0x06),

    /**
     * The type for [BsonObjectId].
     *
     * @see org.bson.BsonType.OBJECT_ID
     * @since 2.0.0
     */
    ObjectId(0x07),

    /**
     * The type for [BsonBoolean].
     *
     * @see org.bson.BsonType.BOOLEAN
     * @since 2.0.0
     */
    Boolean(0x08),

    /**
     * The type for [BsonDateTime].
     *
     * @see org.bson.BsonType.DATE_TIME
     * @since 2.0.0
     */
    DateTime(0x09),

    /**
     * The type for [BsonNull].
     *
     * @see org.bson.BsonType.NULL
     * @since 2.0.0
     */
    Null(0x0a),

    /**
     * The type for [BsonRegExp].
     *
     * @see org.bson.BsonType.REGULAR_EXPRESSION
     * @since 2.0.0
     */
    RegExp(0x0b),

    //    DbPointer(0x0c),
    //    Javascript(0x0d),
    //    Symbol(0x0e),
    //    JavascriptWithScope(0x0f),

    /**
     * The type for [BsonInt32].
     *
     * @see org.bson.BsonType.INT32
     * @since 2.0.0
     */
    Int32(0x10),

    //    Timestamp(0x11),

    /**
     * The type for [BsonInt64].
     *
     * @see org.bson.BsonType.INT64
     * @since 2.0.0
     */
    Int64(0x12),

    /**
     * The type for [BsonDecimal128].
     *
     * @see org.bson.BsonType.DECIMAL128
     * @since 2.0.0
     */
    Decimal128(0x13),

    //    MinKey(0xff),
    //    MaxKey(0x7f)
}

/* ============= ------------------ ============= */

/**
 * Base class for any BSON type.
 *
 * This class is meant to be immutable.
 * If a way to mutate an instance of this class is
 * found. The behaviour of the instance is undefined
 * and that way will not be guaranteed to even work
 * in different versions.
 *
 * @see org.bson.BsonValue
 * @since 2.0.0
 */
sealed interface BsonElement {
    /**
     * Return the type of this element.
     *
     * @since 2.0.0
     */
    val type: BsonType
}

/**
 * Construct a new empty bson array.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun BsonArray(): BsonArray {
    return BsonArray.Empty
}

/**
 * Construct a new bson array using the given
 * builder [block].
 *
 * **Warning: mutating the instance provided by the
 * given [block] after the execution of this
 * function will result to an undefined behaviour.**
 */
fun BsonArray(block: BsonArrayBlock): BsonArray {
    val content = mutableBsonListOf()
    content.apply(block)
    return BsonArray(content)
}

/**
 * Construct a new bson array with the given [elements].
 *
 * **Warning: mutating the given array [elements]
 * after the execution of this function will result
 * to an undefined behaviour.**
 */
fun BsonArray(vararg elements: BsonElement): BsonArray {
    return BsonArray(elements.toList())
}

/**
 * A block of code building a bson array.
 *
 * @since 2.0.0
 */
typealias BsonArrayBlock = MutableBsonList.() -> Unit

/**
 * A type-safe representation of the BSON array type.
 *
 * This class is meant to be immutable.
 * If a way to mutate an instance of this class is
 * found. The behaviour of the instance is undefined
 * and that way will not be guaranteed to even work
 * in different versions.
 *
 * @see org.bson.BsonArray
 * @since 2.0.0
 */
class BsonArray internal constructor(
    private val content: BsonList
) : BsonElement, BsonList by content {
    companion object {
        /**
         * A global instance of [BsonArray] that has no items.
         *
         * @since 2.0.0
         */
        val Empty = BsonArray(emptyList())
    }

    override val type: BsonType get() = BsonType.Array

    override fun equals(other: Any?) =
        content == other

    override fun hashCode() =
        content.hashCode()

    override fun toString() =
        content.joinToString(",", "[", "]")
}

/**
 * Construct a new empty bson document.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun BsonDocument(): BsonDocument {
    return BsonDocument.Empty
}

/**
 * Construct a new bson document using the given
 * builder [block].
 *
 * **Warning: mutating the instance provided by the
 * given [block] after the execution of this
 * function will result to an undefined behaviour.**
 */
fun BsonDocument(block: BsonDocumentBlock): BsonDocument {
    val content = mutableBsonMapOf()
    content.apply(block)
    return BsonDocument(content)
}

/**
 * Construct a new bson document with the given [pairs].
 *
 * **Warning: mutating the given array [pairs]
 * after the execution of this function will result
 * to an undefined behaviour.**
 */
fun BsonDocument(vararg pairs: Pair<String, BsonElement>): BsonDocument {
    return BsonDocument(pairs.toMap())
}

/**
 * A block of code building a bson document.
 *
 * @since 2.0.0
 */
typealias BsonDocumentBlock = MutableBsonMap.() -> Unit

/**
 * A type-safe container for a BSON document.
 *
 * This class is meant to be immutable.
 * If a way to mutate an instance of this class is
 * found. The behaviour of the instance is undefined
 * and that way will not be guaranteed to even work
 * in different versions.
 *
 * @see org.bson.BsonDocument
 * @since 2.0.0
 */
class BsonDocument internal constructor(
    private val content: BsonMap
) : BsonElement, BsonMap by content {
    companion object {
        /**
         * A global instance of [BsonDocument] that has no entries.
         *
         * @since 2.0.0
         */
        val Empty = BsonDocument(emptyMap())
    }

    override val type: BsonType get() = BsonType.Document

    override fun equals(other: Any?) =
        content == other

    override fun hashCode() =
        content.hashCode()

    override fun toString() =
        content.entries.joinToString(",", "{", "}") {
            """"${it.key}":${it.value}"""
        }
}

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

/**
 * Return a [BsonDateTime] with the given [value].
 *
 * @since 2.0.0
 */
fun BsonDateTime(value: Date): BsonDateTime {
    return BsonDateTime(value.time)
}

/**
 * Return a [BsonDateTime] with the given [value].
 *
 * @since 2.0.0
 */
fun BsonDateTime(value: Instant): BsonDateTime {
    return BsonDateTime(value.toEpochMilliseconds())
}

/**
 * A representation of the BSON DateTime type.
 *
 * @see org.bson.BsonDateTime
 * @since 2.0.0
 */
data class BsonDateTime(val value: Long) : BsonElement {
    override val type: BsonType get() = BsonType.DateTime

    override fun equals(other: Any?) =
        other is BsonDateTime && other.value == value

    override fun hashCode() =
        value.hashCode()

    override fun toString() =
        Date(value).toString()
}

/**
 * A representation of the BSON String type.
 *
 * @see org.bson.BsonString
 * @since 2.0.0
 */
data class BsonString(val value: String) : BsonElement {
    override val type: BsonType get() = BsonType.String

    override fun equals(other: Any?) =
        other is BsonString && other.value == value

    override fun hashCode() =
        value.hashCode()

    override fun toString() =
        """"$value""""
}

/**
 * Return a [BsonRegExp] with the given [pattern] and [options].
 *
 * @since 2.0.0
 */
fun BsonRegExp(@Language("RegExp") pattern: String, options: String): BsonRegExp {
    return BsonRegExp(pattern, options.toSortedSet())
}

/**
 * A holder class for a BSON regular expression,
 * so that we can delay compiling into a Pattern
 * until necessary.
 *
 * @see org.bson.BsonRegularExpression
 * @since 2.0.0
 */
data class BsonRegExp(@Language("RegExp") val pattern: String, val options: Set<Char> = emptySet()) : BsonElement {
    override val type: BsonType get() = BsonType.RegExp

    override fun equals(other: Any?) =
        other is BsonRegExp && other.pattern == pattern && other.options == options

    override fun hashCode() =
        31 * pattern.hashCode() + options.hashCode()

    override fun toString() =
        "/$pattern/${options.joinToString("")}"
}

/**
 * A representation of the BSON ObjectId type.
 *
 * @see org.bson.BsonObjectId
 * @since 2.0.0
 */
data class BsonObjectId(val value: ObjectId = ObjectId()) : BsonElement {
    override val type: BsonType get() = BsonType.ObjectId

    override fun equals(other: Any?) =
        other is BsonObjectId && other.value == value

    override fun hashCode(): Int =
        value.hashCode()

    override fun toString(): String =
        """ObjectId("$value")"""
}

/**
 * Return a [BsonBoolean] representing the given [value].
 *
 * @see org.bson.BsonBoolean
 * @since 2.0.0
 */
fun BsonBoolean(value: Boolean): BsonBoolean {
    return if (value) BsonBoolean.True else BsonBoolean.False
}

/**
 * A representation of the BSON Boolean type.
 *
 * @see org.bson.BsonBoolean
 * @since 2.0.0
 */
sealed interface BsonBoolean : BsonElement {
    override val type: BsonType get() = BsonType.Boolean

    val value: Boolean

    object True : BsonBoolean {
        override val value = true

        override fun hashCode() = 1
        override fun toString() = "true"
    }

    object False : BsonBoolean {
        override val value = false

        override fun hashCode() = 0
        override fun toString() = "false"
    }
}

/**
 * A representation of the BSON Null type.
 *
 * @see org.bson.BsonNull
 * @since 2.0.0
 */
object BsonNull : BsonElement {
    override val type: BsonType get() = BsonType.Null

    override fun hashCode() = 0
    override fun toString() = "null"
}

/**
 * Represents the value associated with the BSON Undefined type.
 *
 * @see org.bson.BsonUndefined
 * @since 2.0.0
 */
object BsonUndefined : BsonElement {
    override val type: BsonType get() = BsonType.Undefined

    override fun hashCode() = 0
    override fun toString() = "undefined"
}

/* ============= ------------------ ============= */
