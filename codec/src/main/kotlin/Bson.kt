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
package org.cufy.codec

import org.cufy.bson.*
import java.math.BigDecimal
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.experimental.ExperimentalTypeInference

/* ============= ------------------ ============= */

/**
 * A bson variant of [FieldCodec] enabling extra
 * features that can be achieved only when the
 * target output is known to be bson.
 *
 * This implements [MutableBsonMapField] to enable the following syntax:
 * ```kotlin
 * document {
 *      MyField by myValue
 * }
 * ```
 *
 * This interface will be useless after context
 * receivers is released for production.
 * This interface will be removed gradually after
 * context receivers is released for production.
 *
 * @param I the type of the decoded value.
 * @param O the type of the encoded value.
 * @author LSafer
 * @since 2.0.0
 */
interface BsonFieldCodec<I, O : BsonElement> : FieldCodec<I, O>, MutableBsonMapField<I> {
    override fun encode(value: I): BsonElement =
        encode(value, this)
}

// Constructor

/**
 * Create a new [BsonFieldCodec] with
 * the given [name] and backed by the given [codec].
 */
@ExperimentalCodecApi
fun <I, O : BsonElement> BsonFieldCodec(name: String, codec: Codec<I, O>): BsonFieldCodec<I, O> {
    return object : BsonFieldCodec<I, O>, Codec<I, O> by codec {
        override val name = name
    }
}

/**
 * Create a new field codec with the given [name]
 * and backed by the given [codec].
 */
@Suppress("FunctionName")
@OptIn(ExperimentalCodecApi::class)
fun <I, O : BsonElement> FieldCodec(name: String, codec: Codec<I, O>): BsonFieldCodec<I, O> {
    return BsonFieldCodec(name, codec)
}

/**
 * Create a new field codec with the given [name]
 * and backed by the codec returned from invoking
 * the given [block].
 */
@OptIn(ExperimentalTypeInference::class, ExperimentalCodecApi::class)
@OverloadResolutionByLambdaReturnType
@Suppress("FunctionName")
fun <I, O : BsonElement> FieldCodec(name: String, block: Codecs.() -> Codec<I, O>): BsonFieldCodec<I, O> {
    return BsonFieldCodec(name, block(Codecs))
}

/**
 * Create a new field codec with the given [name]
 * and backed by [this] codec.
 */
@CodecKeywordMarker
infix fun <I, O : BsonElement> Codec<I, O>.at(name: String): BsonFieldCodec<I, O> {
    return FieldCodec(name, this)
}

/**
 * Create a new field codec with the receiver name
 * and backed by the given [codec].
 */
@CodecKeywordMarker
infix fun <I, O : BsonElement> String.be(codec: Codec<I, O>): BsonFieldCodec<I, O> {
    return FieldCodec(this, codec)
}

/**
 * Create a new field codec with the receiver name
 * and backed by the codec from the given [block].
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@CodecKeywordMarker
infix fun <I, O : BsonElement> String.be(block: Codecs.() -> Codec<I, O>): BsonFieldCodec<I, O> {
    return FieldCodec(this, block)
}

/* ============= ------------------ ============= */

@OptIn(ExperimentalCodecApi::class)
@CodecKeywordMarker
infix fun <I, O : BsonElement> BsonFieldCodec<I, O>.catchIn(block: (Throwable) -> I): BsonFieldCodec<I, O> {
    val codec = this as Codec<I, O>
    return BsonFieldCodec(name, codec catchIn block)
}

@OptIn(ExperimentalCodecApi::class)
@CodecKeywordMarker
infix fun <I, O : BsonElement> BsonFieldCodec<I, O>.catchOut(block: (Throwable) -> O): BsonFieldCodec<I, O> {
    val codec = this as Codec<I, O>
    return BsonFieldCodec(name, codec catchOut block)
}

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
    val codec: Codec<I, O>
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
    val codec: Codec<I, O>
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
val <I, O : BsonElement> FieldCodec<I, O>.Array: FieldCodec<List<I>, BsonArray>
    get() = FieldCodec(name, (this as Codec<I, O>).Array)

/**
 * Obtain a codec for [List] and [BsonArray] that
 * uses this codec to encode/decode each
 * individual item.
 */
@OptIn(ExperimentalCodecApi::class)
val <I, O : BsonElement> BsonFieldCodec<I, O>.Array: FieldCodec<List<I>, BsonArray>
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

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
@Deprecated(
    "Codecs.String instead",
    ReplaceWith("Codecs.String", "org.cufy.codec.Codecs")
)
inline val `bson string` get() = BsonStringCodec

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
@Deprecated(
    "Codecs.String.Nullable instead",
    ReplaceWith("Codecs.String", "org.cufy.codec.Codecs")
)
inline val `bson nullable string` get() = BsonStringCodec.Nullable

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
@Deprecated(
    "Codecs.String.Array instead",
    ReplaceWith("Codecs.String", "org.cufy.codec.Codecs")
)
inline val `bson string array` get() = BsonStringCodec.Array

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.String get() = BsonStringCodec

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

/**
 * The codec for [Boolean] and [BsonBoolean].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.Boolean get() = BsonBooleanCodec

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

/**
 * The codec for [Int] and [BsonInt32].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.Int32 get() = BsonInt32Codec

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

/**
 * The codec for [Long] and [BsonInt64].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.Int64 get() = BsonInt64Codec

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

/**
 * The codec for [Double] and [BsonDouble].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.Double get() = BsonDoubleCodec

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

/**
 * The codec for [Decimal128] and [BsonDecimal128].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.Decimal128 get() = BsonDecimal128Codec

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

/**
 * The codec for [BigDecimal] and [BsonDecimal128].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.BigDecimal get() = BsonBigDecimalCodec

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

/**
 * The codec for [ObjectId] and [BsonObjectId].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.ObjectId get() = BsonObjectIdCodec

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
                else -> failure(CodecException(
                    "Cannot decode ${it::class}; expected either " +
                            BsonObjectId::class + " or " +
                            BsonString::class
                ))
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

/**
 * The codec for [Id] and [BsonObjectId] or [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Codecs.Id get() = BsonIdCodec

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
