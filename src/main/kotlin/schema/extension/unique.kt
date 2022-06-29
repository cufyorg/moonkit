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

import org.cufy.mangaka.bson.by
import org.cufy.mangaka.bson.document
import org.cufy.mangaka.ensureIndex
import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.onSerialize

/**
 * Insures this path is unique.
 *
 * This is not a validator, instead is a
 * collection modifier.
 *
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.unique() {
    onSerialize { parent, document, instance, value ->
        this.model.ensureIndex(document(
            pathname by 1
        )) { unique(true) }

        parent(this, document, instance, value)
    }
}
