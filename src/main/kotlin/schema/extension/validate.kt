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

import org.cufy.mangaka.MangakaInvalidation
import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.ObjectSchemaBuilder
import org.cufy.mangaka.schema.SchemaScope
import org.cufy.mangaka.schema.onValidate

/**
 * Indicate a validation error when the given [block]
 * returns false while validating.
 *
 * @param error the error message factory.
 * @param block the validation block.
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.validate(
    error: suspend SchemaScope<O, T>.(T) -> String = {
        "Invalid value for field '$name'"
    },
    block: suspend SchemaScope<O, T>.(T) -> Boolean
) {
    val cause = MangakaInvalidation()

    onValidate { parent, instance, value ->
        parent(this, instance, value) + when {
            block(this, value) -> emptyList()
            else -> {
                val message = "Validation failed for path $path: ${error(this, value)}"
                listOf(MangakaInvalidation(message, cause))
            }
        }
    }
}

/**
 * Indicate a validation error when the given [block]
 * returns false while validating.
 *
 * @param error the error message factory.
 * @param block the validation block.
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.validate(
    error: suspend SchemaScope<*, T>.(T) -> String = {
        "Object validation failed"
    },
    block: suspend SchemaScope<*, T>.(T) -> Boolean
) {
    val cause = MangakaInvalidation()

    onValidate { parent, value ->
        parent(this, value) + when {
            block(this, value) -> emptyList()
            else -> {
                val message = "Validation failed for path $path: ${error(this, value)}"
                listOf(MangakaInvalidation(message, cause))
            }
        }
    }
}
