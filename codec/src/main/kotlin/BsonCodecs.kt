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
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/* ============= ------------------ ============= */

/**
 * A codec that always decodes nullish values
 * to `null` and encodes `null` to [BsonNull] and
 * uses the given [codec] otherwise.
 *
 * Nullish values includes `null`, [BsonNull] and [BsonUndefined]
 *
 * @author LSafer
 * @since 2.0.0
 */
class BsonNullableCodec<I, O : BsonElement>(
    @Suppress("MemberVisibilityCanBePrivate")
    val codec: Codec<I, O>,
) : Codec<I?, BsonElement> {
    override fun encode(value: Any?) =
        when (value) {
            null -> success(BsonNull)
            else -> codec.encode(value)
        }

    override fun decode(value: Any?) =
        when (value) {
            null, BsonNull, BsonUndefined -> success(null)
            else -> codec.decode(value)
        }
}

/**
 * Obtain a codec that always decodes nullish
 * values to `null` and encodes `null`
 * to [BsonNull] and uses this codec otherwise.
 *
 * Nullish values includes `null`, [BsonNull] and [BsonUndefined]
 */
val <I, O : BsonElement> Codec<I, O>.Nullable: BsonNullableCodec<I, O>
    get() = BsonNullableCodec(this)

/**
 * Obtain a codec that always decodes nullish
 * values to `null` and encodes `null`
 * to [BsonNull] and uses this codec otherwise.
 *
 * Nullish values includes `null`, [BsonNull] and [BsonUndefined]
 */
val <I, O : BsonElement> FieldCodec<I, O>.Nullable: FieldCodec<I?, BsonElement>
    get() = FieldCodec(name, (this as Codec<I, O>).Nullable)

/**
 * Obtain a codec that always decodes nullish
 * values to `null` and encodes `null`
 * to [BsonNull] and uses this codec otherwise.
 *
 * Nullish values includes `null`, [BsonNull] and [BsonUndefined]
 */
@OptIn(ExperimentalCodecApi::class)
val <I, O : BsonElement> BsonFieldCodec<I, O>.Nullable: BsonFieldCodec<I?, BsonElement>
    get() = BsonFieldCodec(name, (this as Codec<I, O>).Nullable)

/**
 * Decode [this] value to [I] using the given [codec].
 *
 * @receiver the value to decode.
 * @param codec the codec to be used.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@JvmName("decodeInfixNullish")
@CodecKeywordMarker
infix fun <I, O : BsonElement> O?.decode(codec: BsonNullableCodec<I, O>): I? {
    return decode(this, codec)
}

/**
 * Decode [this] value to [I] using the given [codec].
 *
 * @receiver the value to decode.
 * @param codec the codec to be used.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@JvmName("decodeInfixNullable")
@CodecKeywordMarker
infix fun <I, O : BsonElement> O.decode(codec: BsonNullableCodec<I, O>): I? {
    return decode(this, codec)
}

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode.
 * @param codec the codec to be used.
 * @return the decoding result.
 * @since 2.0.0
 */
@JvmName("tryDecodeNullish")
@CodecMarker
fun <I, O : BsonElement> tryDecode(value: O?, codec: BsonNullableCodec<I, O>): Result<I?> {
    return tryDecodeAny(value, codec)
}

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode.
 * @param codec the codec to be used.
 * @return the decoding result.
 * @since 2.0.0
 */
@JvmName("tryDecodeNullable")
@CodecMarker
fun <I, O : BsonElement> tryDecode(value: O, codec: BsonNullableCodec<I, O>): Result<I?> {
    return tryDecodeAny(value, codec)
}

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode.
 * @param codec the codec to be used.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@JvmName("decodeNullish")
@CodecMarker
fun <I, O : BsonElement> decode(value: O?, codec: BsonNullableCodec<I, O>): I? {
    return decodeAny(value, codec)
}

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode.
 * @param codec the codec to be used.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@JvmName("decodeNullable")
@CodecMarker
fun <I, O : BsonElement> decode(value: O, codec: BsonNullableCodec<I, O>): I? {
    return decodeAny(value, codec)
}

/* ============= ------------------ ============= */

/**
 * A codec for [List] and [BsonArray] that uses
 * the given [codec] to encode/decode each
 * individual item.
 */
class BsonArrayCodec<I, O : BsonElement>(
    @Suppress("MemberVisibilityCanBePrivate")
    val codec: Codec<I, O>,
) : Codec<List<I>, BsonArray> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: List<*> ->
            success(BsonArray {
                it.mapTo(this) {
                    encodeAny(it, codec)
                }
            })
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonArray ->
            success(it.map {
                decodeAny(it, codec)
            })
        }
}

/**
 * Obtain a codec for [List] and [BsonArray] that
 * uses this codec to encode/decode each
 * individual item.
 */
val <I, O : BsonElement> Codec<I, O>.Array: BsonArrayCodec<I, O>
    get() = BsonArrayCodec(this)

/**
 * Obtain a codec for [List] and [BsonArray] that
 * uses this codec to encode/decode each
 * individual item.
 */
