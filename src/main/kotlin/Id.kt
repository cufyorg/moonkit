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
package org.cufy.mangaka

import kotlinx.serialization.Serializable
import org.bson.BsonObjectId
import org.bson.BsonString
import org.bson.types.ObjectId
import org.cufy.mangaka.internal.nonNormalizableIdError

/**
 * An id wrapper.
 *
 * TODO documentation for class Id
 *
 * @author LSafer
 * @since 1.0.0
 */
@JvmInline
@Serializable
value class Id<T>(
    /**
     * The string representation of the id.
     *
     * @since 1.0.0
     */
    val value: String
) : CharSequence by value {
    /**
     * Construct a new id.
     *
     * @since 1.0.0
     */
    constructor() : this(ObjectId())

    /**
     * Construct a new id from the given [value].
     *
     * @since 1.0.0
     */
    constructor(value: ObjectId) : this(value.toHexString())

    /**
     * Return the best fitting native wrapper for
     * this id.
     * An [ObjectId] if it is a valid object id
     * and a [String] if it is not.
     *
     * @since 1.0.0
     */
    val normal: Any
        get() = when {
            ObjectId.isValid(value) -> ObjectId(value)
            else -> value
        }

    companion object {
        /**
         * Construct a new id from normalizing the
         * given [value].
         *
         * @param value the value to normalize.
         * @since 1.0.0
         */
        fun <T> normalize(value: Any): Id<T> {
            @Suppress("UNCHECKED_CAST")
            return when (value) {
                is Id<*> -> value as Id<T>
                is ObjectId -> Id(value)
                is BsonObjectId -> Id(value.value)
                is BsonString -> Id(value.value)
                else -> nonNormalizableIdError(value)
            }
        }
    }
}
