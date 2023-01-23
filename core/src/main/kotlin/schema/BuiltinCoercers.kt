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
import org.cufy.monkt.*
import org.cufy.monkt.internal.*
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
 * The block of [DeterministicCoercer].
 */
typealias DeterministicCoercerBlock<T> =
        DeterministicCoercerScope<T>.(BsonValue) -> Unit

/**
 * A scope for [DeterministicCoercerBlock].
 *
 * @author LSafer
 * @since 2.0.0
 */
class DeterministicCoercerScope<T> {
    /**
     * The coerced value. Or null if coercion fail.
     */
    internal var value: Lazy<T>? = null

    /**
     * Indicate coercion success with the coercion
     * result being the result of invoking the
     * given [block].
     */
    fun decodeTo(block: () -> T) {
        value = lazy(block)
    }
}

/**
 * Construct a coercer to be used to safely create
 * a coercer with only one [block].
 *
 * @param block the coercing block.
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
@Suppress("FunctionName")
fun <T> DeterministicCoercer(
    block: DeterministicCoercerBlock<T>
): Coercer<T> {
    return DeterministicCoercerImpl(block)
}

/*==================================================
===================== Built In =====================
==================================================*/

// String Boolean Int32 Int64 Double Decimal128 ObjectId
// Id BigDecimal

/**
 * Standard coercing algorithm for type [String].
 */
val StandardStringCoercer: ScalarCoercer<String> = ScalarCoercer {
    expect(BsonStringType, BsonBooleanType, BsonInt32Type, BsonInt64Type, BsonDoubleType, BsonDecimal128Type, BsonObjectIdType)
    deterministic {
        when (it) {
            is BsonString -> decodeTo { it.value }
            is BsonBoolean -> decodeTo { it.value.toString() }
            is BsonInt32 -> decodeTo { it.value.toString() }
            is BsonInt64 -> decodeTo { it.value.toString() }
            is BsonDouble -> decodeTo { it.value.toString() }
            is BsonDecimal128 -> decodeTo { it.value.toString() }
            is BsonObjectId -> decodeTo { it.value.toHexString() }
        }
    }
}

/**
 * Standard coercing algorithm for type [Boolean]
 */
val StandardBooleanCoercer: ScalarCoercer<Boolean> = ScalarCoercer {
    expect(BsonStringType, BsonBooleanType)
    deterministic {
        when (it) {
            is BsonString -> it.value.toBooleanStrictOrNull()?.let { decodeTo { it } }
            is BsonBoolean -> decodeTo { it.value }
        }
    }
}

/**
 * Standard coercing algorithm for type [Int]
 */
val StandardInt32Coercer: ScalarCoercer<Int> = ScalarCoercer {
    expect(BsonStringType, BsonInt32Type, BsonInt64Type, BsonDoubleType, BsonDecimal128Type)
    deterministic {
        when (it) {
            is BsonString -> it.value.toIntOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value }
            is BsonInt64 -> decodeTo { it.value.toInt() }
            is BsonDouble -> decodeTo { it.value.roundToInt() }
            is BsonDecimal128 -> decodeTo { it.value.toInt() }
        }
    }
}

/**
 * Standard coercing algorithm for type [Long]
 */
val StandardInt64Coercer: ScalarCoercer<Long> = ScalarCoercer {
    expect(BsonStringType, BsonInt32Type, BsonInt64Type, BsonDoubleType, BsonDecimal128Type)
    deterministic {
        when (it) {
            is BsonString -> it.value.toLongOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value.toLong() }
            is BsonInt64 -> decodeTo { it.value }
            is BsonDouble -> decodeTo { it.value.roundToLong() }
            is BsonDecimal128 -> decodeTo { it.value.toLong() }
        }
    }
}

/**
 * Standard coercing algorithm for type [Double]
 */
