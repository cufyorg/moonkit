/*
 *	Copyright 2023 cufy.org
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

import org.cufy.bson.BsonElement
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.reflect.KProperty

/* ============= ------------------ ============= */

/**
 * A codec is an encoder/decoder from/to [I] and [O].
 *
 * @param I the type of the decoded value.
 * @param O the type of the encoded value.
 * @author LSafer
 * @since 2.0.0
 */
interface Codec<I, O> {
    /**
     * Encode the given [value] to type [O].
     *
     * @param value the value to be encoded.
     * @return the encoded value.
     * @since 2.0.0
     */
    @AdvancedCodecApi("Use `encode(I, Codec)` instead")
    fun encode(value: Any?): Result<O>

    /**
     * Decode the given [value] value into [I].
     *
     * @param value the value to be decoded.
     * @return the decoded value.
     * @since 2.0.0
     */
    @AdvancedCodecApi("Use `decode(I, Codec)` instead")
    fun decode(value: Any?): Result<I>
}

// Cast

/**
 * Encode [this] value to [O] using the given [codec].
 *
 * @receiver the value to encode.
 * @param codec the codec to be used.
 * @return the encoded value.
 * @throws CodecException if encoding failed.
 * @since 2.0.0
 */
@CodecKeywordMarker
infix fun <I, O> I.encode(codec: Codec<I, O>): O {
    return encode(this, codec)
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
@CodecKeywordMarker
infix fun <I, O> O.decode(codec: Codec<I, O>): I {
    return decode(this, codec)
}

/**
 * Get the value of the field with the name of the
 * given [codec] and encode it using the given [codec].
 */
operator fun <I, O> Map<String, I>.get(codec: FieldCodec<I, O>): O {
    return encodeAny(this[codec.name], codec)
}

/**
 * Get the value of the field with the name of the
 * given [codec] and decode it using the given [codec].
 */
operator fun <I, O> Map<String, O>.get(codec: FieldCodec<I, O>): I {
    return decodeAny(this[codec.name], codec)
}

// Encode Any

/**
 * Encode the given [value] to [O] using the given [codec].
 *
 * @param value the value to encode. (type checked at runtime)
 * @param codec the codec to be used.
 * @return the encoding result.
 * @since 2.0.0
 */
@OptIn(AdvancedCodecApi::class)
@CodecMarker
fun <I, O> tryEncodeAny(value: Any?, codec: Codec<I, O>): Result<O> {
    return codec.encode(value)
}

/**
 * Encode the given [value] to [O] using the codec returned by [block].
 *
 * @param value the value to encode. (type checked at runtime)
 * @param block a function invoked immediately to obtain the codec.
 * @return the encoding result.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> tryEncodeAny(value: Any?, block: Codecs.() -> Codec<I, O>): Result<O> {
    return tryEncodeAny(value, block(Codecs))
}

/**
 * Encode the given [value] to [O] using the given [codec].
 *
 * @param value the value to encode. (type checked at runtime)
 * @param codec the codec to be used.
 * @return the encoded value.
 * @throws CodecException if encoding failed.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> encodeAny(value: Any?, codec: Codec<I, O>): O {
    return tryEncodeAny(value, codec).getOrElse {
        if (it is CodecException) throw it
        throw CodecException(cause = it)
    }
}

/**
 * Encode the given [value] to [O] using the given [Codec].
 *
 * @param value the value to encode. (type checked at runtime)
 * @param block a function invoked immediately to obtain the codec.
 * @return the encoded value.
 * @throws CodecException if encoding failed.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> encodeAny(value: Any?, block: Codecs.() -> Codec<I, O>): O {
    return encodeAny(value, block(Codecs))
}

// Encode

/**
 * Encode the given [value] to [O] using the given [codec].
 *
 * @param value the value to encode.
 * @param codec the codec to be used.
 * @return the encoding result.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> tryEncode(value: I, codec: Codec<I, O>): Result<O> {
    return tryEncodeAny(value, codec)
}

/**
 * Encode the given [value] to [O] using the codec returned by [block].
 *
 * @param value the value to encode.
 * @param block a function invoked immediately to obtain the codec.
 * @return the encoding result.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> tryEncode(value: I, block: Codecs.() -> Codec<I, O>): Result<O> {
    return tryEncodeAny(value, block(Codecs))
}

/**
 * Encode the given [value] to [O] using the given [codec].
 *
 * @param value the value to encode.
 * @param codec the codec to be used.
 * @return the encoded value.
 * @throws CodecException if encoding failed.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> encode(value: I, codec: Codec<I, O>): O {
    return encodeAny(value, codec)
}

/**
 * Encode the given [value] to [O] using the codec returned by [block].
 *
 * @param value the value to encode.
 * @param block a function invoked immediately to obtain the codec.
 * @return the encoded value.
 * @throws CodecException if encoding failed.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> encode(value: I, block: Codecs.() -> Codec<I, O>): O {
    return encodeAny(value, block(Codecs))
}

// Decode Any

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode. (type checked at runtime)
 * @param codec the codec to be used.
 * @return the decoding result.
 * @since 2.0.0
 */
@OptIn(AdvancedCodecApi::class)
@CodecMarker
fun <I, O> tryDecodeAny(value: Any?, codec: Codec<I, O>): Result<I> {
    return codec.decode(value)
}

/**
 * Decode the given [value] to [O] using the codec returned by [block].
 *
 * @param value the value to decode. (type checked at runtime)
 * @param block a function invoked immediately to obtain the codec.
 * @return the decoding result.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> tryDecodeAny(value: Any?, block: Codecs.() -> Codec<I, O>): Result<I> {
    return tryDecodeAny(value, block(Codecs))
}

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode. (type checked at runtime)
 * @param codec the codec to be used.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> decodeAny(value: Any?, codec: Codec<I, O>): I {
    return tryDecodeAny(value, codec).getOrElse {
        if (it is CodecException) throw it
        throw CodecException(cause = it)
    }
}

/**
 * Decode the given [value] to [O] using the given [Codec].
 *
 * @param value the value to decode. (type checked at runtime)
 * @param block a function invoked immediately to obtain the codec.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> decodeAny(value: Any?, block: Codecs.() -> Codec<I, O>): I {
    return decodeAny(value, block(Codecs))
}

// Decode

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode.
 * @param codec the codec to be used.
 * @return the decoding result.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> tryDecode(value: O, codec: Codec<I, O>): Result<I> {
    return tryDecodeAny(value, codec)
}

/**
 * Decode the given [value] to [O] using the codec returned by [block].
 *
 * @param value the value to decode.
 * @param block a function invoked immediately to obtain the codec.
 * @return the decoding result.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> tryDecode(value: O, block: Codecs.() -> Codec<I, O>): Result<I> {
    return tryDecodeAny(value, block(Codecs))
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
@CodecMarker
fun <I, O> decode(value: O, codec: Codec<I, O>): I {
    return decodeAny(value, codec)
}

/**
 * Decode the given [value] to [O] using the codec returned by [block].
 *
 * @param value the value to decode.
 * @param block a function invoked immediately to obtain the codec.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@CodecMarker
fun <I, O> decode(value: O, block: Codecs.() -> Codec<I, O>): I {
    return decodeAny(value, block(Codecs))
}

// Inline Codec Any

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will be fall through
 * this function uncaught.
 *
 * @param value the value to encode.
 * @param block the codec block to be used.
 * @return the encoding/decoding result.
 * @since 2.0.0
 */
@CodecMarker
inline fun <T> tryInlineCodecAny(value: Any?, block: (Any?) -> Result<T>): Result<T> {
    return block(value)
}

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will be caught,
 * wrapped with [CodecException] then returned as
 * a failure.
 *
 * @param value the value to encode.
 * @param block the codec block to be used.
 * @return the encoding/decoding result.
 * @since 2.0.0
 */
@CodecMarker
inline fun <T> tryInlineCodecAnyCatching(value: Any?, block: (Any?) -> T): Result<T> {
    return try {
        success(block(value))
    } catch (error: CodecException) {
        failure(error)
    } catch (error: Throwable) {
        failure(CodecException(cause = error))
    }
}

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will fall through
 * this function uncaught.
 *
 * @param value the value to encode.
 * @param block the codec block to be used.
 * @return the encoded/decoded value.
 * @throws CodecException if encoding/decoding failed.
 * @since 2.0.0
 */
@CodecMarker
inline fun <T> inlineCodecAny(value: Any?, block: (Any?) -> Result<T>): T {
    return block(value).getOrElse {
        if (it is CodecException) throw it
        throw CodecException(cause = it)
    }
}

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will rethrown as
 * [CodecException].
 *
 * @param value the value to encode.
 * @param block the codec block to be used.
 * @return the encoding result.
 * @since 2.0.0
 */
@CodecMarker
inline fun <T> inlineCodecAnyCatching(value: Any?, block: (Any?) -> T): T {
    return try {
        block(value)
    } catch (error: CodecException) {
        throw error
    } catch (error: Throwable) {
        throw CodecException(cause = error)
    }
}

// Inline Codec

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will fall through
 * this function uncaught.
 *
 * The given [value] will be safely casted to type [T].
 * A failure with a [CodecException] will be returned
 * when casting failed.
 *
 * @param value the value to encode. (type checked at runtime)
 * @param block the codec block to be used.
 * @return the encoding/decoding result.
 * @since 2.0.0
 */
@CodecMarker
inline fun <reified T, U> tryInlineCodec(value: Any?, block: (T) -> Result<U>): Result<U> {
    return when (value) {
        is T -> block(value)
        else -> failure(CodecException(
            "Cannot encode/decode ${value?.let { it::class }}; expected ${T::class}"
        ))
    }
}

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will be caught,
 * wrapped with [CodecException] then returned as
 * a failure.
 *
 * The given [value] will be safely casted to type [T].
 * A failure with a [CodecException] will be returned
 * when casting failed.
 *
 * @param value the value to encode. (type checked at runtime)
 * @param block the codec block to be used.
 * @return the encoding/decoding result.
 * @since 2.0.0
 */
@CodecMarker
inline fun <reified T, U> tryInlineCodecCatching(value: Any?, block: (T) -> U): Result<U> {
    return tryInlineCodec<T, U>(value) {
        try {
            success(block(it))
        } catch (error: CodecException) {
            failure(error)
        } catch (error: Throwable) {
            failure(CodecException(cause = error))
        }
    }
}

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will fall through
 * this function uncaught.
 *
 * The given [value] will be safely casted to type [T].
 * A [CodecException] will be thrown when casting failed.
 *
 * @param value the value to encode. (type checked at runtime)
 * @param block the codec block to be used.
 * @return the encoded/decoded value.
 * @throws CodecException if encoding/decoding failed.
 * @since 2.0.0
 */
@CodecMarker
inline fun <reified T, U> inlineCodec(value: Any?, block: (T) -> Result<U>): U {
    return tryInlineCodec(value, block).getOrElse {
        if (it is CodecException) throw it
        throw CodecException(cause = it)
    }
}

/**
 * Invoke the given codec [block] on the given [value].
 *
 * Any error thrown by [block] will be rethrown
 * as a [CodecException].
 *
 * The given [value] will be safely casted to type [T].
 * A [CodecException] will be thrown when casting failed.
 *
 * @param value the value to encode. (type checked at runtime)
 * @param block the codec block to be used.
 * @return the encoded/decoded value.
 * @throws CodecException if encoding/decoding failed.
 * @since 2.0.0
 */
@CodecMarker
inline fun <reified T, U> inlineCodecCatching(value: Any?, block: (T) -> U): U {
    // will always wrap errors as CodecException
    return tryInlineCodecCatching(value, block).getOrThrow()
}

/* ============= ------------------ ============= */

/**
 * A builder building a basic [Codec] implementation.
 *
 */
interface CodecBuilder<I, O> {
    /**
     * The encoding block.
     */
    @AdvancedCodecApi("Use `encode()` instead")
    var encodeBlock: (Any?) -> Result<O>

    /**
     * The decoding block.
     */
    @AdvancedCodecApi("Use `decode()` instead")
    var decodeBlock: (Any?) -> Result<I>

    /**
     * Build the codec.
     *
     * @since 2.0.0
     */
    fun build(): Codec<I, O>
}

// Constructor

/**
 * Obtain a new [CodecBuilder] instance.
 */
@OptIn(AdvancedCodecApi::class)
fun <I, O> CodecBuilder(): CodecBuilder<I, O> {
    return object : CodecBuilder<I, O> {
        override lateinit var encodeBlock: (Any?) -> Result<O>
        override lateinit var decodeBlock: (Any?) -> Result<I>

        override fun build(): Codec<I, O> {
            if (!::encodeBlock.isInitialized)
                error("encodeBlock is required but was not set.")
            if (!::decodeBlock.isInitialized)
                error("decodeBlock is required but was not set.")

            val localEncodeBlock = encodeBlock
            val localDecodeBlock = decodeBlock
            return object : Codec<I, O> {
                override fun encode(value: Any?) = localEncodeBlock(value)
                override fun decode(value: Any?) = localDecodeBlock(value)
            }
        }
    }
}

/**
 * Create a new codec configured using the given [block].
 */
fun <I, O> Codec(block: CodecBuilder<I, O>.() -> Unit): Codec<I, O> {
    val builder = CodecBuilder<I, O>()
    builder.apply(block)
    return builder.build()
}

// Encode-Any

/**
 * Set the encode block to be the given [block].
 *
 * @see tryInlineCodecAny
 */
@OptIn(AdvancedCodecApi::class)
@CodecKeywordMarker
fun <I, O> CodecBuilder<I, O>.encodeAny(block: (Any?) -> Result<O>) {
    encodeBlock = block
}

/**
 * Set the encode block to be the given [block].
 *
 * Any error thrown by [block] will be caught,
 * wrapped with [CodecException] then returned as
 * a failure.
 *
 * @see tryInlineCodecAnyCatching
 */
@CodecKeywordMarker
fun <I, O> CodecBuilder<I, O>.encodeAnyCatching(block: (Any?) -> O) {
    encodeAny { tryInlineCodecAnyCatching(it, block) }
}

// Encode

/**
 * Set the encode block to be the given [block].
 *
 * Any error thrown by [block] will fall through
 * uncaught.
 *
 * The value will be safely casted to type [I].
 * A failure with a [CodecException] will be returned
 * when casting failed.
 *
 * @see tryInlineCodec
 */
@CodecKeywordMarker
inline fun <reified I, O> CodecBuilder<I, O>.encode(crossinline block: (I) -> Result<O>) {
    encodeAny { tryInlineCodec(it, block) }
}

/**
 * Set the encode block to be the given [block].
 *
 * Any error thrown by [block] will be caught,
 * wrapped with [CodecException] then returned as
 * a failure.
 *
 * The value will be safely casted to type [I].
 * A failure with a [CodecException] will be returned
 * when casting failed.
 *
 * @see tryInlineCodecCatching
 */
@CodecKeywordMarker
inline fun <reified I, O> CodecBuilder<I, O>.encodeCatching(crossinline block: (I) -> O) {
    encodeAny { tryInlineCodecCatching(it, block) }
}

// Decode-Any

/**
 * Set the decode block to be the given [block].
 *
 * @see tryInlineCodecAny
 */
@OptIn(AdvancedCodecApi::class)
@CodecKeywordMarker
fun <I, O> CodecBuilder<I, O>.decodeAny(block: (Any?) -> Result<I>) {
    decodeBlock = block
}

/**
 * Set the decode block to be the given [block].
 *
 * Any error thrown by [block] will be caught,
 * wrapped with [CodecException] then returned as
 * a failure.
 *
 * @see tryInlineCodecAnyCatching
 */
@CodecKeywordMarker
fun <I, O> CodecBuilder<I, O>.decodeAnyCatching(block: (Any?) -> I) {
    decodeAny { tryInlineCodecAnyCatching(it, block) }
}

// Decode

/**
 * Set the decode block to be the given [block].
 *
 * Any error thrown by [block] will fall through
 * uncaught.
 *
 * The value will be safely casted to type [I].
 * A failure with a [CodecException] will be returned
 * when casting failed.
 *
 * @see tryInlineCodec
 */
@CodecKeywordMarker
inline fun <I, reified O> CodecBuilder<I, O>.decode(crossinline block: (O) -> Result<I>) {
    decodeAny { tryInlineCodec(it, block) }
}

/**
 * Set the decode block to be the given [block].
 *
 * Any error thrown by [block] will be caught,
 * wrapped with [CodecException] then returned as
 * a failure.
 *
 * The value will be safely casted to type [I].
 * A failure with a [CodecException] will be returned
 * when casting failed.
 *
 * @see tryInlineCodecCatching
 */
@CodecKeywordMarker
inline fun <I, reified O> CodecBuilder<I, O>.decodeCatching(crossinline block: (O) -> I) {
    decodeAny { tryInlineCodecCatching(it, block) }
}

/* ============= ------------------ ============= */

/**
 * A codec specifically for the value of some field.
 * It stores two things, the name of the field, and
 * how to encode and decode its values.
 *
 * @param I the type of the decoded value.
 * @param O the type of the encoded value.
 * @author LSafer
 * @since 2.0.0
 */
interface FieldCodec<I, O> : Codec<I, O> {
    /**
     * The name of the field.
     */
    val name: String
}

// Constructor

/**
 * Create a new [FieldCodec] with the given [name]
 * and backed by the given [codec].
 */
fun <I, O> FieldCodec(name: String, codec: Codec<I, O>): FieldCodec<I, O> {
    return object : FieldCodec<I, O>, Codec<I, O> by codec {
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
fun <I, O> FieldCodec(name: String, block: Codecs.() -> Codec<I, O>): FieldCodec<I, O> {
    return FieldCodec(name, block(Codecs))
}

/**
 * Create a new field codec with the given [name]
 * and backed by the codec returned from invoking
 * the given [block].
 */
@Suppress("FunctionName")
@OptIn(ExperimentalCodecApi::class)
fun <I, O : BsonElement> FieldCodec(name: String, block: Codecs.() -> Codec<I, O>): BsonFieldCodec<I, O> {
    return BsonFieldCodec(name, block(Codecs))
}

/**
 * Create a new field codec with the name of the
 * given [property] and backed by [this] codec.
 */
operator fun <I, O> Codec<I, O>.getValue(t: Any?, property: KProperty<*>): FieldCodec<I, O> {
    return FieldCodec(property.name, this)
}

/**
 * Create a new field codec with the name of the
 * given [property] and backed by [this] codec.
 */
@OptIn(ExperimentalCodecApi::class)
operator fun <I, O : BsonElement> Codec<I, O>.getValue(t: Any?, property: KProperty<*>): BsonFieldCodec<I, O> {
    return BsonFieldCodec(property.name, this)
}

/* ============= ------------------ ============= */

/**
 * Marker class for adding well-known codec shortcuts.
 */
interface Codecs {
    companion object : Codecs
}

// Constructor

/**
 * Invoke the given [block] with the [Codecs] companion object.
 */
@Suppress("FunctionName")
inline fun <I, O> Codecs(block: Codecs.() -> Codec<I, O>): Codec<I, O> {
    return block(Codecs)
}

/* ============= ------------------ ============= */

/**
 * An exception thrown when a codec operation fails.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class CodecException(
    message: String? = null,
    cause: Throwable? = null,
    enableSuppression: Boolean = true,
    writableStackTrace: Boolean = true
) : RuntimeException(message ?: cause?.message, cause, enableSuppression, writableStackTrace) {
    companion object {
        private const val serialVersionUID: Long = -1616193939070247846L
    }
}

/* ============= ------------------ ============= */
