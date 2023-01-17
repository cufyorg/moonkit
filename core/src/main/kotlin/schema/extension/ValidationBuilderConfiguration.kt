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
package org.cufy.monkt.schema.extension

import org.cufy.bson.Id
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

/* ============= ---- Protocol ---- ============= */

/**
 * A builder style validation option configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ValidationBuilderConfiguration : ValidationConfiguration() {
    /**
     * The error's message.
     *
     * Set to `null` to use the default message.
     */
    var message: String? = null

    /**
     * The error's label.
     *
     * Set to `null` to use the default label.
     */
    var label: String? = null
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * The error's message.
 *
 * Set to `null` to use the default message.
 */
@OptIn(AdvancedMonktApi::class)
var <T : Any, M> OptionScope<T, M, ValidationBuilderConfiguration>.message: String?
    get() = configuration.message
    set(value) {
        configuration.message = value
    }

/**
 * The error's label.
 *
 * Set to `null` to use the default label.
 */
@OptIn(AdvancedMonktApi::class)
var <T : Any, M> OptionScope<T, M, ValidationBuilderConfiguration>.label: String?
    get() = configuration.label
    set(value) {
        configuration.label = value
    }

// builder

/**
 * Indicate a validation error when the given
 * [block] returns false while validating.
 *
 * @param block the validation block.
 * @return 2.0.0
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.validate(
    block: ReturnOptionBlock<T, M, ValidationBuilderConfiguration, Boolean>
) {
    val cause = ValidationException()
    val configuration = ValidationBuilderConfiguration()
    option(configuration) {
        val isValid = block(it)

        if (!isValid) {
            val label = label ?: "Validation failed for path ${model.name}.$pathname"
            val message = message ?: "validation condition was not met"

            val error = ValidationException(label, message, cause)

            @Suppress("UNCHECKED_CAST")
            this as OptionScope<T, M, ValidationConfiguration>

            error(error)
        }
    }
}

/**
 * Indicate a validation error when the value is
 * not null and the given [block] returns false
 * while validating.
 *
 * @param block the validation block.
 * @return 2.0.0
 */
fun <T : Any, M> FieldDefinitionBuilder<T, M>.insure(
    block: ReturnOptionBlock<T, M & Any, ValidationBuilderConfiguration, Boolean>
) {
    validate { value ->
        value ?: return@validate true

        @Suppress("UNCHECKED_CAST")
        this as OptionScope<T, M & Any, ValidationBuilderConfiguration>

        block(value)
    }
}

/**
 * Add a validator that insures the value is not null.
 *
 * The given [block] is used to determine when the
 * value is required.
 *
 * @param block the validation block.
 * @since 2.0.0
 */
fun <T : Any, M> FieldDefinitionBuilder<T, M>.required(
    block: ReturnOptionBlock<T, Nothing?, ValidationBuilderConfiguration, Boolean> = { true }
) {
    validate { value ->
        if (value != null)
            return@validate true

        @Suppress("UNCHECKED_CAST")
        this as OptionScope<T, Nothing?, ValidationBuilderConfiguration>

        val isRequired = block(null)

        !isRequired
    }
}

/**
 * Add a validator that insures no other document
 * has the same value as the value of this field.
 */
@OptIn(AdvancedMonktApi::class)
@Deprecated("Use `unique` instead. This validator is unnecessary")
fun <T : Any, M> FieldDefinitionBuilder<T, M>.singleton(
    block: ReturnOptionBlock<T, M, ValidationBuilderConfiguration, Boolean> = { true }
) {
    validate {
        val shouldSingleton = block(it)

        if (!shouldSingleton)
            return@validate true

        val root = root
        val pathname = pathname
        val schema = schema
        val model = model

        val id = Document.getId(root)

        val duplicateCount by count(model) {
            "_id" by { `$ne` by id }
            "$pathname" by schema.encode(value)
        }

        then { duplicateCount <= 0 }
    }
}

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 *
 * The given [block] is used to get the model to
 * check at.
 *
 * @param block the model getter.
 * @since 2.0.0
 */
fun <T : Any, M : Id<*>?> FieldDefinitionBuilder<T, M>.exists(
    block: ReturnOptionBlock<T, M & Any, ValidationBuilderConfiguration, Model<*>>
) {
    insure {
        val model = block(it)

        val count by count(model) { "_id" by it }

        then { count > 0 }
    }
}

/**
 * Add a validator that insures the id is pointing
 * to a valid document.
 *
 * The given [block] is used to get the name of
 * the collection to check at.
 *
 * @param block the collection name getter.
 * @since 2.0.0
 */
@Deprecated("Use `exists` instead. This validator is unnecessary")
fun <T : Any, M : Id<*>?> FieldDefinitionBuilder<T, M>.existsAt(
    block: ReturnOptionBlock<T, M & Any, ValidationBuilderConfiguration, String>
) {
    insure {
        val collectionName = block(it)

        val monkt = model.monkt()

        val collection = monkt.database().getCollection(collectionName)

        // TODO use signals (or remove the whole thing)
        val count = collection.countDocumentsSuspend({ "_id" by it })

        then { count > 0 }
    }
}
