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
package org.cufy.mangaka.schema

import org.bson.BsonValue

/**
 * A schema defines how to serialize values
 * into bson and vice-versa.
 *
 * @author LSafer
 * @since 1.0.0
 */
interface Schema<T> {
    /**
     * Serialize the given [value] to bson.
     *
     * If returned null, this means serialization
     * of the value should be ignored.
     *
     * @param scope the execution environment.
     * @param value the value.
     * @since 1.0.0
     */
    suspend fun serialize(
        scope: SchemaScope<*, T>,
        value: T
    ): BsonValue

    /**
     * Deserialize the given [bson] into [T].
     *
     * When passing null, this means the serialized
     * bson is either missing.
     *
     * @param scope the execution environment.
     * @param bson the source bson.
     * @since 1.0.0
     */
    suspend fun deserialize(
        scope: SchemaScope<*, T>,
        bson: BsonValue
    ): T

    /**
     * Validate the given [value].
     *
     * @param scope the execution environment.
     * @param value the value.
     * @since 1.0.0
     */
    suspend fun validate(
        scope: SchemaScope<*, T>,
        value: T
    ): List<Throwable>
}
