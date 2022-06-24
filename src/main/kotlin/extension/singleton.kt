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
import com.mongodb.client.model.Filters.nor
import org.cufy.mangaka.Model
import org.cufy.mangaka.Schema
import org.cufy.mangaka.SchemaScope
import org.cufy.mangaka._id

/**
 * Insures this path is unique.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.singleton(
    message: String,
    function: suspend SchemaScope<D, O, T>.(T?) -> Boolean = { true }
) {
    validate(message) { !function(it) || _singleton(model, path, document, it) }
}

/**
 * Insures this path is unique.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.singleton(
    message: suspend SchemaScope<D, O, T>.(T?) -> String = {
        "Validation failed for path $path: Duplicate value"
    },
    function: suspend SchemaScope<D, O, T>.(T?) -> Boolean = { true }
) {
    validate(message) {
        !function(it) || _singleton(model, path, document, it)
    }
}

// internal

suspend fun _singleton(
    model: Model<*>,
    path: String,
    document: Any?,
    value: Any?
): Boolean {
    return !model.exists(
        nor(eq("_id", document?._id?.normal)),
        eq(path.split('.', limit = 2)[1], value)
    )
}
