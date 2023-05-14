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

/* ============= ------------------ ============= */

/**
 * A list containing only items of type [BsonElement].
 *
 * @since 2.0.0
 */
typealias BsonList = List<BsonElement>

/* ============= ------------------ ============= */

/**
 * A mutable list containing only items of type [BsonElement].
 *
 * This interface will be replaced with a `typealias` once
 * kotlin context receivers is stable.
 *
 * @since 2.0.0
 */
interface MutableBsonList : BsonList, MutableList<BsonElement> {
    /* ============= ------------------ ============= */

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonElement?) {
        value ?: return run { this += bnull }
        this += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonDocument?) {
        value ?: return run { this += bnull }
        this += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonArray?) {
        value ?: return run { this += bnull }
        this.add(value)
    }

    /* ============= ------------------ ============= */

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonConstructorMarker
    fun by(value: Map<String, BsonElement>?) {
        value ?: return run { this += bnull }
        this += value.toBsonDocument()
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonConstructorMarker
    fun by(value: List<BsonElement>?) {
        value ?: return run { this += bnull }
        this.add(value.toBsonArray())
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonConstructorMarker
    fun by(value: String?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonConstructorMarker
    fun by(value: ObjectId?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.b].
     */
    @BsonConstructorMarker
    fun by(value: Id<*>?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.b].
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("byIdList")
    @BsonConstructorMarker
    fun by(value: List<Id<*>>?) {
        value ?: return run { this += bnull }
        this.add(value.map { it.b }.toBsonArray())
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    fun by(value: Decimal128?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonConstructorMarker
    fun by(value: BigDecimal?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /* ============= ------------------ ============= */

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonConstructorMarker
    fun by(value: Boolean?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonConstructorMarker
    fun by(value: Int?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonConstructorMarker
    fun by(value: Long?) {
        value ?: return run { this += bnull }
        this += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonConstructorMarker
    fun by(value: Double?) {
        value ?: return run { this += bnull }
        this += value.b
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

/**
 * Return an empty new [MutableBsonList].
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableListOf
 * @since 2.0.0
 */
fun mutableBsonListOf(): MutableBsonList {
    return mutableListOf<BsonElement>()
        .asMutableBsonList()
}

/**
 * Returns a new [MutableBsonList] with the given elements.
 *
 * This function will be obsolete once kotlin
 * context receivers is stable.
 *
 * @see mutableListOf
 * @since 2.0.0
 */
fun mutableBsonListOf(vararg elements: BsonElement): MutableBsonList {
    return mutableListOf(*elements)
        .asMutableBsonList()
}

/**
 * Create a new array from combining this array with the given [list].
 */
operator fun BsonArray.plus(list: BsonList): BsonArray {
    return BsonArray {
        byAll(this)
        byAll(list)
    }
}

/**
 * Create a new array from combining this array with the given [block].
 */
operator fun BsonArray.plus(block: BsonArrayBlock): BsonArray {
    return BsonArray {
        byAll(this)
        block()
    }
}

/* ============= ------------------ ============= */
