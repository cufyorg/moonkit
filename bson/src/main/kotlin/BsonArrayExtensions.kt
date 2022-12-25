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

//

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 */
operator fun BsonArray.plusAssign(value: BsonValue?) {
    add(value ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 */
operator fun BsonArray.plusAssign(value: BsonDocument?) {
    add(value ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 */
operator fun BsonArray.plusAssign(value: BsonArray?) {
    add(value ?: bnull)
}

//

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonDocument].
 */
operator fun BsonArray.plusAssign(value: Map<String, BsonValue>?) {
    add(value?.let { BsonDocument(it) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonArray].
 */
operator fun BsonArray.plusAssign(value: List<BsonValue>?) {
    add(value?.let { BsonArray(it) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonString].
 */
operator fun BsonArray.plusAssign(value: String?) {
    add(value?.let { BsonString(it) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonObjectId].
 */
operator fun BsonArray.plusAssign(value: ObjectId?) {
    add(value?.let { BsonObjectId(it) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped using [Id.bson].
 */
operator fun BsonArray.plusAssign(value: Id<*>?) {
    add(value?.bson ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped using [BsonArray] and [Id.bson].
 */
@JvmName("plusAssignIdList")
operator fun BsonArray.plusAssign(value: List<Id<*>>?) {
    add(value?.let { BsonArray(it.map { it.bson }) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonDecimal128].
 */
operator fun BsonArray.plusAssign(value: Decimal128?) {
    add(value?.let { BsonDecimal128(it) } ?: bnull)
}

//

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonBoolean].
 */
operator fun BsonArray.plusAssign(value: Boolean?) {
    add(value?.let { BsonBoolean(it) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonInt32].
 */
operator fun BsonArray.plusAssign(value: Int?) {
    add(value?.let { BsonInt32(it) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonInt64].
 */
operator fun BsonArray.plusAssign(value: Long?) {
    add(value?.let { BsonInt64(it) } ?: bnull)
}

/**
 * Add the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonDouble].
 */
operator fun BsonArray.plusAssign(value: Double?) {
    add(value?.let { BsonDouble(it) } ?: bnull)
}

//

/**
 * Add the given [value].
 *
 * The given [value] will be wrapped with [BsonBoolean].
 */
operator fun BsonArray.plusAssign(value: Boolean) {
    add(BsonBoolean(value))
}

/**
 * Add the given [value].
 *
 * The given [value] will be wrapped with [BsonInt32].
 */
operator fun BsonArray.plusAssign(value: Int) {
    add(BsonInt32(value))
}

/**
 * Add the given [value].
 *
 * The given [value] will be wrapped with [BsonInt64].
 */
operator fun BsonArray.plusAssign(value: Long) {
    add(BsonInt64(value))
}

/**
 * Add the given [value].
 *
 * The given [value] will be wrapped with [BsonDouble].
 */
operator fun BsonArray.plusAssign(value: Double) {
    add(BsonDouble(value))
}
