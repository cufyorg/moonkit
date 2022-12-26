package org.cufy.monkt.internal

import org.cufy.bson.*
import org.cufy.monkt.*
import org.cufy.monkt.schema.*
import java.util.function.Predicate

/**
 * The default implementation of [ScalarCoercer].
 *
 * @param T the type of the runtime value.
 * @since 2.0.0
 */
@InternalMonktApi
open class ScalarCoercerImpl<T>(
    override val types: List<BsonType>,
    val decoder: Decoder<out T>,
    val predicate: Predicate<BsonValue>
) : ScalarCoercer<T> {
    override fun canDecode(bsonValue: BsonValue): Boolean {
        return predicate.test(bsonValue)
    }

    @OptIn(AdvancedMonktApi::class)
    override fun decode(bsonValue: BsonValue): T {
        return decoder.decode(bsonValue)
    }
}

/**
 * The default implementation of [MapCoercer].
 *
 * @param T the type of the wrapped coercer.
 * @since 2.0.0
 */
@InternalMonktApi
open class MapCoercerImpl<T, U>(
    override val coercer: Coercer<out U>,
    /**
     * Transforms runtime values of type `U` to `T`
     */
    val decodeMapper: Mapper<U, T>,
    /**
     * Transforms bson values of type `T` to `U`
     */
    val bsonEncodeMapper: BsonMapper<T, U>
) : MapCoercer<T, U> {
    @AdvancedMonktApi
    override fun canDecode(bsonValue: BsonValue): Boolean {
        val uBsonValue = bsonEncodeMapper.map(bsonValue)
        return coercer.canDecode(uBsonValue)
    }

    @AdvancedMonktApi
    override fun decode(bsonValue: BsonValue): T {
        val uBsonValue = bsonEncodeMapper.map(bsonValue)
        val uValue = coercer.decode(uBsonValue)
        return decodeMapper.map(uValue)
    }
}

/**
 * The default implementation of [ArraySchema].
 *
 * @param T the type of the wrapped schema.
 * @since 2.0.0
 */
@InternalMonktApi
open class ArraySchemaImpl<T>(
    /**
     * The schema to be wrapped.
     */
    override val schema: Schema<T>,
    /**
     * The coercers to be used to coerce the
     * values. (order matters)
     */
    val coercers: List<Coercer<out T>>,
    /**
     * The final decoder to be used when all
     * coercers fail.
     */
    val finalDecoder: Decoder<out T>?,
    /**
     * The instance options.
     */
    val options: List<Option<List<T>, List<T>, *>>,
    /**
     * The static options.
     */
    val staticOptions: List<Option<Unit, Unit, *>>,
    /**
     * A function to be executed after the array
     * has been decoded.
     */
    val onDecode: ArraySchemaCodecBlock<T>?,
    /**
     * A function to be executed after the array
     * has been encoded.
     */
    val onEncode: ArraySchemaCodecBlock<T>?,
) : ArraySchema<T> {
    @OptIn(AdvancedMonktApi::class)
    override fun obtainStaticOptions(
        model: Model<*>,
        pathname: Pathname,
        dejaVu: Set<Schema<*>>
    ): List<OptionData<Unit, Unit, *>> {
        val subOptions = when (val schema = schema) {
            is ElementSchema<T> ->
                schema.obtainStaticOptions(model, pathname, dejaVu)
            else -> emptyList()
        }

        return subOptions + staticOptions.map { option ->
            OptionData(model, pathname, option)
        }
    }

    @AdvancedMonktApi
    override fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: List<T>
    ): List<OptionData<*, *, *>> {
        val subOptions = when (val schema = schema) {
            is ElementSchema<T> ->
                instance.filterNotNull().flatMapIndexed { index, item ->
                    val subPathname = pathname + "$index"

                    schema.obtainOptions(model, root, subPathname, item)
                }
            else -> emptyList()
        }

        return subOptions + options.map { option ->
            OptionData(model, root, pathname, this, instance, instance, option)
        }
    }

    @AdvancedMonktApi
    override fun canDecode(bsonValue: BsonValue): Boolean {
        return bsonValue is BsonArray/* && bsonValue.all { schema.canDecode(it) }*/
    }

    @AdvancedMonktApi
    override fun decode(bsonValue: BsonValue): List<T> {
        bsonValue as BsonArray

        val list = mutableListOf<T>()

        bsonValue.forEach { item ->
            val value = run {
                if (schema.canDecode(item))
                    return@run schema.decode(item)

                val coercer = coercers.firstOrNull { it.canDecode(item) }

                if (coercer != null)
                    return@run coercer.decode(item)

                finalDecoder?.let {
                    return@run it.decode(item)
                }

                return@forEach /* Skip Decoding */
            }

            list += value
        }

        onDecode?.let {
            it(ArraySchemaCodecScope(
                schema = this,
                instance = list,
                array = bsonValue
            ))
        }

        return list
    }

    @AdvancedMonktApi
    override fun encode(value: List<T>): BsonValue {
        val array = BsonArray(value.map { schema.encode(it) })

        onEncode?.let {
            it(ArraySchemaCodecScope(
                schema = this,
                instance = value,
                array = array
            ))
        }

        return array
    }
}

