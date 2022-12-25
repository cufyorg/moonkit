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

import kotlin.reflect.KCallable

/**
 * A block of code building a bson document.
 *
 * @since 2.0.0
 */
typealias BsonDocumentBlock = BsonDocumentBuilder.() -> Unit

/**
 * A builder building a [BsonDocument].
 *
 * @author LSafer
 * @since 2.0.0
 */
class BsonDocumentBuilder(
    /**
     * The document currently building.
     */
    val document: BsonDocument = BsonDocument()
) {
    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    infix fun String.by(value: BsonValue?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: BsonValue?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    infix fun String.by(value: BsonDocument?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: BsonDocument?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    infix fun String.by(value: BsonArray?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: BsonArray?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonBuildMarker
    infix fun String.by(value: Map<String, BsonValue>?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDocument].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: Map<String, BsonValue>?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonBuildMarker
    infix fun String.by(value: List<BsonValue>?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonArray].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: List<BsonValue>?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonBuildMarker
    infix fun String.by(value: String?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonString].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: String?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonBuildMarker
    infix fun String.by(value: ObjectId?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonObjectId].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: ObjectId?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.bson].
     *
     */
    @BsonBuildMarker
    infix fun String.by(value: Id<*>?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [Id.bson].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: Id<*>?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.bson].
     */
    @JvmName("byIdList")
    @BsonBuildMarker
    infix fun String.by(value: List<Id<*>>?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped using [BsonArray] and [Id.bson].
     */
    @JvmName("byIdList")
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: List<Id<*>>?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonBuildMarker
    infix fun String.by(value: Decimal128?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDecimal128].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: Decimal128?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonBuildMarker
    infix fun String.by(value: Boolean?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonBoolean].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: Boolean?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonBuildMarker
    infix fun String.by(value: Double?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonDouble].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: Double?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonBuildMarker
    infix fun String.by(value: Int?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt32].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: Int?) {
        name by value
    }


    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonBuildMarker
    infix fun String.by(value: Long?) {
        document[this] = value
    }

    /**
     * Set the field with the name [this] to the given [value].
     *
     * If [value] is null then [bnull] will be set instead.
     *
     * The given [value] will be wrapped with [BsonInt64].
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(value: Long?) {
        name by value
    }

    //

    /**
     * Set the field with the name [this] to the value from invoking the given [block]
     */
    @BsonBuildMarker
    infix fun String.by(block: BsonDocumentBlock) {
        document[this] = document(block)
    }

    /**
     * Set the field with the name [this] to the value from invoking the given [block]
     */
    @BsonBuildMarker
    infix fun KCallable<*>.by(block: BsonDocumentBlock) {
        name by block
    }
}

/**
 * Construct a new bson document using the given
 * builder [block].
 */
@BsonBuildMarker
fun document(
    block: BsonDocumentBlock = {}
): BsonDocument {
    val builder = BsonDocumentBuilder()
    builder.apply(block)
    return builder.document
}

/**
 * Construct a new bson document with the given [pairs]
 */
@BsonBuildMarker
fun document(
    vararg pairs: Pair<String, BsonValue>
): BsonDocument {
    return BsonDocument(pairs.toMap())
}

/**
 * Apply the given [block] to this document.
 */
fun BsonDocument.configure(
    block: BsonDocumentBlock
) {
    val builder = BsonDocumentBuilder(this)
    builder.apply(block)
}
