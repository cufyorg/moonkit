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

import org.cufy.mangaka.bson.`$exists`
import org.cufy.mangaka.bson.by
import org.cufy.mangaka.bson.document
import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.ObjectSchemaBuilder

/**
 * Insures this path is unique.
 *
 * This is not a validator, instead is a
 * collection modifier.
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.unique(
    skipNull: Boolean = false
) {
    index { pathname ->
        unique(true)

        if (skipNull) {
            partialFilterExpression(document(
                pathname by document(`$exists` by true)
            ))
        }
    }
}

/**
 * Insures the paths of the given [fields] are unique.
 *
 * This is not a validator, instead is a
 * collection modifier.
 *
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.unique(
    vararg fields: String,
    skipNull: Boolean = false
) {
    unique(fields.asList(), skipNull)
}

/**
 * Insures the paths of the given [fields] are unique.
 *
 * This is not a validator, instead is a
 * collection modifier.
 *
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.unique(
    fields: List<String>,
    skipNull: Boolean = false
) {
    index(fields) { pathnames ->
        unique(true)

        if (skipNull) {
            partialFilterExpression(document(
                pathnames.map { it by document(`$exists` by true) }
            ))
        }
    }
}