/**
 * A coercer to be used to safely create a coercer
 * with only one [block].
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
class DeterministicCoercerImpl<T>(
    val block: DeterministicCoercerBlock<T>
) : Coercer<T> {
    @AdvancedMonktApi
    override fun canDecode(bsonValue: BsonValue): Boolean {
        val scope = DeterministicCoercerScope<T>()
        scope.apply { block(bsonValue) }
        return scope.value != null
    }

    @AdvancedMonktApi
    override fun decode(bsonValue: BsonValue): T {
        val scope = DeterministicCoercerScope<T>()
        scope.apply { block(bsonValue) }
        return scope.value?.value
            ?: error("Deterministic Coercion Failed: got ${bsonValue.bsonType}")
    }
}

/**
 * The default implementation of [ScalarSchema].
 *
 * @param T the type of the runtime value.
 * @since 2.0.0
 */
@InternalMonktApi
open class ScalarSchemaImpl<T>(
    override val types: List<BsonType>,
    val decoder: Decoder<out T>,
    val encoder: Encoder<in T>,
    val predicate: Predicate<BsonValue>
) : ScalarSchema<T> {
    override fun canDecode(bsonValue: BsonValue): Boolean {
        return predicate.test(bsonValue)
    }

    @OptIn(AdvancedMonktApi::class)
    override fun decode(bsonValue: BsonValue): T {
        return decoder.decode(bsonValue)
    }

    @OptIn(AdvancedMonktApi::class)
    override fun encode(value: T): BsonValue {
        return encoder.encode(value)
    }
}

/**
 * The default implementation of [EnumSchema].
 *
 * @param T the type of the runtime enum.
 * @since 2.0.0
 */
@InternalMonktApi
open class EnumSchemaImpl<T>(
    override val values: Map<BsonValue, T>
) : EnumSchema<T> {
    @Suppress("LeakingThis")
    override val types: List<BsonType> =
        values.keys.map { it.bsonType }.distinct()

    override fun canDecode(bsonValue: BsonValue): Boolean {
        return values.containsKey(bsonValue)
    }

    override fun decode(bsonValue: BsonValue): T {
        val value = values[bsonValue]
        require(value != null) {
            "EnumSchema.decode(...) expected one of ${values.keys} but got $bsonValue"
        }
        return value
    }

    override fun encode(value: T): BsonValue {
        val bsonValue = values.entries.firstOrNull { it.value == value }?.key
        require(bsonValue != null) {
            "EnumSchema.encode(...) expected one of ${values.values} but got $value"
        }
        return bsonValue
    }
}

/**
 * The default implementation of [MapSchema].
 *
 * @param T the type of the wrapped schema.
 * @since 2.0.0
 */
