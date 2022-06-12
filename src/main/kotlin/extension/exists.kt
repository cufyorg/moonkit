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

import com.mongodb.client.model.Filters.eq
import org.cufy.mangaka.*

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 * The given [function] is used to define where to
 * search for the document.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, Id<T>>.exists(
    message: String,
    function: suspend SchemaScope<D, O, Id<T>>.(Id<T>?) -> Model<T & Any>
) {
    exists({ message }, function)
}

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 * The given [function] is used to define where to
 * search for the document.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, Id<T>>.exists(
    message: SchemaScope<D, O, Id<T>>.(Id<T>?) -> String = {
        "Validation failed for path $path: Document not found with id $it"
    },
    function: suspend SchemaScope<D, O, Id<T>>.(Id<T>?) -> Model<T & Any>
) {
    onValidate { value, validator ->
        validator(value) + run {
            value?.takeIf { _exists(function(value), value) }
                ?.let { emptyList() }
                ?: listOf(MangakaInvalidation(message(value)))
        }
    }
}

// internal

internal suspend fun <T> _exists(model: Model<T & Any>, id: Id<T>): Boolean {
    return model.exists(eq("_id", id.normal))
}
