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
import org.cufy.mangaka.schema.ObjectSchema
import org.cufy.mangaka.schema.ObjectSchemaBuilder
import org.cufy.mangaka.schema.SchemaScope

/**
 * Insures this path is unique.
 *
 * @param error the error message factory.
 * @param block a function that returns `true` to
 *              activate this validator.
 * @since 1.0.0
 */
fun <O : Any, T> FieldDefinitionBuilder<O, T>.singleton(
    error: suspend SchemaScope<O, T>.(T) -> String = {
        "Duplicate value '$it'"
    },
    block: suspend SchemaScope<O, T>.(T) -> Boolean = { true }
) {
    validate(error) { value ->
        if (!block(value))
            return@validate true

        val filter = document()

        filter["_id"] = document(
            `$ne` by document?._id?.bson
        )

        filter[pathname] =
            this.schema.serialize(this, value)

        !model.exists(filter)
    }
}

/**
 * Insures the object fields are unique.
 *
 * @param error the error message factory.
 * @param block a function that returns the names
 *              of the fields to be unique.
 * @since 1.1.0
 */
fun <T : Any> ObjectSchemaBuilder<T>.singleton(
    error: suspend SchemaScope<*, T>.(T) -> String = {
        "Duplicate object values"
    },
    block: suspend SchemaScope<*, T>.(T) -> List<String>?
) {
    validate(error) { instance ->
        val fieldNames = block(instance)
            ?: return@validate true

        val filter = document()

        filter["_id"] = document(
            `$ne` by document?._id?.bson
        )

        for (fieldName in fieldNames) {
            val field = (this.schema as ObjectSchema<T>).fields.first { it.name == fieldName }
            val scope = SchemaScope<T, Any?>(this) {
                self = instance
                name = field.name
                schema = field.schema
            }

            val value = field.get(scope, instance)

            filter[scope.pathname] =
                scope.schema.serialize(scope, value)
        }

        !this.model.exists(filter)
    }
}
