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
import org.intellij.lang.annotations.Language

/* ============= ------------------ ============= */

/**
 * **Expands To:**
 *
 * ```kotlin
 * "_id" by { `$eq` by value }
 * ```
 *
 * @since 2.0.0
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.ID(value: BsonElement) {
    "_id" by { `$eq` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { "_id" by { `$eq` by value } }
 * ```
 *
 * @since 2.0.0
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.ID(value: BsonElement) {
    by { ID(value) }
}

/* ============= ------------------ ============= */

/**
 * **Expands To:**
 *
 * ```kotlin
 * name by { `$eq` by value }
 * ```
 *
 * @since 2.0.0
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.EQ(name: String, value: BsonElement) {
    name by value
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$eq` by value } }
 * ```
 *
 * @since 2.0.0
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.EQ(name: String, value: BsonElement) {
    by { EQ(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$ne` by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.ne
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.NE(name: String, value: BsonElement) {
    name by { `$ne` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$ne` by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.ne
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.NE(name: String, value: BsonElement) {
    by { NE(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$gt` by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.gt
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.GT(name: String, value: BsonElement) {
    name by { `$gt` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$gt` by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.gt
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.GT(name: String, value: BsonElement) {
    by { GT(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$lt` by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.lt
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.LT(name: String, value: BsonElement) {
    name by { `$lt` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$lt` by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.lt
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.LT(name: String, value: BsonElement) {
    by { LT(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$gte` by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.gte
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.GTE(name: String, value: BsonElement) {
    name by { `$gte` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$gte` by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.gte
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.GTE(name: String, value: BsonElement) {
    by { GTE(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$lte` by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.lte
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.LTE(name: String, value: BsonElement) {
    name by { `$gte` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$lte` by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.lte
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.LTE(name: String, value: BsonElement) {
    by { LTE(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$in` by array(block) }
 * ```
 *
 * @since 2.0.0
 * @formatter:off
 * @see com.mongodb.client.model.Filters.in
 * @formatter:on
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.IN(name: String, block: BsonArrayBlock) {
    name by { `$in` by array(block) }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$in` by array(block) } }
 * ```
 *
 * @since 2.0.0
 * @formatter:off
 * @see com.mongodb.client.model.Filters.in
 * @formatter:on
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.IN(name: String, block: BsonArrayBlock) {
    by { IN(name, block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$nin` by array(block) }
 * ```
 *
 * @since 2.0.0
 * @formatter:off
 * @see com.mongodb.client.model.Filters.nin
 * @formatter:on
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.NIN(name: String, block: BsonArrayBlock) {
    name by { `$nin` by array(block) }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$nin` by array(block) } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.nin
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.NIN(name: String, block: BsonArrayBlock) {
    by { NIN(name, block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$and` by array(block)
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.and
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.AND(block: BsonArrayBlock) {
    `$and` by array(block)
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$and` by array(block) }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.and
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.AND(block: BsonArrayBlock) {
    by { AND(block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$or` by array(block)
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.or
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.OR(block: BsonArrayBlock) {
    `$or` by array(block)
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$or` by array(block)
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.or
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.OR(block: BsonArrayBlock) {
    by { OR(block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$not` by block
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.not
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.NOT(block: BsonDocumentBlock) {
    `$not` by block
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$not` by block }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.not
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.NOT(block: BsonArrayBlock) {
    by { NOT(block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$nor` by array(block)
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.nor
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.NOR(block: BsonArrayBlock) {
    `$nor` by array(block)
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$nor` by array(block) }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.nor
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.NOR(block: BsonArrayBlock) {
    by { NOR(block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$exists` by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.exists
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.EXISTS(name: String, value: Boolean = true) {
    name by { `$exists` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$exists` by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.exists
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.EXISTS(name: String, value: Boolean = true) {
    by { EXISTS(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$type` by value.value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.type
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.TYPE(name: String, value: BsonType) {
    name by { `$type` by value.value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$type` by value.value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.type
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.TYPE(name: String, value: BsonType) {
    by { TYPE(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$mod` by array(divisor, remainder) }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.mod
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.MOD(name: String, divisor: Long, remainder: Long) {
    name by { `$mod` by array(divisor.bson, remainder.bson) }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$mod` by array(divisor, remainder) } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.mod
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.MOD(name: String, divisor: Long, remainder: Long) {
    by { MOD(name, divisor, remainder) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by BsonRegExp(pattern, options)
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.regex
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.REGEXP(name: String, @Language("RegExp") pattern: String, options: String) {
    name by BsonRegExp(pattern, options)
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by BsonRegExp(pattern, options) }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.regex
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.REGEXP(name: String, @Language("RegExp") pattern: String, options: String) {
    by { REGEXP(name, pattern, options) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by BsonRegExp(pattern, options)
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.regex
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.REGEXP(name: String, @Language("RegExp") pattern: String, options: Set<Char> = emptySet()) {
    name by BsonRegExp(pattern, options)
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by BsonRegExp(pattern, options) }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.regex
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.REGEXP(name: String, @Language("RegExp") pattern: String, options: Set<Char> = emptySet()) {
    by { REGEXP(name, pattern, options) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$text` by { `$search` by search; /* ... */ }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.text
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.TEXT(search: String, options: TextSearchOptions = TextSearchOptions()) {
    `$text` by {
        `$search` by search
        options.language?.let { "\$language" by it }
        options.caseSensitive?.let { "\$caseSensitive" by it }
        options.diacriticSensitive?.let { "\$diacriticSensitive" by it }
    }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$text` by { `$search` by search; /* ... */ } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.text
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.TEXT(search: String, options: TextSearchOptions = TextSearchOptions()) {
    by { TEXT(search, options) }
}

//

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$text` by { `$search` by search; /* ... */ }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.text
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.TEXT(search: String, options: TextSearchOptions.() -> Unit) {
    TEXT(search, TextSearchOptions(options))
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$text` by { `$search` by search; /* ... */ } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.text
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.TEXT(search: String, options: TextSearchOptions.() -> Unit) {
    by { TEXT(search, options) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$where` by expression
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.where
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.WHERE(@Language("JavaScript") expression: String) {
    `$where` by expression
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$where` by expression }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.where
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.WHERE(expression: String) {
    by { WHERE(expression) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$expr` by expression
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.expr
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.EXPR(expression: BsonDocumentBlock) {
    `$expr` by expression
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$expr` by expression }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.expr
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.EXPR(expression: String) {
    by { EXPR(expression) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$all` by array(block) }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.all
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.ALL(name: String, block: BsonArrayBlock) {
    name by { `$all` by array(block) }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$all` by array(block) } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.all
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.ALL(name: String, block: BsonArrayBlock) {
    by { ALL(name, block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$elemMatch` by block }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.elemMatch
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.ELEM_MATCH(name: String, block: BsonDocumentBlock) {
    name by { `$elemMatch` by block }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$elemMatch` by block } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.elemMatch
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.ELEM_MATCH(name: String, block: BsonDocumentBlock) {
    by { ELEM_MATCH(name, block) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$size` by value }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.size
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.SIZE(name: String, value: Int) {
    name by { `$size` by value }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$size` by value } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.size
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.SIZE(name: String, value: Int) {
    by { SIZE(name, value) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$bitsAllClear` by bitmask }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAllClear
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.BITS_ALL_CLEAR(name: String, bitmask: Long) {
    name by { `$bitsAllClear` by bitmask }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$bitsAllClear` by bitmask } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAllClear
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.BITS_ALL_CLEAR(name: String, bitmask: Long) {
    by { BITS_ALL_CLEAR(name, bitmask) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$bitsAllSet` by bitmask }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAllSet
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.BITS_ALL_SET(name: String, bitmask: Long) {
    name by { `$bitsAllSet` by bitmask }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$bitsAllSet` by bitmask } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAllSet
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.BITS_ALL_SET(name: String, bitmask: Long) {
    by { BITS_ALL_SET(name, bitmask) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$bitsAnyClear` by bitmask }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAnyClear
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.BITS_ANY_CLEAR(name: String, bitmask: Long) {
    name by { `$bitsAnyClear` by bitmask }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$bitsAnyClear` by bitmask } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAnyClear
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.BITS_ANY_CLEAR(name: String, bitmask: Long) {
    by { BITS_ANY_CLEAR(name, bitmask) }
}

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * name by { `$bitsAnySet` by bitmask }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAnySet
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.BITS_ANY_SET(name: String, bitmask: Long) {
    name by { `$bitsAnySet` by bitmask }
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { name by { `$bitsAnySet` by bitmask } }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.bitsAnySet
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.BITS_ANY_SET(name: String, bitmask: Long) {
    by { BITS_ANY_SET(name, bitmask) }
}

/* ============= ------------------ ============= */

// TODO GEO_WITHIN
// TODO GEO_WITHIN_BOX
// TODO GEO_WITHIN_POLYGON
// TODO GEO_WITHIN_CENTER
// TODO GEO_WITHIN_CENTER_SPHERE
// TODO GEO_INTERSECTS
// TODO NEAR
// TODO NEAR_SPHERE
// TODO NEAR_SPHERE

/* ============= ------------------ ============= */

/**
 * ## Expands To:
 *
 * ```kotlin
 * `$jsonSchema` by schema
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.jsonSchema
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonDocumentLike.JSON_SCHEMA(schema: BsonDocumentBlock) {
    `$jsonSchema` by schema
}

/**
 * ## Expands To:
 *
 * ```kotlin
 * by { `$jsonSchema` by schema }
 * ```
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.Filters.jsonSchema
 */
@BsonConstructorMarker
@ExperimentalMongodbApi
fun MutableBsonArrayLike.JSON_SCHEMA(schema: BsonDocumentBlock) {
    by { JSON_SCHEMA(schema) }
}

/* ============= ------------------ ============= */
