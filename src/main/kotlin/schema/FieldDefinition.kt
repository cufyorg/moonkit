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
import org.bson.BsonUndefined
import kotlin.collections.set
import kotlin.reflect.KMutableProperty1

/**
 * A field definition defines the specification of
 * a field in some object.
 *
 * @author LSafer
 * @since 1.0.0
 */
interface FieldDefinition<O : Any, T> {
    /**
     * The name of the field.
     *
     * @since 1.0.0
     */
    val name: String

    /**
     * The schema of the value of the field.
     *
     * @since 1.0.0
     */
    val schema: Schema<T>

    /**
     * Get the value of the field in the [instance].
     *
     * @param scope the execution scope.
     * @param instance the parent object.
     * @return the field value.
     * @since 1.0.0
     */
    suspend fun get(
        scope: SchemaScope<O, T>,
        instance: O
    ): T

    /**
     * Set the value of the field in the [instance]
     * to the given [value].
     *
     * @param scope the execution scope.
     * @param instance the parent object.
     * @param value the field value.
     * @since 1.0.0
     */
    suspend fun set(
        scope: SchemaScope<O, T>,
        instance: O,
        value: T
    )

    /**
     * Serialize the given [value] at the field in
     * the [instance] to the [document].
     *
     * @param scope the execution scope.
     * @param document the database document.
     * @param instance the parent object.
     * @param value the field value.
     * @since 1.0.0
     */
    suspend fun serialize(
        scope: SchemaScope<O, T>,
        document: BsonDocument,
        instance: O,
        value: T
    ) {
        val bson = schema.serialize(scope, value)
        document[name] = bson
    }

    /**
     * Deserialize the value at the field in the
     * [instance] from the [document].
     *
     * @param scope the execution scope.
     * @param document the database document.
     * @param instance the parent object.
     * @return the field value.
     * @since 1.0.0
     */
    suspend fun deserialize(
        scope: SchemaScope<O, T>,
        document: BsonDocument,
        instance: O
    ): T {
        val bson = document[name] ?: BsonUndefined()
        return schema.deserialize(scope, bson)
    }

    /**
     * Validate the given [value] at the field in
     * the [instance].
     *
     * @param scope the execution scope.
     * @param instance the parent object.
     * @param value the field value.
     * @return validation errors.
     * @since 1.0.0
     */
    suspend fun validate(
        scope: SchemaScope<O, T>,
        instance: O,
        value: T
    ): List<Throwable> {
        return schema.validate(scope, value)
    }
}

/**
 * A builder for creating a [FieldDefinition].
 *
 * @author LSafer
 * @since 1.0.0
 */
open class FieldDefinitionBuilder<O : Any, T> {
    /**
     * The name of the field.
     */
    lateinit var name: String

    /**
     * The schema of the value of the field.
     *
     * @since 1.0.0
     */
    lateinit var schema: Schema<T>

    /**
     * The implementation of [FieldDefinition.get].
     */
    lateinit var getter: suspend (SchemaScope<O, T>, O) -> T

    /**
     * The implementation of [FieldDefinition.set].
     */
    lateinit var setter: suspend (SchemaScope<O, T>, O, T) -> Unit

    /**
     * A custom serializer overriding [FieldDefinition.serialize].
     *
     * @since 1.0.0
     */
    var customSerializer: suspend (
        suspend (SchemaScope<O, T>, BsonDocument, O, T) -> Unit,
        SchemaScope<O, T>, BsonDocument, O, T
    ) -> Unit = { p, s, d, i, v -> p(s, d, i, v) }

    /**
     * A custom deserializer overriding [FieldDefinition.deserialize].
     *
     * @since 1.0.0
     */
    var customDeserializer: suspend (
        suspend (SchemaScope<O, T>, BsonDocument, O) -> T,
        SchemaScope<O, T>, BsonDocument, O
    ) -> T = { p, s, d, i -> p(s, d, i) }

    /**
     * A custom validator overriding [FieldDefinition.validate].
     *
     * @since 1.0.0
     */
    var customValidator: suspend (
        suspend (SchemaScope<O, T>, O, T) -> List<Throwable>,
        SchemaScope<O, T>, O, T
    ) -> List<Throwable> = { p, s, i, v -> p(s, i, v) }

