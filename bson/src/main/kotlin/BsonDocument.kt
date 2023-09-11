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
typealias BsonMap = Map<String, BsonElement>

/* ============= ------------------ ============= */

/**
 * An interface allowing custom receivers for
 * [MutableBsonMap.by].
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
 * @author LSafer
 * @since 2.0.0
 */
interface MutableBsonMap : BsonMap, MutableMap<String, BsonElement> {
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.toBsonDocument()
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.toBsonArray()
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.map { it.bson }.toBsonArray()
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        value ?: return run { this@MutableBsonMap[this] = null.bson }
        this@MutableBsonMap[this] = value.bson
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
        this@MutableBsonMap[this] = BsonDocument(block)
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
        this@MutableBsonMap[name] = encode(value) ?: null.bson
    }

    /* ============= ------------------ ============= */

    /**
     * Put all the mappings in the given [map].
     */
    @BsonConstructorMarker
    fun byAll(map: Map<String, BsonElement>) {
        this@MutableBsonMap += map
    }

    /* ============= ------------------ ============= */
}

/**
 * Return an empty new [MutableBsonMap].
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableMapOf
 * @since 2.0.0
 */
fun mutableBsonMapOf(): MutableBsonMap {
    return mutableMapOf<String, BsonElement>()
        .asMutableBsonMap()
}

/**
 * Returns a new [MutableBsonMap] with the given pairs.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableMapOf
 * @since 2.0.0
 */
fun mutableBsonMapOf(vararg pairs: Pair<String, BsonElement>): MutableBsonMap {
    return mutableMapOf(*pairs)
        .asMutableBsonMap()
}

/**
 * Create a new document from combining this document with the given [map].
 */
operator fun BsonDocument.plus(map: BsonMap): BsonDocument {
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
