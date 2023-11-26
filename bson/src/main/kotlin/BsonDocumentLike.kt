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
import kotlin.reflect.KCallable

/* ============= ------------------ ============= */

/**
 * A map containing only items of type [BsonElement].
 *
 * @since 2.0.0
 */
typealias BsonDocumentLike = Map<String, BsonElement>

/**
 * A mutable map containing only items of type [BsonElement].
 *
 * TODO: change to MutableMap<String, BsonElement> when context receivers are stable.
 *
 * @since 2.0.0
 */
typealias MutableBsonDocumentLike = IMutableBsonDocumentLike

/**
 * A block of code building a bson document.
 *
 * @since 2.0.0
 */
typealias BsonDocumentBlock = MutableBsonDocumentLike.() -> Unit

@Deprecated("Replace with BsonDocumentLike", ReplaceWith("BsonDocumentLike"))
typealias BsonMap = BsonDocumentLike

@Deprecated("Replace with MutableBsonDocumentLike", ReplaceWith("MutableBsonDocumentLike"))
typealias MutableBsonMap = MutableBsonDocumentLike

/* ============= ------------------ ============= */

/**
 * Construct a new mutable bson map from this map.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see toMutableMap
 * @since 2.0.0
 */
fun Map<String, BsonElement>.toMutableBsonDocument(): MutableBsonDocumentLike {
    return toMutableMap().asMutableBsonDocument()
}

/**
 * Obtain a mutable bson map backed by this map.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @since 2.0.0
 */
fun MutableMap<String, BsonElement>.asMutableBsonDocument(): MutableBsonDocumentLike {
    val content = this
    return object : MutableBsonDocumentLike, MutableMap<String, BsonElement> by content {
        override fun equals(other: Any?) =
            content == other

        override fun hashCode() =
            content.hashCode()

        override fun toString() =
            content.entries.joinToString(",", "{", "}") {
                """"${it.key}":${it.value}"""
            }
    }
}

/**
 * Construct a new mutable bson map from this map.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see toMutableMap
 * @since 2.0.0
 */
@Deprecated("Use toMutableBsonDocument instead", ReplaceWith("toMutableBsonDocument()"))
fun Map<String, BsonElement>.toMutableBsonMap() =
    toMutableBsonDocument()

/**
 * Obtain a mutable bson map backed by this map.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @since 2.0.0
 */
@Deprecated("Use asMutableBsonDocument instead", ReplaceWith("asMutableBsonDocument()"))
fun MutableMap<String, BsonElement>.asMutableBsonMap() =
    asMutableBsonDocument()

/* ============= ------------------ ============= */

/**
 * Return an empty new [MutableBsonDocumentLike].
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableMapOf
 * @since 2.0.0
 */
fun mutableBsonDocumentOf(): MutableBsonDocumentLike {
    return mutableMapOf<String, BsonElement>()
        .asMutableBsonDocument()
}

/**
 * Returns a new [MutableBsonDocumentLike] with the given pairs.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableMapOf
 * @since 2.0.0
 */
fun mutableBsonDocumentOf(vararg pairs: Pair<String, BsonElement>): MutableBsonDocumentLike {
    return mutableMapOf(*pairs)
        .asMutableBsonDocument()
}

/**
 * Return an empty new [MutableBsonDocumentLike].
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableMapOf
 * @since 2.0.0
 */
@Deprecated("Replace with mutableBsonMapOf", ReplaceWith("mutableBsonDocumentOf()"))
fun mutableBsonMapOf() =
    mutableBsonDocumentOf()

/**
 * Returns a new [MutableBsonDocumentLike] with the given pairs.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableMapOf
 * @since 2.0.0
 */
@Deprecated("Replace with mutableBsonDocumentOf", ReplaceWith("mutableBsonDocumentOf(*pairs)"))
fun mutableBsonMapOf(vararg pairs: Pair<String, BsonElement>) =
    mutableBsonDocumentOf(*pairs)

/* ============= ------------------ ============= */

/**
 * Select the element with the perfect tag for the given [lang] preference.
 *
 * @param lang a list of comma-separated language ranges or a list of language
 *                 ranges in the form of the "Accept-Language" header defined in RFC 2616
 * @throws IllegalArgumentException if a language range or a weight found in the
 *                                  given ranges is ill-formed
 * @see Locale.LanguageRange.parse
 */
@OptIn(ExperimentalBsonApi::class)
operator fun BsonDocumentLike.get(name: String, lang: String): Pair<BsonElement?, String> {
    val rangeList = Locale.LanguageRange.parse(lang)
    val langTagList = getLangList(name)
    val langTag = Locale.lookupTag(rangeList, langTagList)
    langTag ?: return get(name) to ""
    if (langTag.isEmpty()) return get(name) to ""
    return get("$name#$langTag") to langTag
}

/**
 * Select the element with the perfect tag for the given [lang] preference.
 *
 * @param lang the languages ordered by preference. (e.g. `["en-US", "ar-SA"]`)
 * @throws IllegalArgumentException if the given range does not comply with the
 *                                  syntax of the language range mentioned
 *                                  in RFC 4647
 * @see Locale.LanguageRange
 */
