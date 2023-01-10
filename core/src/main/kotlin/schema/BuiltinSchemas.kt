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
import kotlin.reflect.KClass

/*==================================================
=================== ScalarSchema ===================
==================================================*/

/**
 * A schema for scalar values.
 *
 * @param T the type of the runtime value.
 * @since 2.0.0
 */
interface ScalarSchema<T> : Schema<T>, ScalarCoercer<T> {
    @Suppress("UnnecessaryOptInAnnotation")
    @OptIn(AdvancedMonktApi::class)
    override fun canDecode(bsonValue: BsonValue): Boolean

    @Suppress("UnnecessaryOptInAnnotation")
    @OptIn(AdvancedMonktApi::class)
    override fun decode(bsonValue: BsonValue): T

    @OptIn(AdvancedMonktApi::class)
    override fun encode(value: T): BsonValue
}

/**
 * A block of code invoked to fill in options in
 * [ScalarSchemaBuilder].
 */
typealias ScalarSchemaBuilderBlock<T> =
        ScalarSchemaBuilder<T>.() -> Unit

/**
 * A builder for creating a [ScalarSchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
interface ScalarSchemaBuilder<T> :
    WithDecoderBuilder<T>,
    WithDeferredBuilder {

    /**
     * The wrapped encoder
     */
    @AdvancedMonktApi("Use `encode()` instead")
    var encoder: Encoder<in T>? // REQUIRED

    /**
     * Build the schema.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun build(): ScalarSchema<T>
}

/**
 * Obtain a new [ScalarSchemaBuilder].
 *
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T> ScalarSchemaBuilder(): ScalarSchemaBuilder<T> {
    return ScalarSchemaBuilderImpl()
}

/**
 * Construct a new [ScalarSchema] with the given
 * [block]
 *
 * @param block the builder block.
 * @return a new scalar schema.
 * @since 2.0.0
 */
fun <T> ScalarSchema(
    block: ScalarSchemaBuilderBlock<T> = {}
): ScalarSchema<T> {
    val builder = ScalarSchemaBuilder<T>()
    builder.apply(block)
    return builder.build()
}

// encoder

/**
 * Set the encoder to be the given [encoder]
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> ScalarSchemaBuilder<T>.encode(
    encoder: Encoder<in T>
) {
    this.encoder = encoder
}

@OptIn(AdvancedMonktApi::class)
inline fun <reified T> ScalarSchemaBuilder<T>.encodeSafe(
    encoder: Encoder<in T>
) {
    this.encoder = Encoder<Any?> {
        require(T::class.isInstance(it)) {
            "Encoding Failure: Expected ${T::class.simpleName} but got $it"
        }
        encoder.encode(it as T)
    }
}

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
    val values: Map<BsonValue, T>
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
    val values: MutableMap<BsonValue, T>

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
    values: Map<BsonValue, T>? = null,
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
    map: Map<BsonValue, T>
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
    key: BsonValue, value: T
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
==================== MapSchema  ====================
==================================================*/

/**
 * A schema wrapper for mapping schema types.
 *
 * @param <T> the type of the wrapped schema.
 * @since 2.0.0
 */
interface MapSchema<T, U> : ElementSchema<T> {
    /**
     * The wrapped schema.
     */
    val schema: Schema<U>
}

/**
 * A block of code invoked to fill in options in
 * [MapSchemaBuilder].
 */
typealias MapSchemaBuilderBlock<T, U> =
        MapSchemaBuilder<T, U>.() -> Unit

/**
 * A builder for creating a [MapSchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
interface MapSchemaBuilder<T, U> :
    WithSchemaBuilder<U>,
    WithDeferredBuilder {

    /**
     * Transforms runtime values of type `T` to `U`
     */
    @AdvancedMonktApi("Use `encodeMapper()` instead")
    var encodeMapper: Mapper<T, U>?  // REQUIRED

    /**
     * Transforms runtime values of type `U` to `T`
     */
    @AdvancedMonktApi("Use `decodeMapper()` instead")
    var decodeMapper: Mapper<U, T>?  // REQUIRED

    /**
     * Transforms bson values of type `T` to `U`
     */
    @AdvancedMonktApi("Use `bsonEncodeMapper()` instead")
    var bsonEncodeMapper: BsonMapper<T, U>

    /**
     * Transforms bson values of type `U` to `T`
     */
    @AdvancedMonktApi("Use `bsonDecodeMapper()` instead")
    var bsonDecodeMapper: BsonMapper<U, T>

    /**
     * Build the schema.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun build(): MapSchema<T, U>
}

/**
 * Obtain a new [MapSchemaBuilder].
 *
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T, U> MapSchemaBuilder(): MapSchemaBuilder<T, U> {
    return MapSchemaBuilderImpl()
}

/**
 * Construct a new [MapSchema] with the given
 * [block]
 *
 * @param schema the initial schema.
 * @param block the builder block.
 * @return a new map schema.
 * @since 2.0.0
 */
