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
 * A type-safe container for a BSON document.
 *
 * @see org.bson.BsonDocument
 * @since 2.0.0
 */
typealias BsonDocument = org.bson.BsonDocument

/**
 * A type-safe representation of the BSON array type.
 *
 * @see org.bson.BsonArray
 * @since 2.0.0
 */
typealias BsonArray = org.bson.BsonArray

/**
 * A representation of the BSON String type.
 *
 * @see org.bson.BsonString
 * @since 2.0.0
 */
typealias BsonString = org.bson.BsonString

/**
 * A representation of the BSON ObjectId type.
 *
 * @see org.bson.BsonObjectId
 * @since 2.0.0
 */
typealias BsonObjectId = org.bson.BsonObjectId

/**
 * A representation of the BSON Int32 type.
 *
 * @see org.bson.BsonInt32
 * @since 2.0.0
 */
typealias BsonInt32 = org.bson.BsonInt32

/**
 * A representation of the BSON Int64 type.
 *
 * @see org.bson.BsonInt64
 * @since 2.0.0
 */
typealias BsonInt64 = org.bson.BsonInt64

/**
 * Represents the value associated with the BSON Undefined type.
 *
 * @see org.bson.BsonUndefined
 * @since 2.0.0
 */
typealias BsonUndefined = org.bson.BsonUndefined

/**
 * A representation of the BSON Null type.
 *
 * @see org.bson.BsonNull
 * @since 2.0.0
 */
typealias BsonNull = org.bson.BsonNull

/**
 * A representation of the BSON Boolean type.
 *
 * @see org.bson.BsonBoolean
 * @since 2.0.0
 */
typealias BsonBoolean = org.bson.BsonBoolean

/**
 * A representation of the BSON Decimal128 type.
 *
 * @see org.bson.BsonDecimal128
 * @since 2.0.0
 */
typealias BsonDecimal128 = org.bson.BsonDecimal128

/**
 * A representation of the BSON Double type.
 *
 * @see org.bson.BsonDouble
 * @since 2.0.0
 */
typealias BsonDouble = org.bson.BsonDouble

/**
 * Base class for any BSON type.
 *
 * @see org.bson.BsonValue
 * @since 2.0.0
 */
typealias BsonValue = org.bson.BsonValue

/**
 * An interface for types that are able to render themselves into a [BsonDocument].
 *
 * @see org.bson.conversions.Bson
 * @since 2.0.0
 */
typealias Bson = org.bson.conversions.Bson

/**
 * A binary integer decimal representation of a 128-bit decimal value, supporting 34 decimal digits of significand and an exponent range
 * of -6143 to +6144.
 *
 * @see org.bson.types.Decimal128
 * @since 2.0.0
 */
typealias Decimal128 = org.bson.types.Decimal128

/**
 * A globally unique identifier for objects.
 *
 * @see org.bson.types.ObjectId
 * @since 2.0.0
 */
typealias ObjectId = org.bson.types.ObjectId

/**
 * Enumeration of all the BSON types currently supported.
 *
 * @see org.bson.BsonType
 * @since 2.0.0
 */
typealias BsonType = org.bson.BsonType

/**
 * An immutable BSON document that is represented using only the raw bytes.
 *
 * @see org.bson.RawBsonDocument
 * @since 2.0.0
 */
internal typealias RawBsonDocument = org.bson.RawBsonDocument

/**
 * An immutable BSON array that is represented using only the raw bytes.
 *
 * @see org.bson.RawBsonArray
 * @since 2.0.0
 */
internal typealias RawBsonArray = org.bson.RawBsonArray

/**
 * A mapping from a name to a BsonValue.
 *
 * @see org.bson.BsonElement
 * @since 2.0.0
 */
internal typealias BsonElement = org.bson.BsonElement

//

/**
 * The global instance of bson null.
 */
@BsonKeywordMarker
val bnull: BsonNull = BsonNull.VALUE

/**
 * A global instance of bson undefined.
 */
@BsonKeywordMarker
val bundefined: BsonUndefined = BsonUndefined()

/**
 * The global instance of bson true.
 */
@BsonKeywordMarker
val btrue: BsonBoolean = BsonBoolean.TRUE

/**
 * The global instance of bson false.
 */
@BsonKeywordMarker
val bfalse: BsonBoolean = BsonBoolean.FALSE

/**
 * A global instance of an empty immutable array.
 */
@BsonKeywordMarker
val barray: BsonArray = RawBsonArray.parse("[]")

/**
 * A global instance of an empty immutable object.
 */
@BsonKeywordMarker
val bdocument: BsonDocument = RawBsonDocument.parse("{}")

/**
 * Return a [BsonDouble] with the given [value].
 */
@BsonKeywordMarker
fun bdouble(value: Double): BsonDouble = BsonDouble(value)

/**
 * Return a [BsonString] with the given [value].
 */
@BsonKeywordMarker
fun bstring(value: String): BsonString = BsonString(value)

/**
 * Return a [BsonInt32] with the given [value].
 */
@BsonKeywordMarker
fun bint32(value: Int): BsonInt32 = BsonInt32(value)

/**
 * Return a [BsonInt64] with the given [value].
 */
@BsonKeywordMarker
fun bint64(value: Long): BsonInt64 = BsonInt64(value)

//

/**
 * A BSON double.
 */
val BsonDoubleType = BsonType.DOUBLE

/**
 * A BSON string.
 */
val BsonStringType = BsonType.STRING

/**
 * A BSON document.
 */
val BsonDocumentType = BsonType.DOCUMENT

/**
 * A BSON array.
 */
val BsonArrayType = BsonType.ARRAY

/**
 * A BSON undefined value.
 */
val BsonUndefinedType = BsonType.UNDEFINED

/**
 * A BSON ObjectId.
 */
val BsonObjectIdType = BsonType.OBJECT_ID

/**
 * A BSON bool.
 */
val BsonBooleanType = BsonType.BOOLEAN

/**
 * A BSON null value.
 */
val BsonNullType = BsonType.NULL

/**
 * A BSON 32-bit integer.
 */
val BsonInt32Type = BsonType.INT32

/**
 * A BSON 64-bit integer.
 */
val BsonInt64Type = BsonType.INT64

/**
 * A BSON Decimal128.
 *
 * @since 3.4
 */
val BsonDecimal128Type = BsonType.DECIMAL128