@OptIn(ExperimentalBsonApi::class)
operator fun BsonDocumentLike.get(name: String, lang: List<String>): Pair<BsonElement?, String> {
    val rangeList = lang.map { Locale.LanguageRange(it) }
    val langTagList = getLangList(name)
    val langTag = Locale.lookupTag(rangeList, langTagList)
    langTag ?: return get(name) to ""
    if (langTag.isEmpty()) return get(name) to ""
    return get("$name#$langTag") to langTag
}

/**
 * Return the tags of the fields that has the given [name].
 */
@ExperimentalBsonApi
fun BsonDocumentLike.getLangList(name: String): List<String> {
    return buildList {
        for (nt in keys) {
            val nts = nt.split("#", limit = 2)
            val n = nts.first()
            val t = nts.getOrNull(1)

            if (n == name)
                add(t ?: "")
        }
    }
}

/* ============= ------------------ ============= */

/**
 * An interface allowing custom receivers for
 * [MutableBsonDocumentLike.by].
 *
 * This interface will be useless after context
 * receivers is released for production.
 * This interface will be removed gradually after
 * context receivers is released for production.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface MutableBsonMapField<T> {
    /**
     * The name of the field.
     */
    val name: String

    /**
     * Encode the given [value] to a bson value.
     */
    fun encode(value: T): BsonElement?
}

/**
 * A builder building a [BsonDocument].
 *
 * This interface will be removed and its functions will be
 * extensions of [MutableBsonDocumentLike] once kotlin context
 * receivers is stable.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface IMutableBsonDocumentLike : BsonDocumentLike, MutableMap<String, BsonElement> {
    /* ============= ------------------ ============= */

    /**
     * Static (pure) utility function to prettify
     * creating arrays within the dsl.
     *
     * Usage:
     * ```
     * BsonDocument {
     *     "name" by array {
     *         by(100L)
     *         by("item")
     *         /* ... */
     *     }
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
     * BsonDocument {
     *     "name" by array(
     *         100L.bson,
     *         "item".bson,
     *         /* ... */
     *     )
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
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun String.by(value: BsonElement?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BsonElement?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: BsonElement?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun String.by(value: BsonDocument?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BsonDocument?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: BsonDocument?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun String.by(value: BsonArray?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BsonArray?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: BsonArray?) {
        name by value
    }

    /* ============= ------------------ ============= */

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Map<String, BsonElement>?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.toBsonDocument()
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Map<String, BsonElement>?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Map<String, BsonElement>?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonConstructorMarker
    infix fun String.by(value: List<BsonElement>?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.toBsonArray()
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: List<BsonElement>?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: List<BsonElement>?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonConstructorMarker
    infix fun String.by(value: String?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: String?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: String?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonConstructorMarker
    infix fun String.by(value: ObjectId?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: ObjectId?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: ObjectId?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.bson].
     *
     */
    @BsonConstructorMarker
    infix fun String.by(value: AnyId?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.bson].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: AnyId?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.bson].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: AnyId?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.bson].
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("byIdList")
    @BsonConstructorMarker
    infix fun String.by(value: List<AnyId>?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.map { it.bson }.toBsonArray()
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.bson].
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("byIdList")
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: List<AnyId>?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.bson].
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("byIdList")
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: List<AnyId>?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Decimal128?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Decimal128?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Decimal128?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun String.by(value: BigDecimal?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BigDecimal?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: BigDecimal?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Date?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Date?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Date?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Instant?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Instant?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDateTime].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Instant?) {
        name by value
    }

    /* ============= ------------------ ============= */

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Boolean?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Boolean?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Boolean?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Int?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Int?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Int?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Long?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Long?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Long?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Double?) {
        value ?: return run { this@IMutableBsonDocumentLike[this] = null.bson }
        this@IMutableBsonDocumentLike[this] = value.bson
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Double?) {
        name by value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [BsonNull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(value: Double?) {
        name by value
    }

    /* ============= ------------------ ============= */

    /**
     * Set the field with the name [this] to the value from invoking the given [block]
     */
    @BsonConstructorMarker
    infix fun String.by(block: BsonDocumentBlock) {
        this@IMutableBsonDocumentLike[this] = BsonDocument(block)
    }

    /**
     * Set the field with the name [this] to the value from invoking the given [block]
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(block: BsonDocumentBlock) {
        name by block
    }

    /**
     * Set the field with the name [this] to the value from invoking the given [block]
     */
    @BsonConstructorMarker
    infix fun MutableBsonMapField<*>.byname(block: BsonDocumentBlock) {
        name by block
    }

    /* ============= ------------------ ============= */

    /**
     * Set the field represented by the [receiver][this] to the given [value].
     */
    @BsonConstructorMarker
    infix fun <T> MutableBsonMapField<T>.by(value: T) {
        this@IMutableBsonDocumentLike[name] = encode(value) ?: null.bson
    }

    /* ============= ------------------ ============= */

    /**
     * Put all the mappings in the given [map].
     */
    @BsonConstructorMarker
    fun byAll(map: Map<String, BsonElement>) {
        this@IMutableBsonDocumentLike += map
    }

    /* ============= ------------------ ============= */
}

/* ============= ------------------ ============= */
