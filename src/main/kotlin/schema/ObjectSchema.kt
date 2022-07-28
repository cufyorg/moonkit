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
package org.cufy.mangaka.schema

import org.bson.BsonDocument
import org.bson.BsonValue
import kotlin.reflect.KMutableProperty1

/**
 * A schema specification for objects.
 *
 * @author LSafer
 * @since 1.0.0
 */
interface ObjectSchema<T : Any> : Schema<T> {
    /**
     * The fields in the object.
     *
     * @since 1.0.0
     */
    val fields: List<FieldDefinition<T, Any?>>

    /**
     * Construct a new instance of [T].
     *
     * @since 1.0.0
     */
    suspend fun construct(): T

    override suspend fun serialize(
        scope: SchemaScope<*, T>,
        value: T
    ): BsonValue {
        val instance = value
        val document = BsonDocument()
        fields
            .associateWith { field ->
                SchemaScope<T, Any?>(scope) {
                    self = instance
                    name = field.name
                    schema = field.schema
                }
            }
            .filter { (field, fieldScope) ->
                fieldScope.pathname !in scope.skip
            }
            .forEach { (field, fieldScope) ->
                val fieldValue = field.get(fieldScope, instance)

                field.serialize(fieldScope, document, instance, fieldValue)
            }
        return document
    }

    override suspend fun deserialize(
        scope: SchemaScope<*, T>,
        bson: BsonValue
    ): T {
        val document = bson as? BsonDocument ?: BsonDocument()
        val instance = construct()
        fields
            .associateWith { field ->
                SchemaScope<T, Any?>(scope) {
                    self = instance
                    name = field.name
                    schema = field.schema
                }
            }
            .filter { (field, fieldScope) ->
                fieldScope.pathname !in scope.skip
            }
            .forEach { (field, fieldScope) ->
                val fieldValue = field.deserialize(fieldScope, document, instance)

                field.set(fieldScope, instance, fieldValue)
            }
        return instance
    }

    override suspend fun validate(
        scope: SchemaScope<*, T>,
        value: T
    ): List<Throwable> {
        val instance = value
        return fields
            .associateWith { field ->
                SchemaScope<T, Any?>(scope) {
                    self = instance
                    name = field.name
                    schema = field.schema
                }
            }
            .filter { (field, fieldScope) ->
                fieldScope.pathname !in scope.skip
            }
            .flatMap { (field, fieldScope) ->
                val fieldValue = field.get(fieldScope, instance)

                field.validate(fieldScope, instance, fieldValue)
            }
    }
}

/**
 * A builder for creating an [ObjectSchema].
 *
 * @author LSafer
 * @since 1.0.0
 */
open class ObjectSchemaBuilder<T : Any> {
    /**
     * The fields in the object.
     *
     * @since 1.0.0
     */
    val fields: MutableList<FieldDefinition<T, Any?>> = mutableListOf()

    /**
     * A constructor for creating a new instance of [T].
     *
     * @since 1.0.0
     */
    lateinit var constructor: suspend () -> T

    /**
     * A custom serializer overriding [ObjectSchema.serialize].
     *
     * @since 1.0.0
     */
    var customSerializer: suspend (
        suspend (SchemaScope<*, T>, T) -> BsonValue,
        SchemaScope<*, T>, T
    ) -> BsonValue = { p, s, v -> p(s, v) }

    /**
     * A custom deserializer overriding [ObjectSchema.deserialize].
     *
     * @since 1.0.0
     */
    var customDeserializer: suspend (
        suspend (SchemaScope<*, T>, BsonValue) -> T,
        SchemaScope<*, T>, BsonValue
    ) -> T = { p, s, b -> p(s, b) }

    /**
     * A custom validator overriding [ObjectSchema.validate].
     *
     * @since 1.0.0
     */
    var customValidator: suspend (
        suspend (SchemaScope<*, T>, T) -> List<Throwable>,
        SchemaScope<*, T>, T
    ) -> List<Throwable> = { p, s, v -> p(s, v) }

    /**
     * Build the schema.
     *
     * @since 1.0.0
     */
    fun build(): ObjectSchema<T> {
        val _fields = this.fields
        val _constructor = this.constructor
        val _customSerializer = this.customSerializer
        val _customDeserializer = this.customDeserializer
        val _customValidator = this.customValidator
        return object : ObjectSchema<T> {
            override val fields = _fields

            override suspend fun construct() =
                _constructor()

            override suspend fun serialize(
                scope: SchemaScope<*, T>,
                value: T
            ) = _customSerializer(
                { s, v -> super.serialize(s, v) },
                scope, value
            )

            override suspend fun deserialize(
                scope: SchemaScope<*, T>,
                bson: BsonValue
            ) = _customDeserializer(
                { s, b -> super.deserialize(s, b) },
                scope, bson
            )

            override suspend fun validate(
                scope: SchemaScope<*, T>,
                value: T
            ) = _customValidator(
                { s, v -> super.validate(s, v) },
                scope, value
            )
        }
    }
}

