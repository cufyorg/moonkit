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
package org.cufy.monkt.internal

import org.cufy.bson.BsonType
import org.cufy.bson.BsonValue
import org.cufy.monkt.*
import org.cufy.monkt.schema.*
import java.util.function.Predicate

/**
 * A builder for creating a [ScalarCoercer]
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class ScalarCoercerBuilderImpl<T> : ScalarCoercerBuilder<T> {
    @AdvancedMonktApi
    override var decoder: Decoder<out T>? = null // REQUIRED

    @AdvancedMonktApi
    override var types: MutableList<BsonType> = mutableListOf()

    @AdvancedMonktApi
    override val predicate: MutableList<Predicate<BsonValue>> = mutableListOf()

    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    @OptIn(AdvancedMonktApi::class)
    override fun build(): ScalarCoercer<T> {
        deferred.forEach { it() }
        deferred.clear()
        return ScalarCoercerImpl(
            types = types.toList(),
            decoder = decoder
                ?: error("decoder is required but was not provided"),
            predicate = predicate.toList().let {
                Predicate { bsonValue ->
                    it.any { it.test(bsonValue) }
                }
            }
        )
    }
}

/**
 * A builder for creating a [MapCoercer]
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class MapCoercerBuilderImpl<T, U> : MapCoercerBuilder<T, U> {
    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    /**
     * The wrapped coercer.
     */
    @AdvancedMonktApi
    override var coercer: Coercer<out U>? = null // REQUIRED

    /**
     * Transforms runtime values of type `U` to `T`
     */
    @AdvancedMonktApi
    override var decodeMapper: Mapper<U, T>? = null // REQUIRED

    /**
     * Transforms bson values of type `T` to `U`
     */
    @AdvancedMonktApi
    override var bsonEncodeMapper: BsonMapper<T, U> = BsonMapper { it }

    @OptIn(AdvancedMonktApi::class, InternalMonktApi::class)
    override fun build(): MapCoercer<T, U> {
        deferred.forEach { it() }
        deferred.clear()
        return MapCoercerImpl(
            coercer = coercer
                ?: error("coercer is required but was not provided"),
            decodeMapper = decodeMapper
                ?: error("decodeMapper is required but was not provided"),
            bsonEncodeMapper = bsonEncodeMapper
        )
    }
}

