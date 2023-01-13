/*
 *	Copyright 2022 cufy.org
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

/**
 * A block of code building a bson array.
 *
 * @since 2.0.0
 */
typealias BsonArrayBlock = BsonArrayBuilder.() -> Unit

/**
 * A builder building a [BsonArray].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class BsonArrayBuilder(
    /**
     * The array currently building.
     */
    val array: BsonArray = BsonArray()
) {
    //

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    fun by(value: BsonValue?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    fun by(value: BsonDocument?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    fun by(value: BsonArray?) {
        array += value
    }

    //

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonBuildMarker
    fun by(value: Map<String, BsonValue>?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonBuildMarker
    fun by(value: List<BsonValue>?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonBuildMarker
    fun by(value: String?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonBuildMarker
    fun by(value: ObjectId?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.bson].
     */
    @BsonBuildMarker
    fun by(value: Id<*>?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.bson].
     */
    @JvmName("byIdList")
    @BsonBuildMarker
    fun by(value: List<Id<*>>?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonBuildMarker
    fun by(value: Decimal128?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonBuildMarker
    fun by(value: BigDecimal?) {
        array += value
    }

    //

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonBuildMarker
    fun by(value: Boolean?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonBuildMarker
    fun by(value: Int?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonBuildMarker
    fun by(value: Long?) {
        array += value
    }

    /**
     * Add the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonBuildMarker
    fun by(value: Double?) {
        array += value
    }

    //

    /**
     * Add the value from invoking the given [block].
     */
    @BsonBuildMarker
    fun by(block: BsonDocumentBlock) {
        array += document(block)
    }

    //

    /**
     * Put all the items in the given [list].
     */
    @BsonBuildMarker
    fun byAll(list: List<BsonValue>) {
        array += list
    }
}

/**
 * Construct a new bson array using the given
 * builder [block].
 */
@BsonBuildMarker
fun array(
    block: BsonArrayBlock = {}
): BsonArray {
    val builder = BsonArrayBuilder()
    builder.apply(block)
    return builder.array
}

/**
 * Construct a new bson array with the given [items]
 */
@BsonBuildMarker
fun array(
    vararg items: BsonValue
): BsonArray {
    return BsonArray(items.toList())
}

/**
 * Apply the given [block] to this array.
 */
fun BsonArray.configure(
    block: BsonArrayBlock
) {
    val builder = BsonArrayBuilder(this)
    builder.apply(block)
}
