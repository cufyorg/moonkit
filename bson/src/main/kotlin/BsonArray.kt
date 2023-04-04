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
 * A default implementation of [BsonArray].
 *
 * @author LSafer
 * @since 2.0.0
 */
internal class BsonArrayImpl(
    private val content: List<BsonElement>
) : BsonArray, List<BsonElement> by content {
    override fun equals(other: Any?) =
        content == other

    override fun hashCode() =
        content.hashCode()

    override fun toString() =
        content.joinToString(",", "[", "]")
}

/* ============= ------------------ ============= */

/**
 * A block of code building a bson array.
 *
 * @since 2.0.0
 */
typealias BsonArrayBlock = MutableBsonArray.() -> Unit

/**
 * A mutable implementation of [BsonArray].
 *
 * @author LSafer
 * @since 2.0.0
 */
class MutableBsonArray(
    /**
     * The array currently building.
     */
    private val content: MutableList<BsonElement> = mutableListOf()
) : BsonArray, MutableList<BsonElement> by content {
    /* ============= ------------------ ============= */

    override fun equals(other: Any?) =
        content == other

    override fun hashCode() =
        content.hashCode()

    override fun toString() =
        content.joinToString(",", "[", "]")

    /* ============= ------------------ ============= */

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonElement?) {
        value ?: return run { content += bnull }
        content += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonDocument?) {
        value ?: return run { content += bnull }
        content += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonConstructorMarker
    fun by(value: BsonArray?) {
        value ?: return run { content += bnull }
        content.add(value)
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
        value ?: return run { content += bnull }
        content += value.toBsonDocument()
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
        value ?: return run { content += bnull }
        content.add(value.toBsonArray())
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
        value ?: return run { content += bnull }
        content += value.b
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
        value ?: return run { content += bnull }
        content += value.b
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
        value ?: return run { content += bnull }
        content += value.b
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.b].
     */
    @JvmName("byIdList")
    @BsonConstructorMarker
    fun by(value: List<Id<*>>?) {
        value ?: return run { content += bnull }
        content.add(value.map { it.b }.toBsonArray())
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
        value ?: return run { content += bnull }
        content += value.b
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
        value ?: return run { content += bnull }
        content += value.b
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
        value ?: return run { content += bnull }
        content += value.b
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
        value ?: return run { content += bnull }
        content += value.b
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
        value ?: return run { content += bnull }
        content += value.b
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
        value ?: return run { content += bnull }
        content += value.b
    }

    /* ============= ------------------ ============= */

    /**
     * Add the value from invoking the given [block].
     */
    @BsonConstructorMarker
    fun by(block: BsonDocumentBlock) {
        content += BsonDocument(block)
    }

    /* ============= ------------------ ============= */

    /**
     * Put all the items in the given [list].
     */
    @BsonConstructorMarker
    fun byAll(list: List<BsonElement>) {
        content += list
    }

    /* ============= ------------------ ============= */
}

/* ============= ------------------ ============= */
