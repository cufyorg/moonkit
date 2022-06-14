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
import org.cufy.mangaka.Id
import org.cufy.mangaka.Model
import org.cufy.mangaka.Schema
import org.cufy.mangaka.SchemaScope
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine

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
    message: SchemaScope<D, O, Id<T>>.(Id<T>) -> String = {
        "Validation failed for path $path: Document not found with id $it"
    },
    function: suspend SchemaScope<D, O, Id<T>>.(Id<T>) -> Model<T & Any>
) {
    validate({ message(it!!) }) { value ->
        value == null || _exists(function(value), value)
    }
}

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 * The given [function] is used to define where to
 * search for the document.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, Id<T>>.existsAt(
    message: String,
    function: suspend SchemaScope<D, O, Id<T>>.(Id<T>) -> String
) {
    existsAt({ message }, function)
}

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 * The given [function] is used to define where to
 * search for the document.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, Id<T>>.existsAt(
    message: SchemaScope<D, O, Id<T>>.(Id<T>) -> String = {
        "Validation failed for path $path: Document not found with id $it"
    },
    function: suspend SchemaScope<D, O, Id<T>>.(Id<T>) -> String
) {
    validate({ message(it!!) }) { value ->
        value == null || _exists(function(value).let {
            model.mangaka.database.database
                .getCollection(it)
                .coroutine
        }, value)
    }
}

// internal

internal suspend fun <T> _exists(model: Model<T & Any>, id: Id<T>): Boolean {
    return model.exists(eq("_id", id.normal))
}

internal suspend fun <T> _exists(collection: CoroutineCollection<*>, id: Id<T>): Boolean {
    return collection.countDocuments(eq("_id", id.normal)) > 0
}