val <I, O : BsonElement> FieldCodec<I, O>.Array: BsonFieldCodec<List<I>, BsonArray>
    get() = FieldCodec(name, (this as Codec<I, O>).Array)

/**
 * Obtain a codec for [List] and [BsonArray] that
 * uses this codec to encode/decode each
 * individual item.
 */
@OptIn(ExperimentalCodecApi::class)
val <I, O : BsonElement> BsonFieldCodec<I, O>.Array: BsonFieldCodec<List<I>, BsonArray>
    get() = BsonFieldCodec(name, (this as Codec<I, O>).Array)

/* ============= ------------------ ============= */

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
object BsonStringCodec : Codec<String, BsonString> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: String ->
            success(BsonString(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonString ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Boolean] and [BsonBoolean].
 *
 * @since 2.0.0
 */
object BsonBooleanCodec : Codec<Boolean, BsonBoolean> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Boolean ->
            success(BsonBoolean(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonBoolean ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Int] and [BsonInt32].
 *
 * @since 2.0.0
 */
object BsonInt32Codec : Codec<Int, BsonInt32> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Int ->
            success(BsonInt32(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonInt32 ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Long] and [BsonInt64].
 *
 * @since 2.0.0
 */
object BsonInt64Codec : Codec<Long, BsonInt64> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Long ->
            success(BsonInt64(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonInt64 ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Double] and [BsonDouble].
 *
 * @since 2.0.0
 */
object BsonDoubleCodec : Codec<Double, BsonDouble> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Double ->
            success(BsonDouble(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonDouble ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Decimal128] and [BsonDecimal128].
 *
 * @since 2.0.0
 */
object BsonDecimal128Codec : Codec<Decimal128, BsonDecimal128> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Decimal128 ->
            success(BsonDecimal128(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonDecimal128 ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [BigDecimal] and [BsonDecimal128].
 *
 * @since 2.0.0
 */
object BsonBigDecimalCodec : Codec<BigDecimal, BsonDecimal128> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: BigDecimal ->
            success(BsonDecimal128(Decimal128(it)))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonDecimal128 ->
            success(it.value.bigDecimalValue())
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Long] and [BsonDateTime].
 *
 * @since 2.0.0
 */
object BsonDateTimeCodec : Codec<Long, BsonDateTime> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Long ->
            success(BsonDateTime(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonDateTime ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Date] and [BsonDateTime].
 *
 * @since 2.0.0
 */
object BsonDateCodec : Codec<Date, BsonDateTime> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Date ->
            success(BsonDateTime(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonDateTime ->
            success(Date(it.value))
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Instant] and [BsonDateTime].
 *
 * @since 2.0.0
 */
object BsonInstantCodec : Codec<Instant, BsonDateTime> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Instant ->
            success(BsonDateTime(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonDateTime ->
            success(Instant.fromEpochMilliseconds(it.value))
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [ObjectId] and [BsonObjectId].
 *
 * @since 2.0.0
 */
object BsonObjectIdCodec : Codec<ObjectId, BsonObjectId> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: ObjectId ->
            success(BsonObjectId(it))
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonObjectId ->
            success(it.value)
        }
}

/* ============= ------------------ ============= */

/**
 * The codec for [Id] and [BsonObjectId] or [BsonString].
 *
 * @since 2.0.0
 */
object BsonIdCodec : Codec<Id<*>, BsonElement> {
    override fun encode(value: Any?) =
        tryInlineCodec(value) { it: Id<*> ->
            success(it.bson)
        }

    override fun decode(value: Any?) =
        tryInlineCodec(value) { it: BsonElement ->
            when (it) {
                is BsonObjectId -> success(Id<Any>(it.value))
                is BsonString -> success(Id(it.value))
                else -> failure(
                    CodecException(
                        "Cannot decode ${it::class}; expected either " +
                                BsonObjectId::class + " or " +
                                BsonString::class
                    )
                )
            }
        }

    /**
     * Return this codec casted to [T].
     */
    operator fun <T> invoke(): Codec<Id<T>, BsonElement> {
        @Suppress("UNCHECKED_CAST")
        return this as Codec<Id<T>, BsonElement>
    }
}

/* ============= ------------------ ============= */

/**
 * A codec simplifying enum encoding.
 */
class EnumCodec<I, O>(private val pairs: List<Pair<I, O>>) : Codec<I, O> {
    constructor(vararg pairs: Pair<I, O>) : this(pairs.asList())

    override fun encode(value: Any?): Result<O> {
        return pairs.firstOrNull { it.first == value }.let {
            when (it) {
                null -> failure(CodecException("Enum mismatch: $value"))
                else -> success(it.second)
            }
        }
    }

    override fun decode(value: Any?): Result<I> {
        return pairs.firstOrNull { it.second == value }.let {
            when (it) {
                null -> failure(CodecException("Enum mismatch: $value"))
                else -> success(it.first)
            }
        }
    }
}

/* ============= ------------------ ============= */

inline operator fun <I> Codec<I, BsonDocument>.invoke(block: BsonDocumentBlock): I {
    return decode(BsonDocument(block), this)
}

/* ============= ------------------ ============= */
