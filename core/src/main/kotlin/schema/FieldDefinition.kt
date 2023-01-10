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
import org.cufy.monkt.schema.extension.*
import java.util.*
import kotlin.reflect.KMutableProperty1

/**
 * A field definition defines the specification of
 * a field in some object.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface FieldDefinition<T : Any, M> {
    /**
     * The name of the field.
     */
    val name: String

    /**
     * The schema of the field's values.
     */
    val schema: Schema<M>

    /**
     * A getter to get the field's value from the
     * instance.
     */
    val getter: FieldDefinitionGetter<T, M>

    /**
     * A setter to set values to the field at the
     * instance.
     */
    val setter: FieldDefinitionSetter<T, M>

    /**
     * Obtain the static options in this
     * definition.
     *
     * @param model the options model.
     * @param pathname the pathname to this schema.
     * @param dejaVu a set of schemas previously visited.
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun obtainStaticOptions(
        model: Model<*>,
        pathname: Pathname,
        dejaVu: Set<Schema<*>>
    ): List<OptionData<Unit, Unit, *>>

    /**
     * Obtain the options in this definition for
     * the given [instance].
     *
     * @param model the options model.
     * @param root the root instance.
     * @param pathname the pathname to this schema.
     * @param instance the instance to get its options.
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: T
    ): List<OptionData<*, *, *>>

    @AdvancedMonktApi("Called by monkt internally")
    fun decode(instance: T, document: BsonDocument)

    @AdvancedMonktApi("Called by monkt internally")
    fun encode(instance: T, document: BsonDocument)
}

/**
 * The type of a field definition getter.
 */
typealias FieldDefinitionGetter<T, M> =
            (instance: T) -> M

/**
 * The type of a field definition setter.
 */
typealias FieldDefinitionSetter<T, M> =
            (instance: T, value: M) -> Unit

/**
 * A block of code invoked after field definition
 * operations.
 */
typealias FieldDefinitionCodecBlock<T, M> =
        FieldDefinitionCodecScope<T, M>.() -> Unit

/**
 * A block of code invoked after field definition
 * operations with a result.
 */
typealias ReturnFieldDefinitionCodecBlock<T, M, R> =
        FieldDefinitionCodecScope<T, M>.() -> R

/**
 * A block of code invoked to fill in options in
 * [FieldDefinitionBuilder].
 */
typealias FieldDefinitionBuilderBlock<T, M> =
        FieldDefinitionBuilder<T, M>.() -> Unit

/**
 * A block of code invoked to fill in options in
 * [FieldDefinitionMapperBuilder].
 */
@OptIn(ExperimentalMonktApi::class)
typealias FieldDefinitionMapperBuilderBlock<T, M, N> =
        FieldDefinitionMapperBuilder<T, M, N>.() -> Unit

/**
 * A scope passed to field definition operation
 * listeners.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class FieldDefinitionCodecScope<T : Any, M>(
    /**
     * The definition.
     */
    val definition: FieldDefinition<T, M>,
    /**
     * The container instance.
     */
    val instance: T,
    /**
     * The container's source document.
     */
    val document: BsonDocument,
    /**
     * The value.
     */
    val value: M,
    /**
     * The value's source bson.
     */
    val bsonValue: BsonValue
)

