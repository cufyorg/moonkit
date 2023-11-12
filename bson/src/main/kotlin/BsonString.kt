/*
 *	Copyright 2022-2023 cufy.org and meemer.com
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
package org.cufy.bson

/* ============= ------------------ ============= */

/**
 * A representation of the BSON String type.
 *
 * @see org.bson.BsonString
 * @since 2.0.0
 */
data class BsonString(val value: String) : BsonElement {
    override val type: BsonType get() = BsonType.String

    override fun equals(other: Any?) =
        other is BsonString && other.value == value

    override fun hashCode() =
        value.hashCode()

    override fun toString() =
        """"$value""""
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonString] with the value of this.
 */
inline val String.bson: BsonString get() = BsonString(this)

/**
 * Return a [BsonString] with the value of this or [BsonNull] if this is `null`.
 */
val String?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonString] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val String.b: BsonString get() = bson

/* ============= ------------------ ============= */
