/*
 *	Copyright 2023 cufy.org and meemer.com
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
package org.cufy.codec

import kotlinx.datetime.Instant
import org.cufy.bson.*
import java.math.BigDecimal
import java.util.*

/**
 * Marker class for primary bson codecs.
 */
object Codecs {
    /**
     * The codec for [String] and [BsonString].
     *
     * @since 2.0.0
     */
    val String = BsonStringCodec

    /**
     * The codec for [Boolean] and [BsonBoolean].
     *
     * @since 2.0.0
     */
    val Boolean = BsonBooleanCodec

    /**
     * The codec for [Int] and [BsonInt32].
     *
     * @since 2.0.0
     */
    val Int32 = BsonInt32Codec

    /**
     * The codec for [Long] and [BsonInt64].
     *
     * @since 2.0.0
     */
    val Int64 = BsonInt64Codec

    /**
     * The codec for [Double] and [BsonDouble].
     *
     * @since 2.0.0
     */
    val Double = BsonDoubleCodec

    /**
     * The codec for [Decimal128] and [BsonDecimal128].
     *
     * @since 2.0.0
     */
    val Decimal128 = BsonDecimal128Codec

    /**
     * The codec for [BigDecimal] and [BsonDecimal128].
     *
     * @since 2.0.0
     */
    val BigDecimal = BsonBigDecimalCodec

    /**
     * The codec for [Long] and [BsonDateTime].
     *
     * @since 2.0.0
     */
    val DateTime = BsonDateTimeCodec

    /**
     * The codec for [Date] and [BsonDateTime].
     *
     * @since 2.0.0
     */
    val Date = BsonDateCodec

    /**
     * The codec for [Instant] and [BsonDateTime].
     *
     * @since 2.0.0
     */
    val Instant = BsonInstantCodec

    /**
     * The codec for [ObjectId] and [BsonObjectId].
     *
     * @since 2.0.0
     */
    val ObjectId = BsonObjectIdCodec

    /**
     * The codec for [Id] and [BsonObjectId] or [BsonString].
     *
     * @since 2.0.0
     */
    val Id = BsonIdCodec
}
