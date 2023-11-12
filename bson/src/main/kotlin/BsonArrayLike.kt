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

import kotlinx.datetime.Instant
import java.math.BigDecimal
import java.util.*

/* ============= ------------------ ============= */

/**
 * A list containing only items of type [BsonElement].
 *
 * @since 2.0.0
 */
typealias BsonArrayLike = List<BsonElement>

/**
 * A mutable list containing only items of type [BsonElement].
 *
 * TODO: change to MutableList<BsonDocument> when context receivers are stable.
 *
 * @since 2.0.0
 */
typealias MutableBsonArrayLike = IMutableBsonArrayLike

/**
 * A block of code building a bson array.
 *
 * @since 2.0.0
 */
typealias BsonArrayBlock = MutableBsonArrayLike.() -> Unit

@Deprecated("Replace with BsonArrayLike", ReplaceWith("BsonArrayLike"))
typealias BsonList = BsonArrayLike

@Deprecated("Replace with MutableBsonArrayLike", ReplaceWith("MutableBsonArrayLike"))
typealias MutableBsonList = MutableBsonArrayLike

/* ============= ------------------ ============= */

/**
 * Construct a new mutable bson list from this list.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see toMutableList
 * @since 2.0.0
 */
fun Iterable<BsonElement>.toMutableBsonArray(): MutableBsonArrayLike {
    return toMutableList().asMutableBsonArray()
}

/**
 * Obtain a mutable bson list backed by this list.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @since 2.0.0
 */
fun MutableList<BsonElement>.asMutableBsonArray(): MutableBsonArrayLike {
    val content = this
    return object : MutableBsonArrayLike, MutableList<BsonElement> by content {
        override fun equals(other: Any?) =
            content == other

        override fun hashCode() =
            content.hashCode()

        override fun toString() =
            content.joinToString(",", "[", "]")
    }
}

/**
 * Construct a new mutable bson list from this list.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see toMutableList
 * @since 2.0.0
 */
@Deprecated("Use toMutableBsonArray() instead", ReplaceWith("toMutableBsonArray()"))
fun Iterable<BsonElement>.toMutableBsonList() =
    toMutableBsonArray()

/**
 * Obtain a mutable bson list backed by this list.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @since 2.0.0
 */
@Deprecated("Use asMutableBsonArray instead", ReplaceWith("asMutableBsonArray()"))
fun MutableList<BsonElement>.asMutableBsonList() =
    asMutableBsonArray()

/* ============= ------------------ ============= */

/**
 * Return an empty new [MutableBsonArrayLike].
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableListOf
 * @since 2.0.0
 */
fun mutableBsonArrayOf(): MutableBsonArrayLike {
    return mutableListOf<BsonElement>()
        .asMutableBsonArray()
}

/**
 * Returns a new [MutableBsonArrayLike] with the given elements.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableListOf
 * @since 2.0.0
 */
fun mutableBsonArrayOf(vararg elements: BsonElement): MutableBsonArrayLike {
    return mutableListOf(*elements)
        .asMutableBsonArray()
}

/**
 * Return an empty new [MutableBsonArrayLike].
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableListOf
 * @since 2.0.0
 */
@Deprecated("Replace with mutableBsonArrayOf", ReplaceWith("mutableBsonArrayOf()"))
fun mutableBsonListOf() =
    mutableBsonArrayOf()

/**
 * Returns a new [MutableBsonArrayLike] with the given elements.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableListOf
 * @since 2.0.0
 */
@Deprecated("Replace with mutableBsonArrayOf", ReplaceWith("mutableBsonArrayOf(*elements)"))
fun mutableBsonListOf(vararg elements: BsonElement) =
    mutableBsonArrayOf(*elements)

/* ============= ------------------ ============= */

/**
 * A mutable list containing only items of type [BsonElement].
 *
 * This interface will be removed and its functions will be
 * extensions of [MutableBsonArrayLike] once kotlin context
 * receivers is stable.
 *
 * @since 2.0.0
 */
interface IMutableBsonArrayLike : BsonArrayLike, MutableList<BsonElement> {
    /* ============= ------------------ ============= */

    /**
     * Static (pure) utility function to prettify
     * creating arrays within the dsl.
     *
     * Usage:
     * ```
     * BsonArray {
     *     by(array {
     *         by(100L)
     *         by("item")
     *         /* ... */
     *     })
     * }
     * ```
     *
     * @return an array built with the given [block].
     * @since 2.0.0
     */
    @BsonConstructorMarker
    fun array(block: BsonArrayBlock) = BsonArray(block)

    /**
     * Static (pure) utility function to prettify
     * creating arrays within the dsl.
     *
     * Usage:
     * ```
     * BsonArray {
     *     by(array(
     *         100L.bson,
     *         "item".bson,
     *         /* ... */
     *     ))
     * }
     * ```
     *
     * @return an array with the given [elements].
     * @since 2.0.0
     */
    @BsonConstructorMarker
    fun array(vararg elements: BsonElement) = BsonArray(*elements)

    /* ============= ------------------ ============= */

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonElement?) {
        value ?: return run { this += null.bson }
        this += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonDocument?) {
        value ?: return run { this += null.bson }
        this += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonArray?) {
        value ?: return run { this += null.bson }
        this.add(value)
    }

    /* ============= ------------------ ============= */

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonConstructorMarker
    fun by(value: Map<String, BsonElement>?) {
        value ?: return run { this += null.bson }
        this += value.toBsonDocument()
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonConstructorMarker
    fun by(value: List<BsonElement>?) {
        value ?: return run { this += null.bson }
        this.add(value.toBsonArray())
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonConstructorMarker
    fun by(value: String?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonConstructorMarker
    fun by(value: ObjectId?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.bson].
     */
    @BsonConstructorMarker
    fun by(value: AnyId?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.bson].
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("byIdList")
    @BsonConstructorMarker
    fun by(value: List<AnyId>?) {
        value ?: return run { this += null.bson }
        this.add(value.map { it.bson }.toBsonArray())
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    fun by(value: Decimal128?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    fun by(value: BigDecimal?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    fun by(value: Date?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    fun by(value: Instant?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /* ============= ------------------ ============= */

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonConstructorMarker
    fun by(value: Boolean?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonConstructorMarker
    fun by(value: Int?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonConstructorMarker
    fun by(value: Long?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonConstructorMarker
    fun by(value: Double?) {
        value ?: return run { this += null.bson }
        this += value.bson
    }

    /* ============= ------------------ ============= */

    /**
     * Add the value from invoking the given [block].
     */
    @BsonConstructorMarker
    fun by(block: BsonDocumentBlock) {
        this += BsonDocument(block)
    }

    /* ============= ------------------ ============= */

    /**
     * Put all the items in the given [list].
     */
    @BsonConstructorMarker
    fun byAll(list: List<BsonElement>) {
        this += list
    }

    /* ============= ------------------ ============= */
}

/* ============= ------------------ ============= */
