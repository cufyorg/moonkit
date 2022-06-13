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
import org.cufy.mangaka.onConstruct
import org.cufy.mangaka.onFormat
import org.cufy.mangaka.onValidate

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
    schema: Schema<D, O, T>
) = apply {
    onConstruct { bson, constructor ->
        schema.constructor(bson, constructor)
    }
    onFormat { value, formatter ->
        schema.formatter(value, formatter)
    }
    onValidate { value, validator ->
        schema.validator(value, validator)
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
    schema: Schema<D, O, T>
) = apply {
    this.constructor = schema.constructor
    this.formatter = schema.formatter
    this.validator = schema.validator
}
