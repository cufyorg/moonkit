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
 * A type-safe container for a BSON document.
 *
 * This class is meant to be immutable.
 * If a way to mutate an instance of this class is
 * found. The behaviour of the instance is undefined
 * and that way will not be guaranteed to even work
 * in different versions.
 *
 * @see org.bson.BsonDocument
 * @since 2.0.0
 */
class BsonDocument internal constructor(
    private val content: BsonDocumentLike,
) : BsonElement, BsonDocumentLike by content {
    companion object {
        /**
         * A global instance of [BsonDocument] that has no entries.
         *
         * @since 2.0.0
         */
        val Empty = BsonDocument(emptyMap())
    }

    override val type: BsonType get() = BsonType.Document

    override fun equals(other: Any?) =
        content == other

    override fun hashCode() =
        content.hashCode()

    override fun toString() =
        content.entries.joinToString(",", "{", "}") {
            """"${it.key}":${it.value}"""
        }
}

/* ============= ------------------ ============= */

/**
 * Construct a new empty bson document.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun BsonDocument(): BsonDocument {
    return BsonDocument.Empty
}

/**
 * Construct a new bson document using the given
 * builder [block].
 *
 * **Warning: mutating the instance provided by the
 * given [block] after the execution of this
 * function will result to an undefined behaviour.**
 */
fun BsonDocument(block: BsonDocumentBlock): BsonDocument {
    val content = mutableBsonMapOf()
    content.apply(block)
    return BsonDocument(content)
}

/**
 * Construct a new bson document with the given [pairs].
 *
 * **Warning: mutating the given array [pairs]
 * after the execution of this function will result
 * to an undefined behaviour.**
 */
fun BsonDocument(vararg pairs: Pair<String, BsonElement>): BsonDocument {
    return BsonDocument(pairs.toMap())
}

/* ============= ------------------ ============= */

/**
 * Construct a new bson document from this map.
 */
fun Map<String, BsonElement>.toBsonDocument(): BsonDocument {
    return BsonDocument(toMap())
}

/* ============= ------------------ ============= */

/**
 * Create a new document from combining this document with the given [map].
 */
operator fun BsonDocument.plus(map: BsonDocumentLike): BsonDocument {
    return BsonDocument {
        byAll(this)
        byAll(map)
    }
}

/**
 * Create a new document from combining this document with the given [block].
 */
operator fun BsonDocument.plus(block: BsonDocumentBlock): BsonDocument {
    return BsonDocument {
        byAll(this)
        block()
    }
}

/* ============= ------------------ ============= */

/**
 * A global instance of [BsonDocument] that has no entries.
 *
 * @since 2.0.0
 */
@Deprecated(
    "Use BsonDocument.Empty instead", ReplaceWith(
        "BsonDocument.Empty",
        "org.cufy.bson.BsonDocument"
    )
)
val EmptyBsonDocument = BsonDocument.Empty

/**
 * Invoke this function with the given [block] as the argument.
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Use Constructor(BsonDocument { }) instead")
@JvmName("invokeWithBsonDocumentBlock")
operator fun <T> ((BsonDocument) -> T).invoke(block: BsonDocumentBlock): T {
    return this(BsonDocument(block))
}

/* ============= ------------------ ============= */
