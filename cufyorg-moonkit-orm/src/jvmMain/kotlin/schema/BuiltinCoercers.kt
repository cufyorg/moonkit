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
package org.cufy.monkt.schema

import org.cufy.bson.*
import org.cufy.monkt.internal.DeterministicDecoderImpl
import java.math.BigDecimal
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/*==================================================
================== ScalarCoercer  ==================
==================================================*/

/*==================================================
=============== DeterministicCoercer ===============
==================================================*/

/**
 * The block of [DeterministicDecoder].
 */
typealias DeterministicDecoderBlock<T> =
        DeterministicDecoderScope<T>.(BsonElement) -> Unit

/**
 * A scope for [DeterministicDecoderBlock].
 *
 * @author LSafer
 * @since 2.0.0
 */
class DeterministicDecoderScope<T> {
    /**
     * The decoded value. Or null if decoding fail.
     */
    private var lazyValue: Lazy<T>? = null

    /**
     * Indicate decoding success with the decoding
     * result being the result of invoking the
     * given [block].
     */
    fun decodeTo(block: () -> T) {
        lazyValue = lazy(block)
    }

    /**
     * Check if the decoding succeeded.
     */
    val isDecoded: Boolean
        get() = lazyValue != null

    /**
     * Return the decoded value.
     */
    val decodedValue: T
        get() = lazyValue
            .let { it ?: error("Deterministic Decoding Failed") }
            .value
}

/**
 * Construct a decoder to be used to safely create
 * a decoder with only one [block].
 *
 * @param block the decoding block.
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun <T> DeterministicDecoder(
    block: DeterministicDecoderBlock<T>
): Decoder<T> {
    return DeterministicDecoderImpl(block)
}

/*==================================================
===================== Built In =====================
==================================================*/

// String Boolean Int32 Int64 Double Decimal128 ObjectId
// ID BigDecimal

/**
 * Standard decoding algorithm for type [String].
 */
val StandardStringDecoder: ScalarDecoder<String> = ScalarDecoder {
    expect(BsonType.String, BsonType.Boolean, BsonType.Int32, BsonType.Int64, BsonType.Double, BsonType.Decimal128, BsonType.ObjectId)
    deterministic {
        when (it) {
            is BsonString -> decodeTo { it.value }
            is BsonBoolean -> decodeTo { it.value.toString() }
            is BsonInt32 -> decodeTo { it.value.toString() }
            is BsonInt64 -> decodeTo { it.value.toString() }
            is BsonDouble -> decodeTo { it.value.toString() }
            is BsonDecimal128 -> decodeTo { it.value.toString() }
            is BsonObjectId -> decodeTo { it.value.toHexString() }
            else -> {}
        }
    }
}

/**
 * Standard decoding algorithm for type [Boolean]
 */
val StandardBooleanDecoder: ScalarDecoder<Boolean> = ScalarDecoder {
    expect(BsonType.String, BsonType.Boolean)
    deterministic {
        when (it) {
            is BsonString -> it.value.toBooleanStrictOrNull()?.let { decodeTo { it } }
            is BsonBoolean -> decodeTo { it.value }
            else -> {}
        }
    }
}

/**
 * Standard decoding algorithm for type [Int]
 */
val StandardInt32Decoder: ScalarDecoder<Int> = ScalarDecoder {
    expect(BsonType.String, BsonType.Int32, BsonType.Int64, BsonType.Double, BsonType.Decimal128)
    deterministic {
        when (it) {
            is BsonString -> it.value.toIntOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value }
            is BsonInt64 -> decodeTo { it.value.toInt() }
            is BsonDouble -> decodeTo { it.value.roundToInt() }
            is BsonDecimal128 -> decodeTo { it.value.toInt() }
            else -> {}
        }
    }
}

/**
 * Standard decoding algorithm for type [Long]
 */
val StandardInt64Decoder: ScalarDecoder<Long> = ScalarDecoder {
    expect(BsonType.String, BsonType.Int32, BsonType.Int64, BsonType.Double, BsonType.Decimal128)
    deterministic {
        when (it) {
            is BsonString -> it.value.toLongOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value.toLong() }
            is BsonInt64 -> decodeTo { it.value }
            is BsonDouble -> decodeTo { it.value.roundToLong() }
            is BsonDecimal128 -> decodeTo { it.value.toLong() }
            else -> {}
        }
    }
}

