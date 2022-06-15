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
package org.cufy.mangaka.extension

import org.cufy.mangaka.MangakaInvalidation
import org.cufy.mangaka.Schema
import org.cufy.mangaka.SchemaScope
import org.cufy.mangaka.onValidate

/**
 * Add the given validator [function] to this schema.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.validate(
    message: String,
    function: SchemaScope<D, O, T>.(T?) -> Boolean
) {
    validate({ message }, function)
}

/**
 * Add the given validator [function] to this schema.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.validate(
    message: SchemaScope<D, O, T>.(T?) -> String = {
        "Validation failed for path $path"
    },
    function: suspend SchemaScope<D, O, T>.(T?) -> Boolean
) {
    onValidate { value, fallback ->
        fallback(this, value) + run {
            when (function(value)) {
                true -> emptyList()
                false -> listOf(MangakaInvalidation(message(value)))
            }
        }
    }
}