/**
 * Construct a new [ObjectSchema] with the
 * given [block].
 *
 * @param constructor the initial constructor.
 * @param block the builder block.
 * @return a new object schema.
 * @since 1.0.0
 */
fun <T : Any> ObjectSchema(
    constructor: (suspend () -> T)? = null,
    block: ObjectSchemaBuilder<T>.() -> Unit = {}
): ObjectSchema<T> {
    val builder = ObjectSchemaBuilder<T>()
    constructor?.let { builder.constructor = it }
    builder.apply(block)
    return builder.build()
}

/**
 * Add a field from a kotlin property.
 *
 * @param property the property.
 * @param schema the initial schema.
 * @param block the builder block.
 * @since 1.0.0
 */
fun <T : Any, M> ObjectSchemaBuilder<T>.field(
    property: KMutableProperty1<in T, M>,
    schema: Schema<out M>? = null,
    block: FieldDefinitionBuilder<T, M>.() -> Unit = {}
) {
    @Suppress("UNCHECKED_CAST")
    this.fields.add(FieldDefinition(
        property = property,
        schema = schema,
        block = block
    ) as FieldDefinition<T, Any?>)
}

/**
 * Add a field with the given arguments.
 *
 * @param name the initial name.
 * @param schema the initial schema.
 * @param block the builder block.
 * @since 1.0.0
 */
fun <T : Any, M> ObjectSchemaBuilder<T>.field(
    name: String? = null,
    schema: Schema<out M>? = null,
    block: FieldDefinitionBuilder<T, M>.() -> Unit = {}
) {
    @Suppress("UNCHECKED_CAST")
    this.fields.add(FieldDefinition(
        name = name,
        schema = schema,
        block = block
    ) as FieldDefinition<T, Any?>)
}

/**
 * Set the constructor to the given [block].
 *
 * @since 1.0.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.construct(
    block: suspend () -> T
) {
    this.constructor = block
}

/**
 * Prepend the custom serializer with the given [block].
 *
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.onSerialize(
    block: suspend SchemaScope<*, T>.(T) -> Unit
) {
    onSerialize { parent, value ->
        block(this, value)
        parent(this, value)
    }
}

/**
 * Wrap the custom serializer with the given [block].
 *
 * @since 1.0.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.onSerialize(
    block: suspend SchemaScope<*, T>.(
        suspend (SchemaScope<*, T>, T) -> BsonValue,
        T
    ) -> BsonValue
) {
    customSerializer.let {
        customSerializer = { parent, scope, value ->
            block(
                scope,
                { s, v -> it(parent, s, v) },
                value
            )
        }
    }
}

/**
 * Prepend the custom deserializer with the given [block].
 *
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.onDeserialize(
    block: suspend SchemaScope<*, T>.(BsonValue) -> Unit
) {
    onDeserialize { parent, bson ->
        block(this, bson)
        parent(this, bson)
    }
}

/**
 * Wrap the custom deserializer with the given [block].
 *
 * @since 1.0.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.onDeserialize(
    block: suspend SchemaScope<*, T>.(
        suspend (SchemaScope<*, T>, BsonValue) -> T,
        BsonValue
    ) -> T
) {
    customDeserializer.let {
        customDeserializer = { parent, scope, bson ->
            block(
                scope,
                { s, b -> it(parent, s, b) },
                bson
            )
        }
    }
}

/**
 * Prepend the custom validator with the given [block].
 *
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.onValidate(
    block: suspend SchemaScope<*, T>.(T) -> Unit
) {
    onValidate { parent, value ->
        block(this, value)
        parent(this, value)
    }
}

/**
 * Wrap the custom validator with the given [block].
 *
 * @since 1.0.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.onValidate(
    block: suspend SchemaScope<*, T>.(
        suspend (SchemaScope<*, T>, T) -> List<Throwable>,
        T
    ) -> List<Throwable>
) {
    customValidator.let {
        customValidator = { parent, scope, value ->
            block(
                scope,
                { s, v -> it(parent, s, v) },
                value
            )
        }
    }
}
