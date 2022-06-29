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

import org.bson.BsonNull
import org.bson.BsonUndefined
import org.bson.BsonValue
import org.cufy.mangaka.schema.Schema
import org.cufy.mangaka.schema.SchemaScope

/**
 * A schema wrapper for nullable values.
 *
 * @author LSafer
 * @since 1.0.0
 */
class NullableSchema<T>(
    /**
     * The wrapped schema.
     *
     * @since 1.0.0
     */
    val schema: Schema<T>
) : Schema<T?> {
    override suspend fun serialize(
        scope: SchemaScope<*, T?>,
        value: T?
    ): BsonValue {
        if (value == null)
            return BsonNull.VALUE

        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        return schema.serialize(scope, value)
    }

    override suspend fun deserialize(
        scope: SchemaScope<*, T?>,
        bson: BsonValue
    ): T? {
        if (bson is BsonNull || bson is BsonUndefined)
            return null

        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        return schema.deserialize(scope, bson)
    }

    override suspend fun validate(
        scope: SchemaScope<*, T?>,
        value: T?
    ): List<Throwable> {
        if (value == null)
            return emptyList()

        @Suppress("UNCHECKED_CAST")
        scope as SchemaScope<*, T>

        return schema.validate(scope, value)
    }
}
