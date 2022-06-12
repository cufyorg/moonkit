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
 * Intercept the functions of this schema to
 * inherit the given [schema].
 *
 * ### Constructor Interception
 * The constructor of the given [schema] will be
 * invoked first and if returned null, the
 * original constructor will be invoked.
 *
 * ### Formatter Interception
 * The formatter of the given [schema] will be
 * invoked first and if returned null, the
 * original formatter will be invoked.
 *
 * ### Validator Interception
 * The validator of the given [schema] will be
 * invoked first and then the original validator.
 *
 * @param schema the schema to inherit.
 * @since 1.0.0
 */
infix fun <D, O, T> Schema<D, O, T>.inherits(
    schema: Schema<D, O, T>
) = apply {
    onConstruct { bson, constructor ->
        schema.constructor.invoke(this, bson) ?: constructor(bson)
    }
    onFormat { value, formatter ->
        schema.formatter.invoke(this, value) ?: formatter(value)
    }
    onValidate { value, validator ->
        schema.validator.invoke(this, value) + validator(value)
    }
}

/**
 * Intercept the functions of this schema to
 * implement the given [schema].
 *
 * ### Constructor Interception
 * The constructor of the given [schema] will be
 * invoked if the original constructor returned
 * null.
 *
 * ### Formatter Interception
 * The formatter of the given [schema] will be
 * invoked if the original formatter returned
 * null.
 *
 * ### Validator Interception
 * The validator of the given [schema] will be
 * invoked after the original validator.
 *
 * @param schema the schema to implement.
 * @since 1.0.0
 */
infix fun <D, O, T> Schema<D, O, T>.implements(
    schema: Schema<D, O, T>
) = apply {
    onConstruct { bson, constructor ->
        constructor(bson) ?: schema.constructor.invoke(this, bson)
    }
    onFormat { value, formatter ->
        formatter(value) ?: schema.formatter.invoke(this, value)
    }
    onValidate { value, validator ->
        validator(value) + schema.validator.invoke(this, value)
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