/**
 * Standard decoding algorithm for type [Double]
 */
val StandardDoubleDecoder: ScalarDecoder<Double> = ScalarDecoder {
    expect(BsonType.String, BsonType.Int32, BsonType.Int64, BsonType.Double, BsonType.Decimal128)
    deterministic {
        when (it) {
            is BsonString -> it.value.toDoubleOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value.toDouble() }
            is BsonInt64 -> decodeTo { it.value.toDouble() }
            is BsonDouble -> decodeTo { it.value }
            is BsonDecimal128 -> decodeTo { it.value.toDouble() }
            else -> {}
        }
    }
}

/**
 * Standard decoding algorithm for type [Decimal128]
 */
val StandardDecimal128Decoder: ScalarDecoder<Decimal128> = ScalarDecoder {
    expect(BsonType.String, BsonType.Int32, BsonType.Int64, BsonType.Double, BsonType.Decimal128)
    deterministic {
        when (it) {
            is BsonString -> it.value.toBigDecimalOrNull()?.let { decodeTo { Decimal128(it) } }
            is BsonInt32 -> decodeTo { Decimal128(it.value.toBigDecimal()) }
            is BsonInt64 -> decodeTo { Decimal128(it.value.toBigDecimal()) }
            is BsonDouble -> decodeTo { Decimal128(it.value.toBigDecimal()) }
            is BsonDecimal128 -> decodeTo { it.value }
            else -> {}
        }
    }
}

/**
 * Standard decoding algorithm for type [ObjectId]
 */
val StandardObjectIdDecoder: ScalarDecoder<ObjectId> = ScalarDecoder {
    expect(BsonType.String, BsonType.ObjectId)
    deterministic {
        when (it) {
            is BsonString -> {
                if (ObjectId.isValid(it.value))
                    decodeTo { ObjectId(it.value) }
            }
            is BsonObjectId -> decodeTo { it.value }
            else -> {}
        }
    }
}

/**
 * A schema for [ID] and both [BsonObjectId] and [BsonString].
 *
 * @since 2.0.0
 */
val StandardIdDecoder: ScalarDecoder<ID<Any>> = StandardIdDecoder()

/**
 * A schema for [ID] and both [BsonObjectId] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun <T> StandardIdDecoder(): ScalarDecoder<ID<T>> = ScalarDecoder {
    expect(BsonType.ObjectId, BsonType.String)
    deterministic {
        when (it) {
            is BsonString -> decodeTo { ID(it.value) }
            is BsonObjectId -> decodeTo { ID(it.value) }
            else -> {}
        }
    }
}

/**
 * Standard decoding algorithm for type [BigDecimal]
 */
val StandardBigDecimalDecoder: ScalarDecoder<BigDecimal> = ScalarDecoder {
    expect(BsonType.String, BsonType.Int32, BsonType.Int64, BsonType.Double, BsonType.Decimal128)
    deterministic {
        when (it) {
            is BsonString -> it.value.toBigDecimalOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value.toBigDecimal() }
            is BsonInt64 -> decodeTo { it.value.toBigDecimal() }
            is BsonDouble -> decodeTo { it.value.toBigDecimal() }
            is BsonDecimal128 -> decodeTo { it.value.toBigDecimal() }
            else -> {}
        }
    }
}

//

/**
 * More lenient decoding algorithm for type [ID].
 *
 * If `null` or `undefined` decode to a new ID.
 */
val LenientIdDecoder: ScalarDecoder<ID<Any>> = LenientIdDecoder()

/**
 * More lenient decoding algorithm for type [ID].
 *
 * If `null` or `undefined` decode to a new ID.
 */
@Suppress("FunctionName")
fun <T> LenientIdDecoder(): ScalarDecoder<ID<T>> = ScalarDecoder {
    expect(BsonType.String, BsonType.ObjectId, BsonType.Undefined, BsonType.Null)
    deterministic {
        when (it) {
            is BsonString -> decodeTo { ID(it.value) }
            is BsonObjectId -> decodeTo { ID(it.value) }
            is BsonUndefined, is BsonNull -> decodeTo { ID() }
            else -> {}
        }
    }
}
