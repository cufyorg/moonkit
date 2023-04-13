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
package org.cufy.bson.java

import org.cufy.bson.*

/* ============= ------------------ ============= */

internal typealias JavaBsonElement =
        org.bson.BsonValue

/**
 * Return the java version of this bson element.
 */
val BsonElement.java: JavaBsonElement
    get() = when (this) {
        is BsonDocument -> java
        is BsonArray -> java
        is BsonString -> java
        is BsonRegExp -> java
        is BsonObjectId -> java
        is BsonInt32 -> java
        is BsonInt64 -> java
        is BsonDouble -> java
        is BsonDecimal128 -> java
        is BsonBoolean -> java
        is BsonUndefined -> java
        is BsonNull -> java
    }

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonElement.kt: BsonElement
    get() = when (this) {
        is JavaBsonDocument -> kt
        is JavaBsonArray -> kt
        is JavaBsonString -> kt
        is JavaBsonRegExp -> kt
        is JavaBsonObjectId -> kt
        is JavaBsonInt32 -> kt
        is JavaBsonInt64 -> kt
        is JavaBsonDouble -> kt
        is JavaBsonDecimal128 -> kt
        is JavaBsonBoolean -> kt
        is JavaBsonUndefined -> kt
        is JavaBsonNull -> kt
        else -> error("Unsupported bson element: $bsonType")
    }

//

internal typealias JavaBsonDocument =
        org.bson.BsonDocument

/**
 * Return the java version of this bson element.
 */
val BsonDocument.java: JavaBsonDocument
    get() {
        val document = JavaBsonDocument()
        document.putAll(mapValues { it.value.java })
        return document
    }

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonDocument.kt: BsonDocument
    get() = BsonDocument { this@kt.mapValuesTo(this) { it.value.kt } }

//

internal typealias JavaBsonArray =
        org.bson.BsonArray

/**
 * Return the java version of this bson element.
 */
val BsonArray.java: JavaBsonArray
    get() = JavaBsonArray(map { it.java })

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonArray.kt: BsonArray
    get() = BsonArray { this@kt.mapTo(this) { it.kt } }

//

internal typealias JavaBsonString =
        org.bson.BsonString

/**
 * Return the java version of this bson element.
 */
val BsonString.java: JavaBsonString
    get() = JavaBsonString(value)

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonString.kt: BsonString
    get() = BsonString(value)

//

internal typealias JavaBsonRegExp =
        org.bson.BsonRegularExpression

/**
 * Return the java version of this bson element.
 */
val BsonRegExp.java: JavaBsonRegExp
    get() = JavaBsonRegExp(pattern, options.joinToString(""))

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonRegExp.kt: BsonRegExp
    get() = BsonRegExp(pattern, options.toSet())

//

internal typealias JavaBsonObjectId =
        org.bson.BsonObjectId

/**
 * Return the java version of this bson element.
 */
val BsonObjectId.java: JavaBsonObjectId
    get() = JavaBsonObjectId(value)

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonObjectId.kt: BsonObjectId
    get() = BsonObjectId(value)

//

internal typealias JavaBsonInt32 =
        org.bson.BsonInt32

/**
 * Return the java version of this bson element.
 */
val BsonInt32.java: JavaBsonInt32
    get() = JavaBsonInt32(value)

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonInt32.kt: BsonInt32
    get() = BsonInt32(value)

//

internal typealias JavaBsonInt64 =
        org.bson.BsonInt64

/**
 * Return the java version of this bson element.
 */
val BsonInt64.java: JavaBsonInt64
    get() = JavaBsonInt64(value)

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonInt64.kt: BsonInt64
    get() = BsonInt64(value)

//

internal typealias JavaBsonDouble =
        org.bson.BsonDouble

/**
 * Return the java version of this bson element.
 */
val BsonDouble.java: JavaBsonDouble
    get() = JavaBsonDouble(value)

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonDouble.kt: BsonDouble
    get() = BsonDouble(value)

//

internal typealias JavaBsonDecimal128 =
        org.bson.BsonDecimal128

/**
 * Return the java version of this bson element.
 */
val BsonDecimal128.java: JavaBsonDecimal128
    get() = JavaBsonDecimal128(value)

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonDecimal128.kt: BsonDecimal128
    get() = BsonDecimal128(value)

//

internal typealias JavaBsonBoolean =
        org.bson.BsonBoolean

/**
 * Return the java version of this bson element.
 */
val BsonBoolean.java: JavaBsonBoolean
    get() = when (this) {
        is BsonBoolean.True -> JavaBsonBoolean.TRUE
        is BsonBoolean.False -> JavaBsonBoolean.FALSE
    }

/**
 * Return the kotlin version of this bson element.
 */
val JavaBsonBoolean.kt: BsonBoolean
    get() = BsonBoolean(value)

//

internal typealias JavaBsonUndefined =
        org.bson.BsonUndefined

/**
 * Return the java version of this bson element.
 */
@Suppress("UnusedReceiverParameter")
val BsonUndefined.java: JavaBsonUndefined
    get() = JavaBsonUndefined()

/**
 * Return the kotlin version of this bson element.
 */
@Suppress("UnusedReceiverParameter")
val JavaBsonUndefined.kt: BsonUndefined
    get() = BsonUndefined

//

internal typealias JavaBsonNull =
        org.bson.BsonNull

/**
 * Return the java version of this bson element.
 */
@Suppress("UnusedReceiverParameter")
val BsonNull.java: JavaBsonNull
    get() = JavaBsonNull.VALUE

/**
 * Return the kotlin version of this bson element.
 */
@Suppress("UnusedReceiverParameter")
val JavaBsonNull.kt: BsonNull
    get() = BsonNull

/* ============= ------------------ ============= */