@InternalMonktApi
open class MapSchemaImpl<T, U>(
    lazySchema: Lazy<Schema<U>>,
    /**
     * Transforms runtime values of type `T` to `U`
     */
    val encodeMapper: Mapper<T, U>,
    /**
     * Transforms runtime values of type `U` to `T`
     */
    val decodeMapper: Mapper<U, T>,
    /**
     * Transforms bson values of type `T` to `U`
     */
    val bsonEncodeMapper: BsonMapper<T, U>,
    /**
     * Transforms bson values of type `U` to `T`
     */
    val bsonDecodeMapper: BsonMapper<U, T>
) : MapSchema<T, U> {
    override val schema by lazySchema

    @AdvancedMonktApi
    override fun obtainStaticOptions(
        model: Model<*>,
        pathname: Pathname,
        dejaVu: Set<Schema<*>>
    ): List<OptionData<Unit, Unit, *>> {
        return when (val schema = schema) {
            is ElementSchema<U> ->
                schema.obtainStaticOptions(model, pathname, dejaVu)
            else -> emptyList()
        }
    }

    @AdvancedMonktApi
    override fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: T & Any
    ): List<OptionData<*, *, *>> {
        val uInstance = encodeMapper.map(instance)

        uInstance ?: return emptyList()

        return when (val schema = schema) {
            is ElementSchema<U> ->
                schema.obtainOptions(model, root, pathname, uInstance)
            else -> emptyList()
        }
    }

    @AdvancedMonktApi
    override fun canDecode(bsonValue: BsonValue): Boolean {
        val uBsonValue = bsonEncodeMapper.map(bsonValue)
        return schema.canDecode(uBsonValue)
    }

    @AdvancedMonktApi
    override fun decode(bsonValue: BsonValue): T {
        val uBsonValue = bsonEncodeMapper.map(bsonValue)
        val uValue = schema.decode(uBsonValue)
        return decodeMapper.map(uValue)
    }

    @AdvancedMonktApi
    override fun encode(value: T): BsonValue {
        val uValue = encodeMapper.map(value)
        val uBsonValue = schema.encode(uValue)
        return bsonDecodeMapper.map(uBsonValue)
    }
}

/**
 * A schema wrapper for nullable values.
 *
 * Wraps another schema to be nullable.
 *
 * @param <T> the type of teh wrapped schema.
 * @since 2.0.0
 */
@InternalMonktApi
open class NullableSchemaImpl<T>(
    override val schema: Schema<T>
) : NullableSchema<T> {
    @AdvancedMonktApi
    override fun obtainStaticOptions(
        model: Model<*>,
        pathname: Pathname,
        dejaVu: Set<Schema<*>>
    ): List<OptionData<Unit, Unit, *>> {
        return when (val schema = schema) {
            is ElementSchema<T> ->
                schema.obtainStaticOptions(model, pathname, dejaVu)
            else -> emptyList()
        }
    }

    @AdvancedMonktApi
    override fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: T & Any
    ): List<OptionData<*, *, *>> {
        return when (val schema = schema) {
            is ElementSchema<T> ->
                schema.obtainOptions(model, root, pathname, instance)
            else -> emptyList()
        }
    }

    @AdvancedMonktApi
    override fun canDecode(bsonValue: BsonValue): Boolean {
        return bsonValue is BsonNull || schema.canDecode(bsonValue)
    }

    @AdvancedMonktApi
    override fun decode(bsonValue: BsonValue): T? {
        return when (bsonValue) {
            is BsonNull -> null
            else -> schema.decode(bsonValue)
        }
    }

    @AdvancedMonktApi
    override fun encode(value: T?): BsonValue {
        return when (value) {
            null -> bnull
            else -> schema.encode(value)
        }
    }
}

