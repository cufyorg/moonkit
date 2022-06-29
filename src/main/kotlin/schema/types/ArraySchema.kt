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
package org.cufy.mangaka.schema.types

import org.bson.BsonArray
import org.bson.BsonValue
import org.cufy.mangaka.schema.Schema
import org.cufy.mangaka.schema.SchemaScope

/**
 * A schema wrapper for arrays.
 *
 * @author LSafer
 * @since 1.0.0
 */
class ArraySchema<T>(
    /**
     * The wrapped schema.
     *
     * @since 1.0.0
     */
    val schema: Schema<T>
) : Schema<MutableList<T>> {
    override suspend fun serialize(
        scope: SchemaScope<*, MutableList<T>>,
        value: MutableList<T>
    ): BsonValue {
        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        val bson = BsonArray()
        value.forEach { itemValue ->
            bson += schema.serialize(scope, itemValue)
        }
        return bson
    }

    override suspend fun deserialize(
        scope: SchemaScope<*, MutableList<T>>,
        bson: BsonValue
    ): MutableList<T> {
        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        val value = mutableListOf<T>()
        if (bson is BsonArray) {
            bson.forEach { itemBson ->
                value += schema.deserialize(scope, itemBson)
            }
        }
        return value
    }

    override suspend fun validate(
        scope: SchemaScope<*, MutableList<T>>,
        value: MutableList<T>
    ): List<Throwable> {
        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        return value.flatMap { itemValue ->
            schema.validate(scope, itemValue)
        }
    }
}
