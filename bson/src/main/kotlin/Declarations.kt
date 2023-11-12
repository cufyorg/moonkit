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

/* ============= ------------------ ============= */

/**
 * Enumeration of the supported [BsonElement] types.
 *
 * @see org.bson.BsonType
 * @since 2.0.0
 */
enum class BsonType(val value: Int) {
    //    EndOfDocument(0x00),

    /**
     * The type for [BsonDouble].
     *
     * @see org.bson.BsonType.DOUBLE
     * @since 2.0.0
     */
    Double(0x01),

    /**
     * The type for [BsonString].
     *
     * @see org.bson.BsonType.STRING
     * @since 2.0.0
     */
    String(0x02),

    /**
     * The type for [BsonDocument].
     *
     * @see org.bson.BsonType.DOCUMENT
     * @since 2.0.0
     */
    Document(0x03),

    /**
     * The type for [BsonArray].
     *
     * @see org.bson.BsonType.ARRAY
     * @since 2.0.0
     */
    Array(0x04),

    //    Binary(0x05),

    /**
     * The type for [BsonUndefined].
     *
     * @see org.bson.BsonType.UNDEFINED
     * @since 2.0.0
     */
    Undefined(0x06),

    /**
     * The type for [BsonObjectId].
     *
     * @see org.bson.BsonType.OBJECT_ID
     * @since 2.0.0
     */
    ObjectId(0x07),

    /**
     * The type for [BsonBoolean].
     *
     * @see org.bson.BsonType.BOOLEAN
     * @since 2.0.0
     */
    Boolean(0x08),

    /**
     * The type for [BsonDateTime].
     *
     * @see org.bson.BsonType.DATE_TIME
     * @since 2.0.0
     */
    DateTime(0x09),

    /**
     * The type for [BsonNull].
     *
     * @see org.bson.BsonType.NULL
     * @since 2.0.0
     */
    Null(0x0a),

    /**
     * The type for [BsonRegExp].
     *
     * @see org.bson.BsonType.REGULAR_EXPRESSION
     * @since 2.0.0
     */
    RegExp(0x0b),

    //    DbPointer(0x0c),
    //    Javascript(0x0d),
    //    Symbol(0x0e),
    //    JavascriptWithScope(0x0f),

    /**
     * The type for [BsonInt32].
     *
     * @see org.bson.BsonType.INT32
     * @since 2.0.0
     */
    Int32(0x10),

    //    Timestamp(0x11),

    /**
     * The type for [BsonInt64].
     *
     * @see org.bson.BsonType.INT64
     * @since 2.0.0
     */
    Int64(0x12),

    /**
     * The type for [BsonDecimal128].
     *
     * @see org.bson.BsonType.DECIMAL128
     * @since 2.0.0
     */
    Decimal128(0x13),

    //    MinKey(0xff),
    //    MaxKey(0x7f)
}

/**
 * Base class for any BSON type.
 *
 * This class is meant to be immutable.
 * If a way to mutate an instance of this class is
 * found. The behaviour of the instance is undefined
 * and that way will not be guaranteed to even work
 * in different versions.
 *
 * @see org.bson.BsonValue
 * @since 2.0.0
 */
sealed interface BsonElement {
    /**
     * Return the type of this element.
     *
     * @since 2.0.0
     */
    val type: BsonType
}

/* ============= ------------------ ============= */
