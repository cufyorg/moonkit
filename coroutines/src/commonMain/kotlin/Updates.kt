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
@file:Suppress("FunctionName")

package org.cufy.mongodb

import org.cufy.bson.*

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$set` by block
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.set
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.SET(block: BsonDocumentBlock) {
    `$set` by block
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$set` by block }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.set
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.SET(block: BsonDocumentBlock) {
    by { SET(block) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$set` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.set
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.SET(name: String, value: BsonElement) {
    `$set` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$set` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.set
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.SET(name: String, value: BsonElement) {
    by { SET(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$unset` by block
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.unset
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.UNSET(block: BsonDocumentBlock) {
    `$unset` by block
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$unset` by block }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.unset
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.UNSET(block: BsonDocumentBlock) {
    by { UNSET(block) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$unset` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.unset
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.UNSET(name: String, value: BsonElement) {
    `$unset` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$unset` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.unset
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.UNSET(name: String, value: BsonElement) {
    by { UNSET(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$setOnInsert` by block
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.setOnInsert
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.SET_ON_INSERT(block: BsonDocumentBlock) {
    `$setOnInsert` by block
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$setOnInsert` by block }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.setOnInsert
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.SET_ON_INSERT(block: BsonDocumentBlock) {
    by { SET_ON_INSERT(block) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$setOnInsert` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.setOnInsert
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.SET_ON_INSERT(name: String, value: BsonElement) {
    `$setOnInsert` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$setOnInsert` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.setOnInsert
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.SET_ON_INSERT(name: String, value: BsonElement) {
    by { SET_ON_INSERT(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$rename` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.rename
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.RENAME(name: String, value: String) {
    `$rename` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$rename` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.rename
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.RENAME(name: String, value: String) {
    by { RENAME(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$inc` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.inc
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.INC(name: String, value: BsonNumber) {
    `$inc` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$inc` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.inc
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.INC(name: String, value: BsonNumber) {
    by { INC(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$mul` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.mul
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.MUL(name: String, value: BsonNumber) {
    `$mul` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$mul` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.mul
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.MUL(name: String, value: BsonNumber) {
    by { MUL(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$min` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.min
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.MIN(name: String, value: BsonNumber) {
    `$min` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$min` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.min
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.MIN(name: String, value: BsonNumber) {
    by { MIN(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$max` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.max
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.MAX(name: String, value: BsonNumber) {
    `$max` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$max` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.max
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.MAX(name: String, value: BsonNumber) {
    by { MAX(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$currentDate` by { name by true }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.currentDate
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.CURRENT_DATE(name: String) {
    `$currentDate` by { name by true }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$currentDate` by { name by true } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.currentDate
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.CURRENT_DATE(name: String) {
    by { CURRENT_DATE(name) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$currentDate` by { name by { `$type` by "timestamp" } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.currentTimestamp
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.CURRENT_TIMESTAMP(name: String) {
    `$currentDate` by { name by { `$type` by "timestamp" } }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$currentDate` by { name by { `$type` by "timestamp" } } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.currentTimestamp
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.CURRENT_TIMESTAMP(name: String) {
    by { CURRENT_TIMESTAMP(name) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$addToSet` by block
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.addToSet
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.ADD_TO_SET(block: BsonDocumentBlock) {
    `$addToSet` by block
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$addToSet` by block }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.addToSet
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.ADD_TO_SET(block: BsonDocumentBlock) {
    by { ADD_TO_SET(block) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$addToSet` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.addToSet
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.ADD_TO_SET(name: String, value: BsonElement) {
    `$addToSet` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$addToSet` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.addToSet
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.ADD_TO_SET(name: String, value: BsonElement) {
    by { ADD_TO_SET(name, value) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$addToSet` by { name by { `$each` by array(block) } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.addEachToSet
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.ADD_EACH_TO_SET(name: String, block: BsonArrayBlock) {
    `$addToSet` by { name by { `$each` by array(block) } }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$addToSet` by { name by { `$each` by array(block) } } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.addEachToSet
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.ADD_EACH_TO_SET(name: String, block: BsonArrayBlock) {
    by { ADD_EACH_TO_SET(name, block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$push` by block
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.push
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.PUSH(block: BsonDocumentBlock) {
    `$push` by block
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$push` by block }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.push
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.PUSH(block: BsonDocumentBlock) {
    by { PUSH(block) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$push` by { name by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.push
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.PUSH(name: String, value: BsonElement) {
    `$push` by { name by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$push` by { name by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.push
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.PUSH(name: String, value: BsonElement) {
    by { PUSH(name, value) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$push` by { name by { `$each` by array(block); /* ... */ } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.pushEach
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.PUSH_EACH(name: String, options: PushOptions = PushOptions(), block: BsonArrayBlock) {
    `$push` by {
        name by {
            `$each` by array(block)
            options.position?.let { `$position` by it }
            options.slice?.let { `$slice` by it }
            options.sort?.let { `$sort` by it }
        }
    }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$push` by { name by { `$each` by array(block); /* ... */ } } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.pushEach
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.PUSH_EACH(name: String, options: PushOptions = PushOptions(), block: BsonArrayBlock) {
    by { PUSH_EACH(name, options, block) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$push` by { name by { `$each` by array(block); /* ... */ } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.pushEach
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.PUSH_EACH(name: String, options: PushOptions.() -> Unit, block: BsonArrayBlock) {
    PUSH_EACH(name, PushOptions(options), block)
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$push` by { name by { `$each` by array(block); /* ... */ } } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Updates.pushEach
 */
@BsonMarker2
@ExperimentalMongodbApi
fun MutableBsonArrayLike.PUSH_EACH(name: String, options: PushOptions.() -> Unit, block: BsonArrayBlock) {
    by { PUSH_EACH(name, options, block) }
}

/* ============= ------------------ ============= */

// TODO PULL
// TODO PULL_BY_FILTER
// TODO PULL_ALL
// TODO POP_FIRST
// TODO POP_LAST
// TODO BITWISE_AND
// TODO BITWISE_OR
// TODO BITWISE_XOR

/* ============= ------------------ ============= */
