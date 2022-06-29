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
import org.cufy.mangaka.schema.SchemaScope

/**
 * Indicate a validation error when the value is
 * not null and the given [block] returns false
 * while validating.
 *
 * @param error the error message factory.
 * @param block the validation block.
 * @return 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.insure(
    error: suspend SchemaScope<O, T>.(T) -> String = {
        "Invalid value for field '$name'"
    },
    block: suspend SchemaScope<O, T>.(T & Any) -> Boolean
) {
    validate(error) { it == null || block(it) }
}
