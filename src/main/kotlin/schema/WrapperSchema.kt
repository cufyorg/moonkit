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

import org.bson.BsonValue

/**
 * A schema wrapping another schema.
 *
 * @author LSafer
 * @since 1.0.0
 */
interface WrapperSchema<T> : Schema<T> {
    /**
     * The wrapped schema.
     *
     * @since 1.0.0
     */
    val schema: Schema<T>

    override suspend fun serialize(
        scope: SchemaScope<*, T>,
        value: T
    ): BsonValue {
        return schema.serialize(scope, value)
    }

    override suspend fun deserialize(
        scope: SchemaScope<*, T>,
        bson: BsonValue
    ): T {
        return schema.deserialize(scope, bson)
    }

    override suspend fun validate(
        scope: SchemaScope<*, T>,
        value: T
    ): List<Throwable> {
        return schema.validate(scope, value)
    }
}

/**
 * A builder for creating a [WrapperSchema].
 *
 * @author LSafer
 * @since 1.0.0
 */
open class WrapperSchemaBuilder<T> {
    /**
     * The schema to be wrapped.
     *
     * @since 1.0.0
     */
    lateinit var schema: Schema<T>

    /**
     * A custom serializer overriding [WrapperSchema.serialize].
     *
     * @since 1.0.0
     */
    var customSerializer: suspend (
        suspend (SchemaScope<*, T>, T) -> BsonValue,
        SchemaScope<*, T>, T
    ) -> BsonValue = { p, e, v -> p(e, v) }

    /**
     * A custom deserializer overriding [WrapperSchema.deserialize].
     *
     * @since 1.0.0
     */
    var customDeserializer: suspend (
        suspend (SchemaScope<*, T>, BsonValue) -> T,
        SchemaScope<*, T>, BsonValue
    ) -> T = { p, e, b -> p(e, b) }

    /**
     * A custom validator overriding [WrapperSchema.validate].
     *
     * @since 1.0.0
     */
    var customValidator: suspend (
        suspend (SchemaScope<*, T>, T) -> List<Throwable>,
        SchemaScope<*, T>, T
    ) -> List<Throwable> = { p, e, v -> p(e, v) }

    /**
     * Build the schema.
     *
     * @since 1.0.0
     */
    fun build(): WrapperSchema<T> {
        val _schema = this.schema
        val _customSerializer = this.customSerializer
        val _customDeserializer = this.customDeserializer
        val _customValidator = this.customValidator
        return object : WrapperSchema<T> {
            override val schema: Schema<T> = _schema

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
 * Construct a new [WrapperSchema] with the
 * given [block].
 *
 * @param schema the initial schema.
 * @param block the builder block.
 * @return a new wrapper schema.
 * @since 1.0.0
 */
fun <T> WrapperSchema(
    schema: Schema<out T>? = null,
    block: WrapperSchemaBuilder<T>.() -> Unit = {}
): WrapperSchema<T> {
    val builder = WrapperSchemaBuilder<T>()
    schema?.let {
        @Suppress("UNCHECKED_CAST")
        builder.schema = it as Schema<T>
    }
    builder.apply(block)
    return builder.build()
}

/**
 * Set the wrapped schema to the result of [block].
 *
 * @since 1.0.0
 */
fun <T> WrapperSchemaBuilder<T>.schema(
    block: () -> Schema<out T>
) {
    @Suppress("UNCHECKED_CAST")
    schema = block() as Schema<T>
}

/**
 * Prepend the custom serializer with the given [block].
 *
 * @since 1.1.0
 */
fun <T> WrapperSchemaBuilder<T>.onSerialize(
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
fun <T> WrapperSchemaBuilder<T>.onSerialize(
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
fun <T> WrapperSchemaBuilder<T>.onDeserialize(
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
fun <T> WrapperSchemaBuilder<T>.onDeserialize(
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
fun <T> WrapperSchemaBuilder<T>.onValidate(
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
fun <T> WrapperSchemaBuilder<T>.onValidate(
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