    /**
     * Build the definition.
     *
     * @since 1.0.0
     */
    fun build(): FieldDefinition<O, T> {
        val _name = this.name
        val _schema = this.schema
        val _getter = this.getter
        val _setter = this.setter
        val _customSerializer = this.customSerializer
        val _customDeserializer = this.customDeserializer
        val _customValidator = this.customValidator
        return object : FieldDefinition<O, T> {
            override val name = _name

            override val schema = _schema

            override suspend fun get(
                scope: SchemaScope<O, T>,
                instance: O
            ) = _getter(scope, instance)

            override suspend fun set(
                scope: SchemaScope<O, T>,
                instance: O,
                value: T
            ) = _setter(scope, instance, value)

            override suspend fun serialize(
                scope: SchemaScope<O, T>,
                document: BsonDocument,
                instance: O,
                value: T
            ) = _customSerializer(
                { s, d, i, v -> super.serialize(s, d, i, v) },
                scope, document, instance, value
            )

            override suspend fun deserialize(
                scope: SchemaScope<O, T>,
                document: BsonDocument,
                instance: O
            ) = _customDeserializer(
                { s, d, i -> super.deserialize(s, d, i) },
                scope, document, instance
            )

            override suspend fun validate(
                scope: SchemaScope<O, T>,
                instance: O,
                value: T
            ) = _customValidator(
                { s, i, v -> super.validate(s, i, v) },
                scope, instance, value
            )
        }
    }
}

/**
 * Construct a new [FieldDefinition] from a kotlin
 * property with the given [block].
 *
 * @param property the property.
 * @param schema the initial schema.
 * @param block the builder block.
 * @return a new field definition.
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinition(
    property: KMutableProperty1<in O, T>,
    schema: Schema<out T>? = null,
    block: FieldDefinitionBuilder<O, T>.() -> Unit = {}
): FieldDefinition<O, T> {
    return FieldDefinition(property.name, schema) {
        get { property.get(self) }
        set { property.set(self, it) }
        block()
    }
}

/**
 * Construct a new [FieldDefinition] with the
 * given [block].
 *
 * @param name the initial name.
 * @param schema the initial schema.
 * @param block the builder block.
 * @return a new field definition.
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinition(
    name: String? = null,
    schema: Schema<out T>? = null,
    block: FieldDefinitionBuilder<O, T>.() -> Unit = {}
): FieldDefinition<O, T> {
    val builder = FieldDefinitionBuilder<O, T>()
    name?.let { builder.name = it }
    schema?.let {
        @Suppress("UNCHECKED_CAST")
        builder.schema = it as Schema<T>
    }
    builder.apply(block)
    return builder.build()
}

/**
 * Set the field schema to the result of [block].
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.schema(
    block: () -> Schema<out T>
) {
    @Suppress("UNCHECKED_CAST")
    schema = block() as Schema<T>
}

/**
 * Set the setter to the given [block].
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.set(
    block: suspend SchemaScope<O, T>.(T) -> Unit
) {
    setter = { scope, _, value -> block(scope, value) }
}

/**
 * Set the getter to the given [block].
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.get(
    block: suspend SchemaScope<O, T>.() -> T
) {
    getter = { scope, _ -> block(scope) }
}

/**
 * Wrap the custom serializer with the given [block].
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.onSerialize(
    block: suspend SchemaScope<O, T>.(
        suspend (SchemaScope<O, T>, BsonDocument, O, T) -> Unit,
        BsonDocument, O, T
    ) -> Unit
) {
    customSerializer.let {
        customSerializer = { parent, scope, document, instance, value ->
            block(
                scope,
                { s, d, i, v -> it(parent, s, d, i, v) },
                document, instance, value
            )
        }
    }
}

/**
 * Wrap the custom deserializer with the given [block].
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.onDeserialize(
    block: suspend SchemaScope<O, T>.(
        suspend (SchemaScope<O, T>, BsonDocument, O) -> T,
        BsonDocument, O
    ) -> T
) {
    customDeserializer.let {
        customDeserializer = { parent, scope, document, instance ->
            block(
                scope,
                { s, d, i -> it(parent, s, d, i) },
                document, instance
            )
        }
    }
}

/**
 * Wrap the custom validator with the given [block].
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.onValidate(
    block: suspend SchemaScope<O, T>.(
        suspend (SchemaScope<O, T>, O, T) -> List<Throwable>,
        O, T
    ) -> List<Throwable>
) {
    customValidator.let {
        customValidator = { parent, scope, instance, value ->
            block(
                scope,
                { s, i, v -> it(parent, s, i, v) },
                instance, value
            )
        }
    }
}
