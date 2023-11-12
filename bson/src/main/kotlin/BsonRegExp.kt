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

import org.intellij.lang.annotations.Language

/* ============= ------------------ ============= */

/**
 * A holder class for a BSON regular expression,
 * so that we can delay compiling into a Pattern
 * until necessary.
 *
 * @see org.bson.BsonRegularExpression
 * @since 2.0.0
 */
data class BsonRegExp(@Language("RegExp") val pattern: String, val options: Set<Char> = emptySet()) : BsonElement {
    override val type: BsonType get() = BsonType.RegExp

    override fun equals(other: Any?) =
        other is BsonRegExp && other.pattern == pattern && other.options == options

    override fun hashCode() =
        31 * pattern.hashCode() + options.hashCode()

    override fun toString() =
        "/$pattern/${options.joinToString("")}"
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonRegExp] with the given [pattern] and [options].
 *
 * @since 2.0.0
 */
fun BsonRegExp(@Language("RegExp") pattern: String, options: String): BsonRegExp {
    return BsonRegExp(pattern, options.toSortedSet())
}

/* ============= ------------------ ============= */
