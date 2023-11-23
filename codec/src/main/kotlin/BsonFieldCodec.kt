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

import org.cufy.bson.*
import java.util.*
import kotlin.experimental.ExperimentalTypeInference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty1

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
@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Use `Codecs` directly")
fun <I, O : BsonElement> FieldCodec(name: String, block: Codecs.() -> Codec<I, O>): BsonFieldCodec<I, O> {
    return BsonFieldCodec(name, block(Codecs))
}

/* ============= ------------------ ============= */

/**
 * Return a field codec derived from this one with
 * its name tagged with the given language [tag].
 */
@CodecKeywordMarker
infix fun <I, O : BsonElement> BsonFieldCodec<I, O>.lang(tag: String): BsonFieldCodec<I, O> {
    if (tag.isEmpty()) return this
    return FieldCodec("$name#$tag", this)
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
 *
 * Example:
 *
 * ```
 * object User {
 *      val Name = "name" be Codecs.String
 *      val Age = "age" be Codecs.Int64.Nullable
 * }
 * ```
 */
@CodecKeywordMarker
infix fun <I, O : BsonElement> String.be(codec: Codec<I, O>): BsonFieldCodec<I, O> {
    return FieldCodec(this, codec)
}

/**
 * Create a new field codec with the receiver name
 * and backed by the codec from the given [block].
 *
 * Example:
 *
 * ```
 * object User {
 *      val Name = "name" be { String }
 *      val Age = "age" be { Int64.Nullable }
 * }
 * ```
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@CodecKeywordMarker
@Deprecated("Use `Codecs` directly")
infix fun <I, O : BsonElement> String.be(block: Codecs.() -> Codec<I, O>): BsonFieldCodec<I, O> {
    return FieldCodec(this, block)
}

@OptIn(ExperimentalCodecApi::class)
@CodecKeywordMarker
infix fun <I, O : BsonElement> BsonFieldCodec<I, O>.defaultIn(defaultValue: I): BsonFieldCodec<I, O> {
    val codec = this as Codec<I, O>
    return BsonFieldCodec(name, codec defaultIn defaultValue)
}

@OptIn(ExperimentalCodecApi::class)
@CodecKeywordMarker
infix fun <I, O : BsonElement> BsonFieldCodec<I, O>.catchIn(block: (Throwable) -> I): BsonFieldCodec<I, O> {
    val codec = this as Codec<I, O>
    return BsonFieldCodec(name, codec catchIn block)
}

@OptIn(ExperimentalCodecApi::class)
@CodecKeywordMarker
infix fun <I, O : BsonElement> BsonFieldCodec<I, O>.defaultOut(defaultValue: O): BsonFieldCodec<I, O> {
    val codec = this as Codec<I, O>
    return BsonFieldCodec(name, codec defaultOut defaultValue)
}

@OptIn(ExperimentalCodecApi::class)
@CodecKeywordMarker
infix fun <I, O : BsonElement> BsonFieldCodec<I, O>.catchOut(block: (Throwable) -> O): BsonFieldCodec<I, O> {
    val codec = this as Codec<I, O>
    return BsonFieldCodec(name, codec catchOut block)
}

/* ============= ------------------ ============= */

/**
 * Get the value of the field with the name of the
 * given [codec] and decode it using the given [codec].
 */
operator fun <I> BsonDocumentLike.get(codec: FieldCodec<I, out BsonElement>): I {
    return decodeAny(this[codec.name], codec)
}

/**
 * Select the element with the perfect tag for the given [language] preference.
 *
 * @param language a list of comma-separated language ranges or a list of language
 *                 ranges in the form of the "Accept-Language" header defined in RFC 2616
 * @throws IllegalArgumentException if a language range or a weight found in the
 *                                  given ranges is ill-formed
 * @see Locale.LanguageRange.parse
 */
operator fun <I> BsonDocumentLike.get(codec: FieldCodec<I, out BsonElement>, language: String): I {
    return decodeAny(this[codec.name, language], codec)
}

/**
 * Select the element with the perfect tag for the given [language] preference.
 *
 * @param language the languages ordered by preference. (e.g. `["en-US", "ar-SA"]`)
 * @throws IllegalArgumentException if the given range does not comply with the
 *                                  syntax of the language range mentioned
 *                                  in RFC 4647
 * @see Locale.LanguageRange
 */
operator fun <I> BsonDocumentLike.get(codec: FieldCodec<I, out BsonElement>, language: List<String>): I {
    return decodeAny(this[codec.name, language], codec)
}

/**
 * Return the tags of the fields that has the name of the given [codec].
 */
@ExperimentalBsonApi
fun <I> BsonDocumentLike.getLangList(codec: FieldCodec<I, out BsonElement>): List<String> {
    return getLangList(codec.name)
}

/**
 * Get the value of the field with the name of the
 * [this] codec and decode it using [this] codec.
 *
 * This function was made to be used in this manner:
 *
 * ```kotlin
 * data class MyClass(val document: BsonDocument) {
 *    val field by "field" be { String } from document
 * }
 * ```
 *
 * This function can be replaced in this manner:
 *
 * ```kotlin
 * private val FIELD = "field" be { String }
 *
 * data class MyClass(val document: BsonDocument) {
 *    val field by lazy { document[FIELD] }
 * }
 * ```
 */
@CodecKeywordMarker
@Deprecated("use lazy { } instead", ReplaceWith("lazy { map[this] }"))
infix fun <I> FieldCodec<I, out BsonElement>.from(map: BsonDocumentLike): Lazy<I> {
    return lazy { map[this] }
}

/**
 * Return a readonly property that returns the value of the field
 * with the name of [this] from the result of invoking the given [block]
 * and decode it using [this] codec.
 *
 * This function was made to be used in this manner:
 *
 * ```kotlin
 * data class MyClass(val document: BsonDocument)
 *
 * val MyClass.field by "field" be { String } from { document }
 * ```
 *
 * This function can be replaced in this manner:
 *
 * ```kotlin
 * data class MyClass(val document: BsonDocument)
 *
 * private val FIELD = "field" be { String }
 *
 * val MyClass.field get() = document[FIELD]
 * ```
 */
@CodecKeywordMarker
@Deprecated("use a field with custom getter instead")
infix fun <T, I> FieldCodec<I, out BsonElement>.from(block: T.() -> BsonDocumentLike): ReadOnlyProperty<T, I> {
    return ReadOnlyProperty { thisRef, _ -> block(thisRef)[this] }
}

/**
 * Return a readonly property that returns the value of the field
 * with the name of [this] from the result of invoking the given [property]
 * and decode it using [this] codec.
 *
 * This function was made to be used in this manner:
 *
 * ```kotlin
 * data class MyClass(val document: BsonDocument)
 *
 * val MyClass.field by "field" be { String } from MyClass::document
 * ```
 *
 * This function can be replaced in this manner:
 *
 * ```kotlin
 * data class MyClass(val document: BsonDocument)
 *
 * private val FIELD = "field" be { String }
 *
 * val MyClass.field get() = document[FIELD]
 * ```
 */
@CodecKeywordMarker
@Deprecated("use a field with custom getter instead")
infix fun <T, I> FieldCodec<I, out BsonElement>.from(property: KProperty1<T, BsonDocumentLike>): ReadOnlyProperty<T, I> {
    return ReadOnlyProperty { thisRef, _ -> property.get(thisRef)[this] }
}

/* ============= ------------------ ============= */