/**
 * The default implementation of [FieldDefinition].
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class FieldDefinitionImpl<T : Any, M>(
    override val name: String,
    override val getter: FieldDefinitionGetter<T, M>,
    override val setter: FieldDefinitionSetter<T, M>,
    lazySchema: Lazy<Schema<M>>,
    /**
     * A list of coercers to be used when the
     * [schema] cannot decode the value.
     */
    val coercers: List<Coercer<out M>>,
    /**
     * The final decoder to be used when all
     * coercers fail.
     */
    val finalDecoder: Decoder<out M>?,
    /**
     * The instance options.
     */
    val options: List<Option<T, M, *>>,
    /**
     * The static options.
     */
    val staticOptions: List<Option<Unit, Unit, *>>,
    /**
     * A function to be executed after the field
     * has been decoded.
     */
    val onDecode: FieldDefinitionCodecBlock<T, M>?,
    /**
     * A function to be executed after the field
     * has been encoded.
     */
    val onEncode: FieldDefinitionCodecBlock<T, M>?
) : FieldDefinition<T, M> {
    override val schema by lazySchema

    @AdvancedMonktApi
    override fun obtainStaticOptions(
        model: Model<*>,
        pathname: Pathname,
        dejaVu: Set<Schema<*>>
    ): List<OptionData<Unit, Unit, *>> {
        val subPathname = pathname + name

        val subOptions = when (val schema = schema) {
            is ElementSchema<M> ->
                schema.obtainStaticOptions(model, subPathname, dejaVu)
            else -> emptyList()
        }

        return subOptions + staticOptions.map { option ->
            OptionData(model, subPathname, option)
        }
    }

    @AdvancedMonktApi
    override fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: T
    ): List<OptionData<*, *, *>> {
        val value = getter(instance)
        val subPathname = pathname + name
        val schema = schema

        val subOptions = when {
            schema is ElementSchema<M> && value != null ->
                schema.obtainOptions(model, root, subPathname, value)
            else -> emptyList()
        }

        return subOptions + options.map { option ->
            OptionData(model, root, subPathname, schema, instance, value, option)
        }
    }

    @AdvancedMonktApi
    override fun decode(instance: T, document: BsonDocument) {
        val schema = schema

        val bsonValue = document[name] ?: bundefined

        val value = run {
            if (schema.canDecode(bsonValue))
                return@run schema.decode(bsonValue)

            val coercer = coercers.firstOrNull { it.canDecode(bsonValue) }

            if (coercer != null)
                return@run coercer.decode(bsonValue)

            finalDecoder?.let {
                return@run it.decode(bsonValue)
            }

            return /* Skip Decoding */
        }

        setter(instance, value)

        onDecode?.let {
            it(FieldDefinitionCodecScope(
                definition = this,
                instance = instance,
                document = document,
                value = value,
                bsonValue = bsonValue
            ))
        }
    }

    @AdvancedMonktApi
    override fun encode(instance: T, document: BsonDocument) {
        val schema = schema

        val value = getter(instance)
        val bsonValue = schema.encode(value)

        document[name] = bsonValue

        onEncode?.let {
            it(FieldDefinitionCodecScope(
                definition = this,
                instance = instance,
                document = document,
                value = value,
                bsonValue = bsonValue
            ))
        }
    }
}

/**
 * The default implementation of [ObjectSchema].
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
open class ObjectSchemaImpl<T : Any>(
    override val constructor: ObjectSchemaConstructor<T>,
    override val fields: List<FieldDefinition<T, *>>,
    /**
     * The instance options.
     */
    val options: List<Option<T, T, *>>,
    /**
     * The static options.
     */
    val staticOptions: List<Option<Unit, Unit, *>>,
    /**
     * A function to be executed after the object
     * has been decoded.
     */
    val onDecode: ObjectSchemaCodecBlock<T>?,
    /**
     * A function to be executed after the object
     * has been encoded.
     */
    val onEncode: ObjectSchemaCodecBlock<T>?
) : ObjectSchema<T> {
    @AdvancedMonktApi
    override fun obtainStaticOptions(
        model: Model<*>,
        pathname: Pathname,
        dejaVu: Set<Schema<*>>
    ): List<OptionData<Unit, Unit, *>> {
        if (this in dejaVu)
            return emptyList()

        val subDejaVu = dejaVu + this

        val subOptions = fields.flatMap {
            it.obtainStaticOptions(model, pathname, subDejaVu)
        }

        return subOptions + staticOptions.map { option ->
            OptionData(model, pathname, option)
        }
    }

    @AdvancedMonktApi
    override fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: T
    ): List<OptionData<*, *, *>> {
        val subOptions = fields.flatMap {
            it.obtainOptions(model, root, pathname, instance)
        }

        return subOptions + options.map { option ->
            OptionData(model, root, pathname, this, instance, instance, option)
        }
    }

    @AdvancedMonktApi
    override fun canDecode(bsonValue: BsonValue): Boolean {
        return bsonValue is BsonDocument
    }

    @AdvancedMonktApi
    override fun decode(bsonValue: BsonValue): T {
        bsonValue as BsonDocument

        val instance = constructor()

        fields.forEach { it.decode(instance, bsonValue) }

        onDecode?.let {
            it(ObjectSchemaCodecScope(
                schema = this,
                instance = instance,
                document = bsonValue
            ))
        }

        return instance
    }

    @AdvancedMonktApi
    override fun encode(value: T): BsonDocument {
        val document = BsonDocument()

        fields.forEach { it.encode(value, document) }

        onEncode?.let {
            it(ObjectSchemaCodecScope(
                schema = this,
                instance = value,
                document = document
            ))
        }

        return document
    }
}
