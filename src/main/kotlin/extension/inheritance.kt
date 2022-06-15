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

import org.cufy.mangaka.*

/**
 * Apply the functions of the given [schema] to
 * intercept the functions of this schema.
 *
 * TODO documentation for the implements extension
 *
 * @param schema the schema to implement.
 * @since 1.0.0
 */
infix fun <D, O, T> Schema<D, O, T>.implements(
    schema: Schema<in D, in O, T>
) = apply {
    onConstruct { bson, fallback ->
        schema.constructor.safeCast()(this, bson, fallback)
    }
    onFormat { value, fallback ->
        schema.formatter.safeCast()(this, value, fallback)
    }
    onValidate { value, fallback ->
        schema.validator.safeCast()(this, value, fallback)
    }
}

/**
 * Replace the functions of this schema to the
 * functions of the given [schema].
 *
 * @param schema the schema to inherit.
 * @since 1.0.0
 */
infix fun <D, O, T> Schema<D, O, T>.extends(
    schema: Schema<in D, in O, T>
) = apply {
    this.constructor = schema.constructor.safeCast()
    this.formatter = schema.formatter.safeCast()
    this.validator = schema.validator.safeCast()
}
