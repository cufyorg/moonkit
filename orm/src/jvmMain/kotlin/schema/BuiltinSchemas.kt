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
import org.cufy.monkt.AdvancedMonktApi
import org.cufy.monkt.InternalMonktApi
import org.cufy.monkt.internal.EnumSchemaBuilderImpl
import org.cufy.monkt.internal.NullableSchemaImpl
import java.math.BigDecimal
import kotlin.collections.set
import kotlin.reflect.KClass

/*==================================================
==================== EnumSchema ====================
==================================================*/

/**
 * A schema for mapping runtime enums to bson values.
 *
 * @param T the type of the runtime enum.
 * @since 2.0.0
 */
interface EnumSchema<T> : ScalarSchema<T> {
    /**
     * Name / Runtime-Enum table.
     *
     * @since 2.0.0
     */
    val values: Map<BsonElement, T>
}

/**
 * A block of code invoked to fill in options in
 * [EnumSchemaBuilder].
 */
typealias EnumSchemaBuilderBlock<T> =
        EnumSchemaBuilder<T>.() -> Unit

/**
 * A builder for creating an [EnumSchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
interface EnumSchemaBuilder<T> :
    WithDeferredBuilder {

    /**
     * Name / Runtime-Enum table.
     *
     * @since 2.0.0
     */
    @AdvancedMonktApi("Use `value()` instead")
    val values: MutableMap<BsonElement, T>

    /**
     * Build the schema.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun build(): EnumSchema<T>
}

/**
 * Obtain a new [EnumSchemaBuilder].
 *
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T> EnumSchemaBuilder(): EnumSchemaBuilder<T> {
    return EnumSchemaBuilderImpl()
}

/**
 * Construct a new [EnumSchema] with the given
 * [block]
 *
 * @param block the builder block.
 * @return a new enum schema.
 * @since 2.0.0
 */
fun <T> EnumSchema(
    values: Map<BsonElement, T>? = null,
    block: EnumSchemaBuilderBlock<T> = {}
): EnumSchema<T> {
    val builder = EnumSchemaBuilder<T>()
    values?.let { builder.values(values) }
    builder.apply(block)
    return builder.build()
}

/**
 * Construct a new [EnumSchema] with the given
 * [block]
 *
 * @param block the builder block.
 * @return a new enum schema.
 * @since 2.0.0
 */
fun <T : Enum<T>> EnumSchema(
    enumClass: KClass<T>,
    block: EnumSchemaBuilderBlock<T> = {}
): EnumSchema<T> {
    val builder = EnumSchemaBuilder<T>()
    builder.values(enumClass)
    builder.apply(block)
    return builder.build()
}

//

/**
 * Add the values of the given [map].
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> EnumSchemaBuilder<T>.values(
    map: Map<BsonElement, T>
) {
    values += map
}

/**
 * Add the values of the given [enumClass].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Enum<T>> EnumSchemaBuilder<T>.values(
    enumClass: KClass<T>
) {
    values += enumClass.java.enumConstants.associateBy { BsonString(it.name) }
}

// values

/**
 * Add the given [key] and [value].
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> EnumSchemaBuilder<T>.value(
    key: BsonElement, value: T
) {
    values[key] = value
}

/**
 * Add the given [key] and [value].
 *
 * @since 2.0.0
 */
fun <T> EnumSchemaBuilder<T>.value(
    key: String, value: T
) {
    value(BsonString(key), value)
}

/**
 * Add the given [key] and [value].
 *
 * @since 2.0.0
 */
fun <T> EnumSchemaBuilder<T>.value(
    key: Int, value: T
) {
    value(BsonInt32(key), value)
}

/**
 * Add the given [enum].
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Enum<T>> EnumSchemaBuilder<T>.value(
    enum: T
) {
    values[BsonString(enum.name)] = enum
}

/*==================================================
================== NullableSchema ==================
==================================================*/

/**
 * A schema wrapper for nullable values.
 *
 * Wraps another schema to be nullable.
 *
 * @param <T> the type of teh wrapped schema.
 * @since 2.0.0
 */
interface NullableSchema<T> : ElementSchema<T?> {
    /**
     * The wrapped schema.
     */
    val schema: Schema<T>
}

