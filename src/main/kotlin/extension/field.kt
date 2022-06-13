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

import org.bson.BsonDocument
import org.cufy.mangaka.*
import kotlin.reflect.KMutableProperty1

/**
 * Define a property schema to this schema
 * for the given [property].
 *
 * @since 1.0.0
 */
fun <D, O, T, M : Any> Schema<D, O, T>.field(
    property: KMutableProperty1<in T, M>,
    builder: Schema<D, T, M>.() -> Unit = {}
) {
    field(
        name = property.name,
        getter = { property.get(self) },
        setter = { it?.let { property.set(self, it) } },
        schema = Schema<D, T, M>().apply(builder)
    )
}

/**
 * Define a property schema to this schema
 * for the given nullable [property].
 *
 * @since 1.0.0
 */
@JvmName("nullableField")
fun <D, O, T, M> Schema<D, O, T>.field(
    property: KMutableProperty1<in T, M?>,
    builder: Schema<D, T, M & Any>.() -> Unit = {}
) {
    field(
        name = property.name,
        getter = { property.get(self) },
        setter = { property.set(self, it) },
        schema = Schema<D, T, M & Any>().apply(builder),
    )
}

/**
 * Define a field schema to this schema
 * with the given [name].
 *
 * TODO: documentation for field modifier.
 *
 * @param name the name of the field.
 * @param getter a getter to get the field value.
 * @param setter a setter to set the field value.
 * @param schema the schema for the field.
 * @since 1.0.0
 */
fun <D, O, T, M> Schema<D, O, T>.field(
    name: String,
    getter: suspend SchemaScope<D, T, M>.() -> M?,
    setter: suspend SchemaScope<D, T, M>.(value: M?) -> Unit,
    schema: Schema<D, T, M> = Schema()
) {
    /*
        - Construct parent

        - If parent constructed to null, do nothing.

        - If input bson is null, invoke constructor
           with null.
        - If input bson is non-document, invoke
           constructor with null.
        - If input bson didn't contain 'name',
           invoke constructor with null.

        - Invoke setter.
     */
    onConstruct { bson, fallback ->
        // invoke the original constructor
        val self = fallback(bson)

        // skip value constructing if parent is null
        if (self != null) {
            // construct scope
            val scope = SchemaScope<D, T, M>(
                name = name,
                path = "$path.$name",
                document = document,
                model = model,
                self = self
            )

            // invoke property constructor
            val bsonValue = (bson as? BsonDocument)?.get(name)
            val value = with(scope) { schema.constructor(bsonValue) }

            // inject value to document
            setter(scope, value)
        }

        self
    }
    /*
        - Format parent

        - If input value is null, do nothing.
        - If parent formatted to null, do nothing.
        - If parent formatted to non-document, do nothing.

        - Otherwise, Invoke getter then formatter.

        - If the formatter returned null, do nothing.

        - Otherwise, assign results to output bson.
     */
    onFormat { self, fallback ->
        // invoke the original formatter
        val bson = fallback(self)

        // skip value formatting if parent is null
        if (self != null && bson is BsonDocument) {
            // construct scope
            val scope = SchemaScope<D, T, M>(
                name = name,
                path = "$path.$name",
                document = document,
                model = model,
                self = self
            )

            // invoke property formatter
            val value = getter(scope)
            val bsonValue = with(scope) { schema.formatter(value) }

            // inject bson value to bson self
            if (bsonValue != null)
                bson[name] = bsonValue
        }

        bson
    }
    /*
        - Validate parent

        - If input value is null, do nothing.

        - Invoke getter then validator.
        - Added value errors to output list.
     */
    onValidate { self, fallback ->
        // invoke the original validator
        val errors = fallback(self).toMutableList()

        // skip value errors if parent is null
        if (self != null) {
            // construct scope
            val scope = SchemaScope<D, T, M>(
                name = name,
                path = "$path.$name",
                document = document,
                model = model,
                self = self
            )

            // invoke property validator
            val value = getter(scope)
            val valueErrors = with(scope) { schema.validator(value) }

            errors += valueErrors
        }

        errors
    }
}
