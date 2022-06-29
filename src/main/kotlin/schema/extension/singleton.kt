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

import org.cufy.mangaka._id
import org.cufy.mangaka.bson
import org.cufy.mangaka.bson.`$ne`
import org.cufy.mangaka.bson.by
import org.cufy.mangaka.bson.document
import org.cufy.mangaka.exists
import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.SchemaScope

/**
 * Insures this path is unique.
 *
 * @param error the error message factory.
 * @param block the validation block.
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.singleton(
    error: suspend SchemaScope<O, T>.(T) -> String = {
        "Duplicate value '$it'"
    },
    block: suspend SchemaScope<O, T>.(T) -> Boolean = { true }
) {
    validate(error) {
        val filter = document(
            "_id" by document(
                `$ne` by document?._id?.bson
            ),
            pathname by it
        )

        !block(it) || !model.exists(filter)
    }
}