/**
 * Construct a new nullable schema wrapping the
 * given [schema].
 *
 * @param schema the wrapped schema.
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T> NullableSchema(
    schema: Schema<T>
): NullableSchema<T> {
    return NullableSchemaImpl(schema)
}

/*==================================================
===================== Built In =====================
==================================================*/

// String Boolean Int32 Int64 Double Decimal128 ObjectId
// Id BigDecimal

/**
 * The schema for [String] and [BsonString].
 *
 * @since 2.0.0
 */
val StringSchema: ScalarSchema<String> = ScalarSchema {
    canDecode(BsonType.String)
    canEncode { it is String }
    encode { BsonString(it) }
    decode {
        it as BsonString
        it.value
    }
}

/**
 * A schema for [Boolean] and [BsonBoolean].
 *
 * @since 2.0.0
 */
val BooleanSchema: ScalarSchema<Boolean> = ScalarSchema {
    canDecode(BsonType.Boolean)
    canEncode { it is Boolean }
    encode { BsonBoolean(it) }
    decode {
        it as BsonBoolean
        it.value
    }
}

/**
 * A schema for [Int] and [BsonInt32].
 *
 * @since 2.0.0
 */
val Int32Schema: ScalarSchema<Int> = ScalarSchema {
    canDecode(BsonType.Int32)
    canEncode { it is Int }
    encode { BsonInt32(it) }
    decode {
        it as BsonInt32
        it.value
    }
}

/**
 * A schema for [Long] and [BsonInt64].
 *
 * @since 2.0.0
 */
@Suppress("USELESS_IS_CHECK")
val Int64Schema: ScalarSchema<Long> = ScalarSchema {
    canDecode(BsonType.Int64)
    canEncode { it is Long }
    encode { BsonInt64(it) }
    decode {
        it as BsonInt64
        it.value
    }
}

/**
 * A schema for [Double] and [BsonDouble].
 *
 * @since 2.0.0
 */
val DoubleSchema: ScalarSchema<Double> = ScalarSchema {
    canDecode(BsonType.Double)
    canEncode { it is Double }
    encode { BsonDouble(it) }
    decode {
        it as BsonDouble
        it.value
    }
}

/**
 * A schema for [Decimal128] and [BsonDecimal128].
 *
 * @since 2.0.0
 */
val Decimal128Schema: ScalarSchema<Decimal128> = ScalarSchema {
    canDecode(BsonType.Decimal128)
    canEncode { it is Decimal128 }
    encode { BsonDecimal128(it) }
    decode {
        it as BsonDecimal128
        it.value
    }
}

/**
 * A schema for [ObjectId] and [BsonObjectId].
 *
 * @since 2.0.0
 */
val ObjectIdSchema: ScalarSchema<ObjectId> = ScalarSchema {
    canDecode(BsonType.ObjectId)
    canEncode { it is ObjectId }
    encode { BsonObjectId(it) }
    decode {
        it as BsonObjectId
        it.value
    }
}

/**
 * A schema for [Id] and both [BsonObjectId] and [BsonString].
 *
 * @since 2.0.0
 */
val IdSchema: ScalarSchema<Id<Any>> = IdSchema()

/**
 * A schema for [Id] and both [BsonObjectId] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun <T> IdSchema(): ScalarSchema<Id<T>> = ScalarSchema {
    canDecode(BsonType.ObjectId, BsonType.String)
    canEncode { it is Id<*> }
    encode {
        when {
            ObjectId.isValid(it.value) -> BsonObjectId(ObjectId(it.value))
            else -> BsonString(it.value)
        }
    }
    decode {
        when (it) {
            is BsonString -> Id(it.value)
            is BsonObjectId -> Id(it.value)
            else -> throw IllegalArgumentException(
                "IdSchema expected ${BsonType.ObjectId} or ${BsonType.String} but got $it"
            )
        }
    }
}

/**
 * A schema for [BigDecimal] and [BsonDecimal128].
 *
 * @since 2.0.0
 */
val BigDecimalSchema: ScalarSchema<BigDecimal> = ScalarSchema {
    canDecode(BsonType.Decimal128)
    canEncode { it is BigDecimal }
    encode { BsonDecimal128(Decimal128(it)) }
    decode {
        it as BsonDecimal128
        it.value.toBigDecimal()
    }
}