val StandardDoubleCoercer: ScalarCoercer<Double> = ScalarCoercer {
    expect(BsonStringType, BsonInt32Type, BsonInt64Type, BsonDoubleType, BsonDecimal128Type)
    deterministic {
        when (it) {
            is BsonString -> it.value.toDoubleOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value.toDouble() }
            is BsonInt64 -> decodeTo { it.value.toDouble() }
            is BsonDouble -> decodeTo { it.value }
            is BsonDecimal128 -> decodeTo { it.value.toDouble() }
        }
    }
}

/**
 * Standard coercing algorithm for type [Decimal128]
 */
val StandardDecimal128Coercer: ScalarCoercer<Decimal128> = ScalarCoercer {
    expect(BsonStringType, BsonInt32Type, BsonInt64Type, BsonDoubleType, BsonDecimal128Type)
    deterministic {
        when (it) {
            is BsonString -> it.value.toBigDecimalOrNull()?.let { decodeTo { Decimal128(it) } }
            is BsonInt32 -> decodeTo { Decimal128(it.value.toBigDecimal()) }
            is BsonInt64 -> decodeTo { Decimal128(it.value.toBigDecimal()) }
            is BsonDouble -> decodeTo { Decimal128(it.value.toBigDecimal()) }
            is BsonDecimal128 -> decodeTo { it.value }
        }
    }
}

/**
 * Standard coercing algorithm for type [ObjectId]
 */
val StandardObjectIdCoercer: ScalarCoercer<ObjectId> = ScalarCoercer {
    expect(BsonStringType, BsonObjectIdType)
    deterministic {
        when (it) {
            is BsonString -> {
                if (ObjectId.isValid(it.value))
                    decodeTo { ObjectId(it.value) }
            }
            is BsonObjectId -> decodeTo { it.value }
        }
    }
}

/**
 * A schema for [Id] and both [BsonObjectId] and [BsonString].
 *
 * @since 2.0.0
 */
val StandardIdCoercer: ScalarCoercer<Id<Any>> = StandardIdCoercer()

/**
 * A schema for [Id] and both [BsonObjectId] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun <T> StandardIdCoercer(): ScalarCoercer<Id<T>> = ScalarCoercer {
    expect(BsonObjectIdType, BsonStringType)
    deterministic {
        when (it) {
            is BsonString -> decodeTo { Id(it.value) }
            is BsonObjectId -> decodeTo { Id(it.value) }
        }
    }
}

/**
 * Standard coercing algorithm for type [BigDecimal]
 */
val StandardBigDecimalCoercer: ScalarCoercer<BigDecimal> = ScalarCoercer {
    expect(BsonStringType, BsonInt32Type, BsonInt64Type, BsonDoubleType, BsonDecimal128Type)
    deterministic {
        when (it) {
            is BsonString -> it.value.toBigDecimalOrNull()?.let { decodeTo { it } }
            is BsonInt32 -> decodeTo { it.value.toBigDecimal() }
            is BsonInt64 -> decodeTo { it.value.toBigDecimal() }
            is BsonDouble -> decodeTo { it.value.toBigDecimal() }
            is BsonDecimal128 -> decodeTo { it.value.bigDecimalValue() }
        }
    }
}

//

/**
 * More lenient coercing algorithm for type [Id].
 *
 * If `null` or `undefined` coerce to a new Id.
 */
val LenientIdCoercer: ScalarCoercer<Id<Any>> = LenientIdCoercer()

/**
 * More lenient coercing algorithm for type [Id].
 *
 * If `null` or `undefined` coerce to a new Id.
 */
@Suppress("FunctionName")
fun <T> LenientIdCoercer(): ScalarCoercer<Id<T>> = ScalarCoercer {
    expect(BsonStringType, BsonObjectIdType, BsonUndefinedType, BsonNullType)
    deterministic {
        when (it) {
            is BsonString -> decodeTo { Id(it.value) }
            is BsonObjectId -> decodeTo { Id(it.value) }
            is BsonUndefined, is BsonNull -> decodeTo { Id() }
        }
    }
}
