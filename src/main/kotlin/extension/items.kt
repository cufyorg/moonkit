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

import org.bson.BsonArray
import org.cufy.mangaka.*

/**
 * Define an items' schema for a mutable list to
 * this schema.
 *
 * @since 1.0.0
 */
fun <D, O, M : Any> Schema<D, O, MutableList<M>>.items(
    builder: Schema<D, MutableList<M>, M>.() -> Unit
) {
    items(
        iterator = { self.iterator() },
        pusher = { it?.let { self.add(it) } },
        schema = Schema<D, MutableList<M>, M>().apply(builder)
    )
}

/**
 * Define an items' schema for a mutable list of
 * nullable items to this schema.
 *
 * @since 1.0.0
 */
@JvmName("nullableItems")
fun <D, O, M> Schema<D, O, MutableList<M?>>.items(
    builder: Schema<D, MutableList<M?>, M & Any>.() -> Unit
) {
    items(
        iterator = { self.iterator() },
        pusher = { self.add(it) },
        schema = Schema<D, MutableList<M?>, M & Any>().apply(builder)
    )
}

/**
 * Define an items' schema to this schema.
 *
 * TODO: documentation for items modifier.
 *
 * @param iterator a function to get the items.
 * @param pusher a function to push an item.
 * @param schema the schema for the items.
 * @since 1.0.0
 */
fun <D, O, T, M> Schema<D, O, T>.items(
    iterator: suspend SchemaScope<D, T, M>.() -> Iterator<M?>,
    pusher: suspend SchemaScope<D, T, M>.(value: M?) -> Unit,
    schema: Schema<D, T, M> = Schema()
) {
    onConstruct { bson, constructor ->
        // invoke the original constructor
        val self = constructor(bson)

        // skip value constructing if parent is null
        if (self != null) {
            // construct scope
            val scope = SchemaScope<D, T, M>(
                name = name,
                path = "$path[]",
                document = document,
                model = model,
                self = self
            )

            for (bsonValue in (bson as? BsonArray) ?: emptyList()) {
                // invoke item constructor
                val value = schema.constructor(scope, bsonValue)

                // inject value to document
                pusher(scope, value)
            }
        }

        self
    }
    onFormat { self, formatter ->
        // invoke the original formatter
        val bson = formatter(self)

        // skip value formatting if parent is null
        if (self != null && bson is BsonArray) {
            // construct scope
            val scope = SchemaScope<D, T, M>(
                name = name,
                path = "$path[]",
                document = document,
                model = model,
                self = self
            )

            for (value in iterator(scope)) {
                // invoke item formatter
                val bsonValue = schema.formatter(scope, value)

                // inject bson value to bson self
                if (bsonValue != null)
                    bson.add(bsonValue)
            }
        }

        bson
    }
    onValidate { self, validator ->
        // invoke the original validator
        val errors = validator(self).toMutableList()

        // skip value errors if parent is null
        if (self != null) {
            // construct scope
            val scope = SchemaScope<D, T, M>(
                name = name,
                path = "$path[]",
                document = document,
                model = model,
                self = self
            )

            for (value in iterator(scope)) {
                val valueErrors = schema.validator(scope, value)

                errors += valueErrors
            }
        }

        errors
    }
}
