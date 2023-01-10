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
import kotlin.reflect.KMutableProperty1

/**
 * A schema specification for objects.
 *
 * @param T the runtime instance.
 * @author LSafer
 * @since 2.0.0
 */
interface ObjectSchema<T : Any> : ElementSchema<T> {
    /**
     * A function construct a new instance of [T].
     */
    val constructor: ObjectSchemaConstructor<T>

    /**
     * The field definitions.
     */
    val fields: List<FieldDefinition<T, *>>

    @AdvancedMonktApi
    override fun encode(value: T): BsonDocument
}

/**
 * The type of object schema constructor function.
 */
typealias ObjectSchemaConstructor<T> =
            () -> T

/**
 * A block of code invoked after object schema
 * operations.
 */
typealias ObjectSchemaCodecBlock<T> =
        ObjectSchemaCodecScope<T>.() -> Unit

/**
 * A block of code invoked after object schema
 * operations with a return.
 */
typealias ReturnObjectSchemaCodecBlock<T, R> =
        ObjectSchemaCodecScope<T>.() -> R

/**
 * A block of code invoked to fill in options in
 * [ObjectSchemaBuilder].
 */
typealias ObjectSchemaBuilderBlock<T> =
        ObjectSchemaBuilder<T>.() -> Unit

/**
 * A scope passed to object schema operation
 * listeners.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ObjectSchemaCodecScope<T : Any>(
    /**
     * The schema.
     */
    val schema: ObjectSchema<T>,
    /**
     * The instance.
     */
    val instance: T,
    /**
     * The source document.
     */
    val document: BsonDocument
)

/**
 * A builder for creating an [ObjectSchema].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface ObjectSchemaBuilder<T : Any> :
    WithOptionsBuilder<T, T>,
    WithCodecBuilder<ObjectSchemaCodecBlock<T>>,
    WithDeferredBuilder {

    /**
     * The object constructor.
     */
    @AdvancedMonktApi("Use `construct()` instead")
    var constructor: ObjectSchemaConstructor<T>? // REQUIRED

    /**
     * The field definitions.
     */
    @AdvancedMonktApi("Use `field()` instead")
    val fields: MutableList<FieldDefinition<T, *>>

    /**
     * Build the schema.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun build(): ObjectSchema<T>
}

/**
 * Obtain a new [ObjectSchemaBuilder].
 *
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T : Any> ObjectSchemaBuilder(): ObjectSchemaBuilder<T> {
    return ObjectSchemaBuilderImpl()
}

/**
 * Construct a new [ObjectSchema] with the
 * given [block].
 *
 * @param constructor the initial constructor.
 * @param block the builder block.
 * @return a new object schema.
 * @since 2.0.0
 */
fun <T : Any> ObjectSchema(
    constructor: ObjectSchemaConstructor<T>? = null,
    block: ObjectSchemaBuilderBlock<T> = {}
): ObjectSchema<T> {
    val builder = ObjectSchemaBuilder<T>()
    constructor?.let { builder.construct(it) }
    builder.apply(block)
    return builder.build()
}

// constructor

/**
 * Set the constructor to be the given [block].
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any> ObjectSchemaBuilder<T>.construct(
    block: ObjectSchemaConstructor<T>
) {
    constructor = block
}

// fields

/**
 * Add the given field [definition].
 *
 * @param definition the field definition to be added.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> ObjectSchemaBuilder<T>.field(
    definition: FieldDefinition<T, M>
) {
    fields += definition
}

/**
 * Add a field with the given arguments.
 *
 * @param name the initial name.
 * @param schema the initial schema.
 * @param block the builder block.
 * @since 2.0.0
 */
fun <T : Any, M> ObjectSchemaBuilder<T>.field(
    name: String? = null,
    schema: Schema<M>? = null,
    block: FieldDefinitionBuilderBlock<T, M> = {}
) {
    field(FieldDefinition(name, schema, block))
}

/**
 * Add a field with the given arguments.
 *
 * @param property the property.
 * @param schema the initial schema.
 * @param block the builder block.
 * @since 2.0.0
 */
fun <T : Any, M> ObjectSchemaBuilder<T>.field(
    property: KMutableProperty1<in T, M>,
    schema: Schema<M>? = null,
    block: FieldDefinitionBuilderBlock<T, M> = {}
) {
    field(PropertyFieldDefinition(property, schema, block))
}

/**
 * Add a fantom field with the given arguments.
 *
 * @param name the name of the fantom property.
 * @param schema the initial schema.
 * @param block the builder block.
 * @since 2.0.0
 */
fun <T : Any, M> ObjectSchemaBuilder<T>.fantom(
    name: String,
    schema: Schema<M>? = null,
    block: FieldDefinitionBuilderBlock<T, M> = {}
) {
    field(FantomFieldDefinition(name, schema, block))
}
