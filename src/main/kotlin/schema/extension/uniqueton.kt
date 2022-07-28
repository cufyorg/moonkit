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
package org.cufy.mangaka.schema.extension

import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.ObjectSchemaBuilder
import org.cufy.mangaka.schema.SchemaScope

/**
 * Insure this path is unique.
 *
 * This is achieved by combining:
 * - [FieldDefinitionBuilder.unique]
 * - [FieldDefinitionBuilder.singleton]
 *
 * @param error the error message factory.
 * @since 1.1.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.uniqueton(
    error: suspend SchemaScope<O, T>.(T) -> String = {
        "Duplicate value '$it'"
    }
) {
    unique()
    singleton(error)
}

/**
 * Insures the paths of the given [fields] are unique.
 *
 * This is achieved by combining:
 * - [ObjectSchemaBuilder.unique]
 * - [ObjectSchemaBuilder.singleton]
 *
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.uniqueton(
    error: suspend SchemaScope<*, T>.(T) -> String = {
        "Duplicate value '$it'"
    },
    vararg fields: String
) {
    uniqueton(error, fields.asList())
}

/**
 * Insures the paths of the given [fields] are unique.
 *
 * This is achieved by combining:
 * - [ObjectSchemaBuilder.unique]
 * - [ObjectSchemaBuilder.singleton]
 *
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.uniqueton(
    error: suspend SchemaScope<*, T>.(T) -> String = {
        "Duplicate value '$it'"
    },
    fields: List<String>
) {
    unique(fields)
    singleton(error) { fields }
}