fun <T, U> MapSchema(
    schema: Schema<U>? = null,
    encodeMapper: Mapper<T, U>? = null,
    decodeMapper: Mapper<U, T>? = null,
    bsonEncodeMapper: BsonMapper<T, U>? = null,
    bsonDecodeMapper: BsonMapper<U, T>? = null,
    block: MapSchemaBuilderBlock<T, U> = {}
): MapSchema<T, U> {
    val builder = MapSchemaBuilder<T, U>()
    schema?.let { builder.schema { it } }
    encodeMapper?.let { builder.encodeMapper(it) }
    decodeMapper?.let { builder.decodeMapper(it) }
    bsonEncodeMapper?.let { builder.bsonEncodeMapper(it) }
    bsonDecodeMapper?.let { builder.bsonDecodeMapper(it) }
    builder.apply(block)
    return builder.build()
}

// encodeMapper

/**
 * Set the given [block] to be the value encode
 * mapper.
 */
@OptIn(AdvancedMonktApi::class)
fun <T, U> MapSchemaBuilder<T, U>.encodeMapper(
    block: Mapper<T, U>
) {
    encodeMapper = block
}

// decodeMapper

/**
 * Set the given [block] to be the value decode
 * mapper.
 */
@OptIn(AdvancedMonktApi::class)
fun <T, U> MapSchemaBuilder<T, U>.decodeMapper(
    block: Mapper<U, T>
) {
    decodeMapper = block
}

// bsonEncodeMapper

/**
 * Set the given [block] to be the bson value
 * encode mapper.
 */
@OptIn(AdvancedMonktApi::class)
fun <T, U> MapSchemaBuilder<T, U>.bsonEncodeMapper(
    block: BsonMapper<T, U>
) {
    bsonEncodeMapper = block
}

// bsonDecodeMapper

/**
 * Set the given [block] to be the bson value
 * decode mapper.
 */
@OptIn(AdvancedMonktApi::class)
fun <T, U> MapSchemaBuilder<T, U>.bsonDecodeMapper(
    block: BsonMapper<U, T>
) {
    bsonDecodeMapper = block
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
==================== UnitSchema ====================
==================================================*/

/**
 * The schema for the value [Unit].
 *
 * All its functions will throw an error.
 *
 * @author LSafer
 * @since 2.0.0
 */
object UnitSchema : Schema<Unit> {
    @AdvancedMonktApi
    override fun encode(value: Unit): BsonValue {
        error("Unit Schema Usage")
    }

    @AdvancedMonktApi
    override fun decode(bsonValue: BsonValue) {
        error("Unit Schema Usage")
    }

    @AdvancedMonktApi
    override fun canDecode(bsonValue: BsonValue): Boolean {
        error("Unit Schema Usage")
    }
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
    accept(BsonStringType)
    encodeSafe { BsonString(it) }
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
    accept(BsonBooleanType)
    encodeSafe { BsonBoolean(it) }
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
    accept(BsonInt32Type)
    encodeSafe { BsonInt32(it) }
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
    accept(BsonInt64Type)
    encodeSafe { BsonInt64(it) }
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
    accept(BsonDoubleType)
    encodeSafe { BsonDouble(it) }
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
    accept(BsonDecimal128Type)
    encodeSafe { BsonDecimal128(it) }
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
    accept(BsonObjectIdType)
    encodeSafe { BsonObjectId(it) }
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
    accept(BsonObjectIdType, BsonStringType)
    encodeSafe {
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
                "IdSchema expected $BsonObjectIdType or $BsonStringType but got $it"
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
    accept(BsonDecimal128Type)
    encodeSafe { BsonDecimal128(Decimal128(it)) }
    decode {
        it as BsonDecimal128
        it.value.bigDecimalValue()
    }
}