/**
 * A builder for creating an [ArraySchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class ArraySchemaBuilderImpl<T> : ArraySchemaBuilder<T> {
    @AdvancedMonktApi
    override val options: MutableList<Option<List<T>, List<T>, *>> = mutableListOf()

    @AdvancedMonktApi
    override val staticOptions: MutableList<Option<Unit, Unit, *>> = mutableListOf()

    @AdvancedMonktApi
    override val coercers: MutableList<Coercer<out T>> = mutableListOf()

    @AdvancedMonktApi
    override var finalDecoder: Decoder<out T>? = null

    @AdvancedMonktApi
    override var schema: Lazy<Schema<T>>? = null // REQUIRED

    @AdvancedMonktApi
    override val onDecode: MutableList<ArraySchemaCodecBlock<T>> = mutableListOf()

    @AdvancedMonktApi
    override val onEncode: MutableList<ArraySchemaCodecBlock<T>> = mutableListOf()

    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    @OptIn(AdvancedMonktApi::class, InternalMonktApi::class)
    override fun build(): ArraySchema<T> {
        deferred.forEach { it() }
        deferred.clear()
        return ArraySchemaImpl(
            schema = schema?.value
                ?: error("schema is required but was not provided"),
            options = options.toList(),
            staticOptions = staticOptions.toList(),
            coercers = coercers.toList(),
            finalDecoder = finalDecoder,
            onEncode = onEncode.toList().takeIf { it.isNotEmpty() }?.let {
                { it.forEach { it() } }
            },
            onDecode = onDecode.toList().takeIf { it.isNotEmpty() }?.let {
                { it.forEach { it() } }
            }
        )
    }
}

/**
 * A builder for creating a [ScalarSchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class ScalarSchemaBuilderImpl<T> : ScalarSchemaBuilder<T> {
    @AdvancedMonktApi
    override var decoder: Decoder<out T>? = null // REQUIRED

    @AdvancedMonktApi
    override var types: MutableList<BsonType> = mutableListOf()

    @AdvancedMonktApi
    override var predicate: MutableList<Predicate<BsonValue>> = mutableListOf()

    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    @AdvancedMonktApi
    override var encoder: Encoder<in T>? = null // REQUIRED

    @OptIn(InternalMonktApi::class, AdvancedMonktApi::class)
    override fun build(): ScalarSchema<T> {
        deferred.forEach { it() }
        deferred.clear()
        return ScalarSchemaImpl(
            types = types.toList(),
            decoder = decoder
                ?: error("decoder is required but was not provided"),
            encoder = encoder
                ?: error("encoder is required but was not provided"),
            predicate = predicate.toList().let {
                Predicate { bsonValue ->
                    it.any { it.test(bsonValue) }
                }
            }
        )
    }
}

/**
 * A builder for creating an [EnumSchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class EnumSchemaBuilderImpl<T> : EnumSchemaBuilder<T> {
    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    @AdvancedMonktApi
    override val values: MutableMap<BsonValue, T> = mutableMapOf()

    @OptIn(AdvancedMonktApi::class, InternalMonktApi::class)
    override fun build(): EnumSchema<T> {
        deferred.forEach { it() }
        deferred.clear()
        return EnumSchemaImpl(
            values = values.toMap()
        )
    }
}

/**
 * A builder for creating a [MapSchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class MapSchemaBuilderImpl<T, U> : MapSchemaBuilder<T, U> {
    @AdvancedMonktApi
    override var schema: Lazy<Schema<U>>? = null // REQUIRED

    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    /**
     * Transforms runtime values of type `T` to `U`
     */
    @AdvancedMonktApi
    override var encodeMapper: Mapper<T, U>? = null // REQUIRED

    /**
     * Transforms runtime values of type `U` to `T`
     */
    @AdvancedMonktApi
    override var decodeMapper: Mapper<U, T>? = null // REQUIRED

    /**
     * Transforms bson values of type `T` to `U`
     */
    @AdvancedMonktApi
    override var bsonEncodeMapper: BsonMapper<T, U> = BsonMapper { it }

    /**
     * Transforms bson values of type `U` to `T`
     */
    @AdvancedMonktApi
    override var bsonDecodeMapper: BsonMapper<U, T> = BsonMapper { it }

    @OptIn(AdvancedMonktApi::class, InternalMonktApi::class)
    override fun build(): MapSchema<T, U> {
        deferred.forEach { it() }
        deferred.clear()
        return MapSchemaImpl(
            lazySchema = schema
                ?: error("schema is required but was not provided"),
            encodeMapper = encodeMapper
                ?: error("mapEncoder is required but was not provided"),
            decodeMapper = decodeMapper
                ?: error("mapDecoder function is required but was not provided"),
            bsonEncodeMapper = bsonEncodeMapper,
            bsonDecodeMapper = bsonDecodeMapper
        )
    }
}

