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

import java.math.BigDecimal
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
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun String.by(value: BsonElement?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BsonElement?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun String.by(value: BsonDocument?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BsonDocument?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun String.by(value: BsonArray?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BsonArray?) {
        name by value
    }

    /* ============= ------------------ ============= */

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Map<String, BsonElement>?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.toBsonDocument()
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Map<String, BsonElement>?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonConstructorMarker
    infix fun String.by(value: List<BsonElement>?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.toBsonArray()
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: List<BsonElement>?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonConstructorMarker
    infix fun String.by(value: String?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: String?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonConstructorMarker
    infix fun String.by(value: ObjectId?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: ObjectId?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.b].
     *
     */
    @BsonConstructorMarker
    infix fun String.by(value: Id<*>?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.b].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Id<*>?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.b].
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("byIdList")
    @BsonConstructorMarker
    infix fun String.by(value: List<Id<*>>?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.map { it.b }.toBsonArray()
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.b].
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("byIdList")
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: List<Id<*>>?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Decimal128?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Decimal128?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun String.by(value: BigDecimal?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: BigDecimal?) {
        name by value
    }

    /* ============= ------------------ ============= */

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Boolean?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Boolean?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Int?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Int?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Long?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Long?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonConstructorMarker
    infix fun String.by(value: Double?) {
        value ?: return run { this@MutableBsonMap[this] = bnull }
        this@MutableBsonMap[this] = value.b
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonConstructorMarker
    infix fun KCallable<*>.by(value: Double?) {
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

    /* ============= ------------------ ============= */

    /**
     * Set the field represented by the [receiver][this] to the given [value].
     */
    @BsonConstructorMarker
    infix fun <T> MutableBsonMapField<T>.by(value: T) {
        this@MutableBsonMap[name] = encode(value) ?: bnull
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