/**
 * A builder for creating a [FieldDefinition].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface FieldDefinitionBuilder<T : Any, M> :
    WithOptionsBuilder<T, M>,
    WithCoercersBuilder<M>,
    WithSchemaBuilder<M>,
    WithCodecBuilder<FieldDefinitionCodecBlock<T, M>>,
    WithDeferredBuilder {

    /**
     * The name of the field.
     */
    @AdvancedMonktApi("Use `name()` instead")
    var name: String?  // REQUIRED

    /**
     * A getter to get the field's value from the
     * instance.
     */
    @AdvancedMonktApi("Use `get()` instead")
    var getter: FieldDefinitionGetter<T, M>?  // REQUIRED

    /**
     * A setter to set values to the field at the
     * instance.
     */
    @AdvancedMonktApi("Use `set()` instead")
    var setter: FieldDefinitionSetter<T, M>?  // REQUIRED

    /**
     * Build the definition.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun build(): FieldDefinition<T, M>
}

/**
 * A builder for applying a mapped
 * [FieldDefinition] configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
@ExperimentalMonktApi
interface FieldDefinitionMapperBuilder<T : Any, M, N> :
    FieldDefinitionBuilder<T, N> {
    /**
     * Transforms runtime values of type `M` to `N`
     */
    @AdvancedMonktApi("Use `encodeMapper()` instead")
    var encodeMapper: Mapper<M, N>? // REQUIRED

    /**
     * Transforms runtime values of type `N` to `M`
     */
    @AdvancedMonktApi("Use `decodeMapper()` instead")
    var decodeMapper: Mapper<N, M>? // REQUIRED

    /**
     * Transforms bson values of type `M` to `N`
     */
    @AdvancedMonktApi("Use `bsonEncodeMapper()` instead")
    var bsonEncodeMapper: BsonMapper<M, N>

    /**
     * Transforms bson values of type `N` to `M`
     */
    @AdvancedMonktApi("Use `bsonDecodeMapper()` instead")
    var bsonDecodeMapper: BsonMapper<N, M>

    /**
     * Apply this definition to the given [builder].
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun applyTo(builder: FieldDefinitionBuilder<T, M>)
}

/**
 * Obtain a new [FieldDefinitionBuilder].
 *
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T : Any, M> FieldDefinitionBuilder(): FieldDefinitionBuilder<T, M> {
    return FieldDefinitionBuilderImpl()
}

/**
 * Obtain a new [FieldDefinitionMapperBuilder].
 *
 * @since 2.0.0
 */
@ExperimentalMonktApi
@OptIn(InternalMonktApi::class)
fun <T : Any, M, N> FieldDefinitionMapperBuilder(): FieldDefinitionMapperBuilder<T, M, N> {
    return FieldDefinitionMapperBuilderImpl()
}

/**
 * Construct a new [FieldDefinition] with the
 * given [block].
 *
 * @param name the initial name.
 * @param schema the initial schema.
 * @param block the builder block.
 * @return a new field definition.
 * @since 2.0.0
 */
fun <T : Any, M> FieldDefinition(
    name: String? = null,
    schema: Schema<M>? = null,
    block: FieldDefinitionBuilderBlock<T, M> = {}
): FieldDefinition<T, M> {
    val builder = FieldDefinitionBuilder<T, M>()
    name?.let { builder.name(it) }
    schema?.let { builder.schema { it } }
    builder.apply(block)
    return builder.build()
}

/**
 * Construct a new [FieldDefinition] with the
 * given [property] and [block].
 *
 * @param property the property.
 * @param schema the initial schema.
 * @param block the builder block.
 * @return a new field definition.
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun <T : Any, M> PropertyFieldDefinition(
    property: KMutableProperty1<in T, M>,
    schema: Schema<M>? = null,
    block: FieldDefinitionBuilderBlock<T, M> = {}
): FieldDefinition<T, M> {
    val builder = FieldDefinitionBuilder<T, M>()
    schema?.let { builder.schema { it } }
    builder.property(property)
    builder.apply(block)
    return builder.build()
}

/**
 * Construct a new [FieldDefinition] bound to a
 * fantom property.
 *
 * @param name the name of the weak property.
 * @param schema the initial schema.
 * @param block the builder block.
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun <T : Any, M> FantomFieldDefinition(
    name: String,
    schema: Schema<M>? = null,
    block: FieldDefinitionBuilderBlock<T, M> = {}
): FieldDefinition<T, M> {
    val builder = FieldDefinitionBuilder<T, M>()
    schema?.let { builder.schema { it } }
    builder.fantom(name)
    builder.apply(block)
    return builder.build()
}

//

/**
 * Set the name, setter and getter of the definition
 * from the given [property].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> FieldDefinitionBuilder<T, M>.property(
    property: KMutableProperty1<in T, M>
) {
    this.name = property.name
    this.getter = property.getter
    this.setter = property.setter
}

/**
 * Set the name, setter and getter of the definition
 * from a fantom property with the given [name].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> FieldDefinitionBuilder<T, M>.fantom(
    name: String
) {
    this.name = name
    this.getter = { instance ->
        Document[instance, name]
    }
    this.setter = { instance, value ->
        Document[instance, name] = value
    }
}

// name

/**
 * Set the field's name to be the given [name]
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> FieldDefinitionBuilder<T, M>.name(
    name: String
) {
    this.name = name
}

// getter

/**
 * Set the getter to be the given [block]
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> FieldDefinitionBuilder<T, M>.get(
    block: FieldDefinitionGetter<T, M>
) {
    getter = block
}

// setter

/**
 * Set the setter to be the given [block]
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> FieldDefinitionBuilder<T, M>.set(
    block: FieldDefinitionSetter<T, M>
) {
    setter = block
}

// encodeMapper

/**
 * Set the given [block] to be the value encode
 * mapper.
 */
