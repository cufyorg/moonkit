package org.cufy.monkt.internal

import org.cufy.bson.*
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

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
     * The coerce decoders to be used to coerce the
     * values. (order matters)
     */
    val decoders: List<Decoder<out T>>,
    /**
     * The final decoder to be used when all
     * coercers fail.
     */
    val finalDecoder: Decoder<out T>?,
    /**
     * The coerce encoders to be used to coerce the
     * values. (order matters)
     */
    val encoders: List<Encoder<in T>>,
    /**
     * The final encoder to be used when all
     * coercers fail.
     */
    val finalEncoder: Encoder<in T>?,
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
        val declaration = this

        val subOptions = when (val schema = schema) {
            is ElementSchema<T> ->
                schema.obtainStaticOptions(model, pathname, dejaVu)
            else -> emptyList()
        }

        return subOptions + staticOptions.map { option ->
            OptionData(model, pathname, declaration, option)
        }
    }

    @AdvancedMonktApi
    override fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: List<T>
    ): List<OptionData<*, *, *>> {
        val declaration = this

        val subOptions = when (val schema = schema) {
            is ElementSchema<T> ->
                instance.filterNotNull().flatMapIndexed { index, item ->
                    val subPathname = pathname + "$index"

                    schema.obtainOptions(model, root, subPathname, item)
                }
            else -> emptyList()
        }

        return subOptions + options.map { option ->
            OptionData(model, root, pathname, declaration, instance, instance, option)
        }
    }

    @AdvancedMonktApi
    override fun canDecode(element: BsonElement): Boolean {
        return element is BsonArray/* && bsonValue.all { schema.canDecode(it) }*/
    }

    @AdvancedMonktApi
    override fun canEncode(value: Any?): Boolean {
        return value is List<*>
    }

    @AdvancedMonktApi
    override fun decode(element: BsonElement): List<T> {
        element as BsonArray
        val array = element.toMutableBsonList()

        val list = mutableListOf<T>()

        array.forEach { item ->
            val value = run {
                if (schema.canDecode(item))
                    return@run schema.decode(item)

                val decoder = decoders.firstOrNull { it.canDecode(item) }

                if (decoder != null)
                    return@run decoder.decode(item)

                finalDecoder?.let {
                    if (it.canDecode(item))
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
                array = array
            ))
        }

        return list
    }

    @AdvancedMonktApi
    override fun encode(value: List<T>): BsonElement {
        val array = mutableBsonListOf()

        value.forEach { item ->
            val element = run {
                if (schema.canEncode(item))
                    return@run schema.encode(item)

                val encoder = encoders.firstOrNull { it.canEncode(item) }

                if (encoder != null)
                    return@run encoder.encode(item)

                finalEncoder?.let {
                    if (it.canEncode(item))
                        return@run it.encode(item)
                }

                return@forEach /* Skip Encoding */
            }

            array += element
        }

        onEncode?.let {
            it(ArraySchemaCodecScope(
                schema = this,
                instance = value,
                array = array
            ))
        }

        return array.toBsonArray()
    }

    override fun toString(): String = "ArraySchema($schema)"
}

/**
 * A decoder to be used to safely create a decoder
 * with only one [block].
 *
 * @author LSafer
 * @since 2.0.0
 */
@InternalMonktApi
class DeterministicDecoderImpl<T>(
    val block: DeterministicDecoderBlock<T>
) : Decoder<T> {
    @AdvancedMonktApi
    override fun canDecode(element: BsonElement): Boolean {
        val scope = DeterministicDecoderScope<T>()
        scope.apply { block(element) }
        return scope.isDecoded
    }

    @AdvancedMonktApi
    override fun decode(element: BsonElement): T {
        val scope = DeterministicDecoderScope<T>()
        scope.apply { block(element) }
        return scope.decodedValue
    }

    override fun toString(): String = "DeterministicDecoder()"
}

/**
 * The default implementation of [ScalarDecoder].
 *
 * @param T the type of the runtime value.
 * @since 2.0.0
 */
@InternalMonktApi
data class ScalarDecoderImpl<T>(
    override val types: List<BsonType>,
    val decodeBlock: (BsonElement) -> T,
    val canDecodeBlock: (BsonElement) -> Boolean
) : ScalarDecoder<T> {
    override fun canDecode(element: BsonElement): Boolean {
        return canDecodeBlock(element)
    }

    override fun decode(element: BsonElement): T {
        return decodeBlock(element)
    }

    override fun toString(): String = "ScalarDecoder(${types.joinToString(", ")})"
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
    val canDecodeBlock: (BsonElement) -> Boolean,
    val decodeBlock: (BsonElement) -> T,
    val canEncodeBlock: (Any?) -> Boolean,
    val encodeBlock: (T) -> BsonElement,
) : ScalarSchema<T> {
    override fun canDecode(element: BsonElement): Boolean {
        return canDecodeBlock(element)
    }

    override fun canEncode(value: Any?): Boolean {
        return canEncodeBlock(value)
    }

    override fun decode(element: BsonElement): T {
        return decodeBlock(element)
    }

    override fun encode(value: T): BsonElement {
        return encodeBlock(value)
    }

    override fun toString(): String = "ScalarSchema(${types.joinToString(", ")})"
}

/**
 * The default implementation of [EnumSchema].
 *
 * @param T the type of the runtime enum.
 * @since 2.0.0
 */
