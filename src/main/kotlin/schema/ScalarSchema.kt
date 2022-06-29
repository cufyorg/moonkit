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
 * A scalar specifies how to serialize/deserialize
 * a primitive value.
 *
 * @author LSafer
 * @since 1.0.0
 */
interface ScalarSchema<T : Any> : Schema<T> {
    override suspend fun validate(
        scope: SchemaScope<*, T>,
        value: T
    ): List<Throwable> {
        return emptyList()
    }
}

/**
 * A builder for creating a [ScalarSchema].
 *
 * @author LSafer
 * @since 1.0.0
 */
open class ScalarSchemaBuilder<T : Any> {
    /**
     * A function to serialize the value into bson.
     *
     * @since 1.0.0
     */
    lateinit var serializer: suspend (SchemaScope<*, T>, T) -> BsonValue

    /**
     * A function to deserialize the value from bson.
     *
     * @since 1.0.0
     */
    lateinit var deserializer: suspend (SchemaScope<*, T>, BsonValue) -> T

    /**
     * Build the schema.
     *
     * @since 1.0.0
     */
    fun build(): ScalarSchema<T> {
        val _serializer = this.serializer
        val _deserializer = this.deserializer
        return object : ScalarSchema<T> {
            override suspend fun serialize(
                scope: SchemaScope<*, T>,
                value: T
            ) = _serializer(scope, value)

            override suspend fun deserialize(
                scope: SchemaScope<*, T>,
                bson: BsonValue
            ) = _deserializer(scope, bson)
        }
    }
}

/**
 * Construct a new [ScalarSchema] with the
 * given [block].
 *
 * @param block the builder block.
 * @return a new scalar schema.
 * @since 1.0.0
 */
fun <T : Any> ScalarSchema(
    block: ScalarSchemaBuilder<T>.() -> Unit = {}
): ScalarSchema<T> {
    val builder = ScalarSchemaBuilder<T>()
    builder.apply(block)
    return builder.build()
}

/**
 * Set the serializer to the given [block].
 *
 * @since 1.0.0
 */
fun <T : Any> ScalarSchemaBuilder<T>.serialize(
    block: suspend SchemaScope<*, T>.(T) -> BsonValue
) {
    serializer = block
}

/**
 * Set the deserializer to the given [block].
 *
 * @since 1.0.0
 */
fun <T : Any> ScalarSchemaBuilder<T>.deserialize(
    block: suspend SchemaScope<*, T>.(BsonValue) -> T
) {
    deserializer = block
}
