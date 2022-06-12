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

import org.cufy.mangaka.Schema
import org.cufy.mangaka.SchemaScope
import org.cufy.mangaka.internal.MetaData

/**
 * Treat the value as immutable if the given
 * [function] returned true.
 *
 * This is done by ignoring formatting the value
 * when the document is not new and the given
 * [function] returned true.
 *
 * @param function a function to be invoked when
 *                 formatting a value on a not-new
 *                 document to determine if the
 *                 value should be ignored or not.
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.immutable(
    function: SchemaScope<D, O, T>.(T?) -> Boolean = {
        true
    }
) {
    ignore { !_isNew(document) && function(it) }
}

// internal

internal fun _isNew(document: Any?): Boolean {
    if (document == null)
        return true

    val metadata = MetaData.get(document)
        ?: return false

    return metadata.isNew
}
