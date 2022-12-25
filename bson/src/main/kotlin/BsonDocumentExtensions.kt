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

/**
 * Construct a new bson document by copying the given map.
 */
fun BsonDocument(value: Map<String, BsonValue>): BsonDocument {
    return BsonDocument(value.map { BsonElement(it.key, it.value) })
}

//

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 */
operator fun BsonDocument.set(name: String, value: BsonValue?) {
    put(name, value ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 */
operator fun BsonDocument.set(name: String, value: BsonDocument?) {
    put(name, value ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 */
operator fun BsonDocument.set(name: String, value: BsonArray?) {
    put(name, value ?: bnull)
}

//

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonDocument].
 */
operator fun BsonDocument.set(name: String, value: Map<String, BsonValue>?) {
    put(name, value?.let { BsonDocument(it) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonArray].
 */
operator fun BsonDocument.set(name: String, value: List<BsonValue>?) {
    put(name, value?.let { BsonArray(it) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonString].
 */
operator fun BsonDocument.set(name: String, value: String?) {
    put(name, value?.let { BsonString(it) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonObjectId].
 */
operator fun BsonDocument.set(name: String, value: ObjectId?) {
    put(name, value?.let { BsonObjectId(it) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped using [Id.bson].
 */
operator fun BsonDocument.set(name: String, value: Id<*>?) {
    put(name, value?.bson ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped using [BsonArray] and [Id.bson].
 */
@JvmName("setIdList")
operator fun BsonDocument.set(name: String, value: List<Id<*>>?) {
    put(name, value?.let { BsonArray(it.map { it.bson }) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonDecimal128].
 */
operator fun BsonDocument.set(name: String, value: Decimal128?) {
    put(name, value?.let { BsonDecimal128(it) } ?: bnull)
}

//

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonBoolean].
 */
operator fun BsonDocument.set(name: String, value: Boolean?) {
    put(name, value?.let { BsonBoolean(it) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonDouble].
 */
operator fun BsonDocument.set(name: String, value: Double?) {
    put(name, value?.let { BsonDouble(it) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonInt32].
 */
operator fun BsonDocument.set(name: String, value: Int?) {
    put(name, value?.let { BsonInt32(it) } ?: bnull)
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * If [value] is null then [bnull] will be set instead.
 *
 * The given [value] will be wrapped with [BsonInt64].
 */
operator fun BsonDocument.set(name: String, value: Long?) {
    put(name, value?.let { BsonInt64(it) } ?: bnull)
}

//

/**
 * Set the field with the name [name] to the given [value].
 *
 * The given [value] will be wrapped with [BsonBoolean].
 */
operator fun BsonDocument.set(name: String, value: Boolean) {
    put(name, BsonBoolean(value))
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * The given [value] will be wrapped with [BsonDouble].
 */
operator fun BsonDocument.set(name: String, value: Double) {
    put(name, BsonDouble(value))
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * The given [value] will be wrapped with [BsonInt32].
 */
operator fun BsonDocument.set(name: String, value: Int) {
    put(name, BsonInt32(value))
}

/**
 * Set the field with the name [name] to the given [value].
 *
 * The given [value] will be wrapped with [BsonInt64].
 */
operator fun BsonDocument.set(name: String, value: Long) {
    put(name, BsonInt64(value))
}