@ExperimentalMonktApi
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M, N> FieldDefinitionMapperBuilder<T, M, N>.encodeMapper(
    block: Mapper<M, N>
) {
    encodeMapper = block
}

// decodeMapper

/**
 * Set the given [block] to be the value decode
 * mapper.
 */
@ExperimentalMonktApi
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M, N> FieldDefinitionMapperBuilder<T, M, N>.decodeMapper(
    block: Mapper<N, M>
) {
    decodeMapper = block
}

// bsonEncodeMapper

/**
 * Set the given [block] to be the bson value
 * encode mapper.
 */
@ExperimentalMonktApi
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M, N> FieldDefinitionMapperBuilder<T, M, N>.bsonEncodeMapper(
    block: BsonMapper<M, N>
) {
    bsonEncodeMapper = block
}

// bsonDecodeMapper

/**
 * Set the given [block] to be the bson value
 * decode mapper.
 */
@ExperimentalMonktApi
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M, N> FieldDefinitionMapperBuilder<T, M, N>.bsonDecodeMapper(
    block: BsonMapper<N, M>
) {
    bsonDecodeMapper = block
}

//

/**
 * Discard the encoded value of the field when
 * the given [block] returns true.
 */
fun <T : Any, M> FieldDefinitionBuilder<T, M>.discardIf(
    block: ReturnFieldDefinitionCodecBlock<T, M, Boolean>
) {
    onEncode {
        if (block()) {
            document.remove(definition.name)
        }
    }
}

/**
 * Discard the encoded value of the field when the
 * root instance is not new.
 *
 * @param block a block to make this field immutable conditionally.
 * @since 2.0.0
 */
fun <T : Any, M> FieldDefinitionBuilder<T, M>.immutable(
    block: ReturnFieldDefinitionCodecBlock<T, M, Boolean> = { true }
) {
    val isMutableFieldName = "_mutable_" + UUID.randomUUID().toString()

    normalization {
        Document[instance, isMutableFieldName] =
            Document.isNew(root)
    }

    discardIf {
        val shouldBeImmutable = block()

        if (!shouldBeImmutable)
            return@discardIf false

        val isMutable: Any? = Document[instance, isMutableFieldName]

        isMutable != true
    }
}

/**
 * Unset the field from the database when the
 * given [block] returns true.
 */
fun <T : Any, M> FieldDefinitionBuilder<T, M>.unsetIf(
    block: ReturnOptionBlock<T, M, WritesConfiguration, Boolean>
) {
    writes {
        val shouldUnset = block(it)

        if (shouldUnset) {
            update({
                `$unset` by {
                    "$pathname" by bnull
                }
            })
        }
    }
}

/**
 * Unset the field from the database when the
 * field's value is null.
 *
 * Note: this is an option not an encoding
 * extension. Therefore, the check will be for if
 * the runtime value is null rather than if the
 * encoded value is null.
 */
fun <T : Any, M> FieldDefinitionBuilder<T, M?>.unsetIfNull() {
    unsetIf { value == null }
}

/**
 * Apply the configuration in the given [block] to
 * this field definition.
 *
 * Required:
 * - [encodeMapper]
 * - [decodeMapper]
 *
 * @param block the mapped block.
 * @since 2.0.0
 */
@ExperimentalMonktApi
fun <T : Any, M, N> FieldDefinitionBuilder<T, M>.map(
    block: FieldDefinitionMapperBuilderBlock<T, M, N>
) {
    val builder = FieldDefinitionMapperBuilder<T, M, N>()
    builder.apply(block)
    builder.applyTo(this)
}
