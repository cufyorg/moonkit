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

import org.cufy.bson.BsonElement
import org.cufy.bson.BsonDocumentLike
import org.cufy.bson.MutableBsonMapField
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
@Suppress("FunctionName")
fun <I, O : BsonElement> FieldCodec(name: String, block: Codecs.() -> Codec<I, O>): BsonFieldCodec<I, O> {
    return BsonFieldCodec(name, block(Codecs))
}

/* ============= ------------------ ============= */

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
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@CodecKeywordMarker
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
 * Get the value of the field with the name of the
 * [this] codec and decode it using [this] codec.
 *
 * This function was made to be used in this manner:
 *
 * ```kotlin
 * data class MyClass(private val document: BsonDocument) {
 *    val field by "field" be { String } from document
 * }
 * ```
 */
@CodecKeywordMarker
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
 * fun MyClass.field by "field" be { String } from { document }
 * ```
 */
@CodecKeywordMarker
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
 * fun MyClass.field by "field" be { String } from MyClass::document
 * ```
 */
@CodecKeywordMarker
infix fun <T, I> FieldCodec<I, out BsonElement>.from(property: KProperty1<T, BsonDocumentLike>): ReadOnlyProperty<T, I> {
    return ReadOnlyProperty { thisRef, _ -> property.get(thisRef)[this] }
}

/* ============= ------------------ ============= */
