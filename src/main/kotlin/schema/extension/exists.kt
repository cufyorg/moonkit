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

import org.cufy.mangaka.Id
import org.cufy.mangaka.Model
import org.cufy.mangaka.bson
import org.cufy.mangaka.bson.by
import org.cufy.mangaka.bson.document
import org.cufy.mangaka.exists
import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.SchemaScope

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 * The given [block] is used to define where to
 * search for the document.
 *
 * @param error the error message factory.
 * @param block the validation block.
 * @since 1.0.0
 */
fun <O : Any, T : Id<*>?> FieldDefinitionBuilder<O, T>.exists(
    error: suspend SchemaScope<O, T>.(T) -> String = {
        "Document not found with id '${it?.value}'"
    },
    block: suspend SchemaScope<O, T>.(T) -> Model<*>
) {
    insure(error) {
        val filter = document("_id" by it.bson)
        val model = block(it)

        model.exists(filter)
    }
}

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 * The given [block] is used to define where to
 * search for the document.
 *
 * @param error the error message factory.
 * @param block the validation block.
 * @since 1.0.0
 */
fun <O : Any, T : Id<*>?> FieldDefinitionBuilder<O, T>.existsAt(
    error: suspend SchemaScope<O, T>.(T) -> String = {
        "Document not found with id '${it?.value}'"
    },
    block: suspend SchemaScope<O, T>.(T) -> String
) {
    insure(error) {
        val filter = document("_id" by it.bson)
        val collectionName = block(it)
        val collection = model.mangaka.collection(collectionName)

        collection.countDocuments(filter) > 0
    }
}