@InternalMonktApi
open class EnumSchemaImpl<T>(
    override val values: Map<BsonElement, T>
) : EnumSchema<T> {
    @Suppress("LeakingThis")
    override val types: List<BsonType> =
        values.keys.map { it.type }.distinct()

    override fun canDecode(element: BsonElement): Boolean {
        return values.containsKey(element)
    }

    override fun canEncode(value: Any?): Boolean {
        return values.containsValue(value)
    }

    override fun decode(element: BsonElement): T {
        val value = values[element]
        require(value != null) {
            "EnumSchema.decode(...) expected one of ${values.keys} but got $element"
        }
        return value
    }

    override fun encode(value: T): BsonElement {
        val bsonValue = values.entries.firstOrNull { it.value == value }?.key
        require(bsonValue != null) {
            "EnumSchema.encode(...) expected one of ${values.values} but got $value"
        }
        return bsonValue
    }

    override fun toString(): String = "EnumSchema(${values.values.joinToString(", ")})"
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
    override fun canDecode(element: BsonElement): Boolean {
        return element is BsonNull || schema.canDecode(element)
    }

    @AdvancedMonktApi
    override fun canEncode(value: Any?): Boolean {
        return value == null || schema.canEncode(value)
    }

    @AdvancedMonktApi
    override fun decode(element: BsonElement): T? {
        return when (element) {
            is BsonNull -> null
            else -> schema.decode(element)
        }
    }

    @AdvancedMonktApi
    override fun encode(value: T?): BsonElement {
        return when (value) {
            null -> bnull
            else -> schema.encode(value)
        }
    }

    override fun toString(): String = "NullableSchema($schema)"
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
     * A list of coerce decoders to be used when
     * the [schema] cannot decode the value.
     */
    val decoders: List<Decoder<out M>>,
    /**
     * The final decoder to be used when all
     * coercers fail.
     */
    val finalDecoder: Decoder<out M>?,
    /**
     * A list of coerce encoders to be used when
     * the [schema] cannot encode the value.
     */
    val encoders: List<Encoder<in M>>,
    /**
     * The final encoder to be used when all
     * coercers fail.
     */
    val finalEncoder: Encoder<in M>?,
    /**
     * The instance options.
     */
    val options: List<Option<T, M?, *>>,
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
        val schema = schema
        val declaration = this

        val subOptions = when (schema) {
            is ElementSchema<M> ->
                schema.obtainStaticOptions(model, subPathname, dejaVu)
            else -> emptyList()
        }

        return subOptions + staticOptions.map { option ->
            OptionData(model, subPathname, declaration, option)
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
        val declaration = this

        val subOptions = when {
            schema is ElementSchema<M> && value != null ->
                schema.obtainOptions(model, root, subPathname, value)
            else -> emptyList()
        }

        return subOptions + options.map { option ->
            OptionData(model, root, subPathname, declaration, instance, value, option)
        }
    }

    @AdvancedMonktApi
    override fun decode(instance: T, document: MutableBsonMap) {
        val schema = schema

        val bsonValue = document[name] ?: bundefined

        val value = run {
            if (schema.canDecode(bsonValue))
                return@run schema.decode(bsonValue)

            val decoder = decoders.firstOrNull { it.canDecode(bsonValue) }

            if (decoder != null)
                return@run decoder.decode(bsonValue)

            finalDecoder?.let {
                if (it.canDecode(bsonValue))
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
                element = bsonValue
            ))
        }
    }

    @AdvancedMonktApi
    override fun encode(instance: T, document: MutableBsonMap) {
        val schema = schema

        val value = getter(instance)

        val bsonValue = run {
            if (schema.canEncode(value))
                @Suppress("UNCHECKED_CAST")
                return@run schema.encode(value as M)

            val encoder = encoders.firstOrNull { it.canEncode(value) }

            if (encoder != null)
                @Suppress("UNCHECKED_CAST")
                return@run encoder.encode(value as M)

            finalEncoder?.let {
                if (it.canEncode(value))
                    @Suppress("UNCHECKED_CAST")
                    return@run it.encode(value as M)
            }

            return /* Skip Encoding */
        }

        document[name] = bsonValue

        onEncode?.let {
            it(FieldDefinitionCodecScope(
                definition = this,
                instance = instance,
                document = document,
                value = value,
                element = bsonValue
            ))
        }
    }

    override fun toString(): String = "FieldDefinition($name, $schema)"
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
        val declaration = this

        val subOptions = fields.flatMap {
            it.obtainStaticOptions(model, pathname, subDejaVu)
        }

        return subOptions + staticOptions.map { option ->
            OptionData(model, pathname, declaration, option)
        }
    }

    @AdvancedMonktApi
    override fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: T
    ): List<OptionData<*, *, *>> {
        val declaration = this

        val subOptions = fields.flatMap {
            it.obtainOptions(model, root, pathname, instance)
        }

        return subOptions + options.map { option ->
            OptionData(model, root, pathname, declaration, instance, instance, option)
        }
    }

    @AdvancedMonktApi
    override fun canDecode(element: BsonElement): Boolean {
        return element is BsonDocument
    }

    @AdvancedMonktApi
    override fun canEncode(value: Any?): Boolean {
        return true
    }

    @AdvancedMonktApi
    override fun decode(element: BsonElement): T {
        element as BsonDocument
        val document = element.toMutableBsonMap()

        val instance = constructor()

        fields.forEach { it.decode(instance, document) }

        onDecode?.let {
            it(ObjectSchemaCodecScope(
                schema = this,
                instance = instance,
                document = document
            ))
        }

        return instance
    }

    @AdvancedMonktApi
    override fun encode(value: T): BsonDocument {
        val document = mutableBsonMapOf()

        fields.forEach { it.encode(value, document) }

        onEncode?.let {
            it(ObjectSchemaCodecScope(
                schema = this,
                instance = value,
                document = document
            ))
        }

        return document.toBsonDocument()
    }

    override fun toString(): String = "ObjectSchema(${fields.joinToString(", ")})"
}
