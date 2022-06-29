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
package org.cufy.mangaka.schema.types

import org.bson.BsonValue
import org.cufy.mangaka.schema.Schema
import org.cufy.mangaka.schema.SchemaScope
import kotlin.reflect.KClass

/**
 * Construct a new enum schema.
 *
 * @param klass the enum class.
 * @return an enum schema.
 * @since 1.0.0
 */
fun <E : Enum<E>> EnumSchema(
    klass: KClass<E>
) = EnumSchema(
    klass = klass,
    schema = StringSchema,
    association = { it.name }
)

/**
 * Construct a new enum schema.
 *
 * @param klass the enum class.
 * @param schema the schema of the enum value.
 * @param association the enum value getter.
 * @return an enum schema.
 * @since 1.0.0
 */
fun <T, E : Enum<E>> EnumSchema(
    klass: KClass<E>,
    schema: Schema<T>,
    association: (E) -> T
) = EnumSchema(
    enums = klass.java.enumConstants.associateBy(association),
    schema = schema
)

/**
 * An schema for enums.
 *
 * @author LSafer
 * @since 1.0.0
 */
class EnumSchema<T, E>(
    /**
     * The enums table.
     *
     * @since 1.0.0
     */
    val enums: Map<T, E>,
    /**
     * The enum value schema.
     *
     * @since 1.0.0
     */
    val schema: Schema<T>
) : Schema<E> {
    override suspend fun serialize(
        scope: SchemaScope<*, E>,
        value: E
    ): BsonValue {
        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        val entry = enums.entries.firstOrNull { it.value == value }
            ?: enums.entries.first()
        val key = entry.key

        return schema.serialize(scope, key)
    }

    override suspend fun deserialize(
        scope: SchemaScope<*, E>,
        bson: BsonValue
    ): E {
        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        val key = schema.deserialize(scope, bson)
        val entry = enums.entries.firstOrNull { it.key == key }
            ?: enums.entries.first()

        return entry.value
    }

    override suspend fun validate(
        scope: SchemaScope<*, E>,
        value: E
    ): List<Throwable> {
        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        val entry = enums.entries.firstOrNull { it.value == value }
            ?: enums.entries.first()
        val key = entry.key

        return schema.validate(scope, key)
    }
}
