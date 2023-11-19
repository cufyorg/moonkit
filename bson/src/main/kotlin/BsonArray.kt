/*
 *	Copyright 2022-2023 cufy.org
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
 * A type-safe representation of the BSON array type.
 *
 * This class is meant to be immutable.
 * If a way to mutate an instance of this class is
 * found. The behaviour of the instance is undefined
 * and that way will not be guaranteed to even work
 * in different versions.
 *
 * @see org.bson.BsonArray
 * @since 2.0.0
 */
class BsonArray @PublishedApi internal constructor(
    private val content: BsonArrayLike,
) : BsonElement, BsonArrayLike by content {
    companion object {
        /**
         * A global instance of [BsonArray] that has no items.
         *
         * @since 2.0.0
         */
        val Empty = BsonArray(emptyList())
    }

    override val type: BsonType get() = BsonType.Array

    override fun equals(other: Any?) =
        content == other

    override fun hashCode() =
        content.hashCode()

    override fun toString() =
        content.joinToString(",", "[", "]")
}

/* ============= ------------------ ============= */

/**
 * Construct a new empty bson array.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun BsonArray(): BsonArray {
    return BsonArray.Empty
}

/**
 * Construct a new bson array using the given
 * builder [block].
 *
 * **Warning: mutating the instance provided by the
 * given [block] after the execution of this
 * function will result to an undefined behaviour.**
 */
inline fun BsonArray(block: BsonArrayBlock): BsonArray {
    val content = mutableBsonArrayOf()
    content.apply(block)
    return BsonArray(content)
}

/**
 * Construct a new bson array with the given [elements].
 *
 * **Warning: mutating the given array [elements]
 * after the execution of this function will result
 * to an undefined behaviour.**
 */
fun BsonArray(vararg elements: BsonElement): BsonArray {
    return BsonArray(elements.toList())
}

/* ============= ------------------ ============= */

/**
 * Construct a new bson array from this list.
 */
fun Iterable<BsonElement>.toBsonArray(): BsonArray {
    return BsonArray(toList())
}

/* ============= ------------------ ============= */

/**
 * Create a new array from combining this array with the given [list].
 */
operator fun BsonArray.plus(list: BsonArrayLike): BsonArray {
    return BsonArray {
        byAll(this)
        byAll(list)
    }
}

/**
 * Create a new array from combining this array with the given [block].
 */
inline operator fun BsonArray.plus(block: BsonArrayBlock): BsonArray {
    return BsonArray {
        byAll(this)
        block()
    }
}

/* ============= ------------------ ============= */

/**
 * A global instance of [BsonArray] that has no items.
 *
 * @since 2.0.0
 */
@Deprecated(
    "Use BsonArray.Empty instead", ReplaceWith(
        "BsonArray.Empty",
        "org.cufy.bson.BsonArray"
    )
)
val EmptyBsonArray = BsonArray.Empty

/**
 * Invoke this function with the given [block] as the argument.
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Use Constructor(BsonArray { }) instead")
@JvmName("invokeWithBsonArrayBlock")
operator fun <T> ((BsonArray) -> T).invoke(block: BsonArrayBlock): T {
    return this(BsonArray(block))
}

/* ============= ------------------ ============= */
