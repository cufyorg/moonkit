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
import org.cufy.mangaka.schema.onSerialize

/**
 * Ignore formatting the value if the given
 * [block] returned true.
 *
 * @param block a function to be invoked when
 *                 formatting to determine if the
 *                 value should be ignored or not.
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.ignore(
    block: suspend SchemaScope<O, T>.(T?) -> Boolean = { true }
) {
    onSerialize { parent, document, instance, value ->
        if (!block(this, value))
            parent(this, document, instance, value)
    }
}