/**
 * A builder for creating a [FieldDefinition].
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class FieldDefinitionBuilderImpl<T : Any, M> : FieldDefinitionBuilder<T, M> {
    @AdvancedMonktApi
    override val options: MutableList<Option<T, M, *>> = mutableListOf()

    @AdvancedMonktApi
    override val staticOptions: MutableList<Option<Unit, Unit, *>> = mutableListOf()

    @AdvancedMonktApi
    override val coercers: MutableList<Coercer<out M>> = mutableListOf()

    @AdvancedMonktApi
    override var finalDecoder: Decoder<out M>? = null

    @AdvancedMonktApi
    override var schema: Lazy<Schema<M>>? = null // REQUIRED

    @AdvancedMonktApi
    override val onEncode: MutableList<FieldDefinitionCodecBlock<T, M>> = mutableListOf()

    @AdvancedMonktApi
    override val onDecode: MutableList<FieldDefinitionCodecBlock<T, M>> = mutableListOf()

    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    @AdvancedMonktApi
    override var name: String? = null // REQUIRED

    @AdvancedMonktApi
    override var getter: FieldDefinitionGetter<T, M>? = null // REQUIRED

    @AdvancedMonktApi
    override var setter: FieldDefinitionSetter<T, M>? = null // REQUIRED

    @OptIn(AdvancedMonktApi::class, InternalMonktApi::class)
    override fun build(): FieldDefinition<T, M> {
        deferred.forEach { it() }
        deferred.clear()
        return FieldDefinitionImpl(
            name = name
                ?: error("name is required but was not provided"),
            lazySchema = schema
                ?: error("schema is required but was not provided"),
            getter = getter
                ?: error("getter is required but was not provided"),
            setter = setter
                ?: error("setter is required but was not provided"),
            options = options.toList(),
            staticOptions = staticOptions.toList(),
            coercers = coercers.toList(),
            finalDecoder = finalDecoder,
            onEncode = onEncode.toList().takeIf { it.isNotEmpty() }?.let {
                { it.forEach { it() } }
            },
            onDecode = onDecode.toList().takeIf { it.isNotEmpty() }?.let {
                { it.forEach { it() } }
            }
        )
    }
}

/**
 * A builder for applying a mapped
 * [FieldDefinition] configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class FieldDefinitionMapperBuilderImpl<T : Any, M, N> :
    FieldDefinitionBuilderImpl<T, N>(),
    FieldDefinitionMapperBuilder<T, M, N> {
    @AdvancedMonktApi
    override var encodeMapper: Mapper<M, N>? = null // REQUIRED

    @AdvancedMonktApi
    override var decodeMapper: Mapper<N, M>? = null // REQUIRED

    @AdvancedMonktApi
    override var bsonEncodeMapper: BsonMapper<M, N> = BsonMapper { it }

    @AdvancedMonktApi
    override var bsonDecodeMapper: BsonMapper<N, M> = BsonMapper { it }

    @OptIn(AdvancedMonktApi::class, InternalMonktApi::class)
    override fun applyTo(builder: FieldDefinitionBuilder<T, M>) {
        this.deferred.forEach { it() }
        this.deferred.clear()

        val mappers = Mappers(
            encodeMapper = this.encodeMapper
                ?: error("encodeMapper is required but was not provided"),
            decodeMapper = this.decodeMapper
                ?: error("decodeMapper is required but was not provided"),
            bsonEncodeMapper = this.bsonEncodeMapper,
            bsonDecodeMapper = this.bsonDecodeMapper
        )

        builder.options += this.options.map { mapOption(it, this.schema, mappers) }
        builder.staticOptions += this.staticOptions
        builder.coercers += this.coercers.map { mapCoercer(it, mappers) }
        this.finalDecoder?.let { builder.finalDecoder = mapDecoder(it, mappers) }
        this.schema?.let { builder.schema = lazy { mapSchema(it.value, mappers) } }
        builder.onEncode += mapFieldDefinitionCodecBlock(this.onEncode, this.schema, mappers)
        builder.onDecode += mapFieldDefinitionCodecBlock(this.onDecode, this.schema, mappers)
        this.name?.let { builder.name = it }
        this.getter?.let { builder.getter = mapFieldDefinitionGetter(it, mappers) }
        this.setter?.let { builder.setter = mapFieldDefinitionSetter(it, mappers) }
    }
}

/**
 * A builder for creating an [ObjectSchema].
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class ObjectSchemaBuilderImpl<T : Any> : ObjectSchemaBuilder<T> {
    @AdvancedMonktApi
    override val options: MutableList<Option<T, T, *>> = mutableListOf()

    @AdvancedMonktApi
    override val staticOptions: MutableList<Option<Unit, Unit, *>> = mutableListOf()

    @AdvancedMonktApi
    override val onEncode: MutableList<ObjectSchemaCodecBlock<T>> = mutableListOf()

    @AdvancedMonktApi
    override val onDecode: MutableList<ObjectSchemaCodecBlock<T>> = mutableListOf()

    @AdvancedMonktApi
    override val deferred: MutableList<() -> Unit> = mutableListOf()

    @AdvancedMonktApi
    override var constructor: ObjectSchemaConstructor<T>? = null // REQUIRED

    @AdvancedMonktApi
    override val fields: MutableList<FieldDefinition<T, *>> = mutableListOf()

    @OptIn(AdvancedMonktApi::class, InternalMonktApi::class)
    override fun build(): ObjectSchema<T> {
        deferred.forEach { it() }
        deferred.clear()
        return ObjectSchemaImpl(
            constructor = constructor
                ?: error("constructor is required but was not provided"),
            fields = fields.toList(),
            options = options.toList(),
            staticOptions = staticOptions.toList(),
            onEncode = onEncode.toList().takeIf { it.isNotEmpty() }?.let {
                { it.forEach { it() } }
            },
            onDecode = onDecode.toList().takeIf { it.isNotEmpty() }?.let {
                { it.forEach { it() } }
            }
        )
    }
}
