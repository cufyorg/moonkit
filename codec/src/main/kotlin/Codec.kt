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
     * > Do not use this function directly. Use `encode(I, Codec)` instead.
     *
     * @param value the value to be encoded.
     * @return the encoded value.
     * @since 2.0.0
     */
    fun encode(value: Any?): Result<O>

    /**
     * Decode the given [value] value into [I].
     *
     * > Do not use this function directly. Use `decode(Codec, I)` instead.
     *
     * @param value the value to be decoded.
     * @return the decoded value.
     * @since 2.0.0
     */
    fun decode(value: Any?): Result<I>
}

/* ============= ------------------ ============= */

@CodecKeywordMarker
infix fun <I, O> Codec<I, O>.defaultIn(defaultValue: I): Codec<I, O> {
    val codec = this
    return object : Codec<I, O> {
        override fun encode(value: Any?): Result<O> {
            return codec.encode(value)
        }

        override fun decode(value: Any?): Result<I> {
            return runCatching {
                codec.decode(value).getOrDefault(defaultValue)
            }
        }
    }
}

@CodecKeywordMarker
infix fun <I, O> Codec<I, O>.catchIn(block: (Throwable) -> I): Codec<I, O> {
    val codec = this
    return object : Codec<I, O> {
        override fun encode(value: Any?): Result<O> {
            return codec.encode(value)
        }

        override fun decode(value: Any?): Result<I> {
            return runCatching {
                codec.decode(value).getOrElse(block)
            }
        }
    }
}

@CodecKeywordMarker
infix fun <I, O> Codec<I, O>.defaultOut(defaultValue: O): Codec<I, O> {
    val codec = this
    return object : Codec<I, O> {
        override fun encode(value: Any?): Result<O> {
            return runCatching {
                codec.encode(value).getOrDefault(defaultValue)
            }
        }

        override fun decode(value: Any?): Result<I> {
            return codec.decode(value)
        }
    }
}

@CodecKeywordMarker
infix fun <I, O> Codec<I, O>.catchOut(block: (Throwable) -> O): Codec<I, O> {
    val codec = this
    return object : Codec<I, O> {
        override fun encode(value: Any?): Result<O> {
            return runCatching {
                codec.encode(value).getOrElse(block)
            }
        }

        override fun decode(value: Any?): Result<I> {
            return codec.decode(value)
        }
    }
}

/* ============= ------------------ ============= */

// Encode Any

/**
 * Encode the given [value] to [O] using the given [codec].
 *
 * @param value the value to encode. (type checked at runtime)
 * @param codec the codec to be used.
 * @return the encoding result.
 * @since 2.0.0
 */
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

// Encode Infix

/**
 * Encode [this] value to [O] using the given [codec].
 *
 * @receiver the value to encode.
 * @param codec the codec to be used.
 * @return the encoded value.
 * @throws CodecException if encoding failed.
 * @since 2.0.0
 */
@JvmName("encodeInfix")
@CodecKeywordMarker
infix fun <I, O> I.encode(codec: Codec<I, O>): O {
    return encode(this, codec)
}

/**
 * Encode [this] value to [O] using the given [codec].
 *
 * @receiver the value to encode.
 * @param codec the codec to be used.
 * @return the encoded value.
 * @throws CodecException if encoding failed.
 * @since 2.0.0
 */
@JvmName("encodeAnyInfix")
@CodecKeywordMarker
infix fun <I, O> Any?.encodeAny(codec: Codec<I, O>): O {
    return encodeAny(this, codec)
}

/* ============= ------------------ ============= */

// Decode Any

/**
 * Decode the given [value] to [O] using the given [codec].
 *
 * @param value the value to decode. (type checked at runtime)
 * @param codec the codec to be used.
 * @return the decoding result.
 * @since 2.0.0
 */
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

// Decode Infix

/**
 * Decode [this] value to [I] using the given [codec].
 *
 * @receiver the value to decode.
 * @param codec the codec to be used.
 * @return the decoded value.
 * @throws CodecException if decoding failed.
 * @since 2.0.0
 */
@JvmName("decodeInfix")
@CodecKeywordMarker
infix fun <I, O> O.decode(codec: Codec<I, O>): I {
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
@JvmName("decodeAnyInfix")
@CodecKeywordMarker
infix fun <I, O> Any?.decodeAny(codec: Codec<I, O>): I {
    return decodeAny(this, codec)
}

/* ============= ------------------ ============= */

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
        Result.success(block(value))
    } catch (error: CodecException) {
        Result.failure(error)
    } catch (error: Throwable) {
        Result.failure(CodecException(cause = error))
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
        else -> Result.failure(
            CodecException(
                "Cannot encode/decode ${value?.let { it::class }}; expected ${T::class}"
            )
        )
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
            Result.success(block(it))
        } catch (error: CodecException) {
            Result.failure(error)
        } catch (error: Throwable) {
            Result.failure(CodecException(cause = error))
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
