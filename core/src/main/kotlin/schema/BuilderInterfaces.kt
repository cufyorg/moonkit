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
package org.cufy.monkt.schema

import org.cufy.bson.BsonUndefinedType
import org.cufy.bson.BsonValue
import org.cufy.monkt.*
import kotlin.reflect.KClass

/*
 Important Note: these interface might change in
 the future. It was made to make it easier to
 implement features for different kids of tweaks
 with less code and not to be used by regular
 users.
*/

/* =========== - WithOptionsBuilder - =========== */

/**
 * An interface for builders with options.
 *
 * Important Note: this interface might change in
 * the future. It was made to make it easier to
 * implement features for different kids of tweaks
 * with less code and not to be used by regular
 * users.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface WithOptionsBuilder<T : Any, M> {
    /**
     * The instance options.
     */
    @AdvancedMonktApi("Use `option()` instead")
    val options: MutableList<Option<T, M, *>>

    /**
     * The static options.
     */
    @AdvancedMonktApi("Use `staticOption()` instead")
    val staticOptions: MutableList<Option<Unit, Unit, *>>
}

// options

/**
 * Add the given [option].
 *
 * @param option the option to be added.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M, C> WithOptionsBuilder<T, M>.option(
    option: Option<T, M, C>
) {
    options += option
}

/**
 * Added an option with the given arguments.
 *
 * @param configuration the option configuration.
 * @param block the option block.
 * @since 2.0.0
 */
fun <T : Any, M, C> WithOptionsBuilder<T, M>.option(
    configuration: C,
    block: OptionBlock<T, M, C> = {}
) {
    option(Option(configuration, block))
}

// staticOptions

/**
 * Add the given static [option].
 *
 * @param option the option to be added.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M, C> WithOptionsBuilder<T, M>.staticOption(
    option: Option<Unit, Unit, C>
) {
    staticOptions += option
}

/**
 * Added a static option with the given arguments.
 *
 * @param configuration the option configuration.
 * @param block the option block.
 * @since 2.0.0
 */
fun <T : Any, M, C> WithOptionsBuilder<T, M>.staticOption(
    configuration: C,
    block: OptionBlock<Unit, Unit, C> = {}
) {
    staticOption(Option(configuration, block))
}

/* ========== - WithEncodersBuilder  - ========== */

/**
 * An interface for builders with coercer encoders.
 *
 * Important Note: this interface might change in
 * the future. It was made to make it easier to
 * implement features for different kids of tweaks
 * with less code and not to be used by regular
 * users.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface WithEncodersBuilder<T> {
    /**
     * A list of coercers to be used when a direct
     * encoding fails.
     */
    @AdvancedMonktApi("Use `encoder()` instead")
    val encoders: MutableList<Encoder<in T>>

    /**
     * A function to be invoked when encoding
     * coercion fails for some value.
     */
    @AdvancedMonktApi("Use `finalEncoder()` instead")
    var finalEncoder: Encoder<in T>?
}

// encoders

/**
 * Add the given [encoder] to be used as an
 * encoding coercer.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithEncodersBuilder<T>.encoder(
    encoder: Encoder<in T>
) {
    encoders += encoder
}

// finalEncoder

/**
 * The final encoder to be used when all
 * coercers failed.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithEncodersBuilder<T>.finalEncoder(
    encoder: Encoder<in T>
) {
    finalEncoder = encoder
}

/**
 * Make sure this field is encoded and not skipped
 * after coercion failure.
 *
 * This will override the currently set [finalEncoder]
 */
fun <T> WithEncodersBuilder<T>.requireEncode() {
    finalEncoder {
        error("Required encoding failed for value: $it")
    }
}

/* ========== - WithDecodersBuilder  - ========== */

/**
 * An interface for builders with coercer decoders.
 *
 * Important Note: this interface might change in
 * the future. It was made to make it easier to
 * implement features for different kids of tweaks
 * with less code and not to be used by regular
 * users.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface WithDecodersBuilder<T> {
    /**
     * A list of coercers to be used when a direct
     * decoding fails.
     */
    @AdvancedMonktApi("Use `decoder()` instead")
    val decoders: MutableList<Decoder<out T>>

    /**
     * A function to be invoked when decoding
     * coercion fails for some value.
     */
    @AdvancedMonktApi("Use `finalDecoder()` instead")
    var finalDecoder: Decoder<out T>?
}

// decoders

/**
 * Add the given [decoder] to be used as a
 * decoding coercer.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithDecodersBuilder<T>.decoder(
    decoder: Decoder<out T>
) {
    decoders += decoder
}

/**
 * Add a default coercer decoder that uses the given [block].
 *
 * This will only cover the value `undefined`. To
 * set a default value for when the coercion fails
 * you might use [finalDecoder] instead.
 */
fun <T> WithDecodersBuilder<T>.default(
    block: (BsonValue) -> T
) {
    decoder(ScalarDecoder {
        canDecode(BsonUndefinedType)
        decode(block)
    })
}

// finalDecoder

/**
 * The final decoder to be used when all
 * coercers failed.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithDecodersBuilder<T>.finalDecoder(
    decoder: Decoder<out T>
) {
    finalDecoder = decoder
}

/**
 * Make sure this field is decoded and not skipped
 * after coercion failure.
 *
 * This will override the currently set [finalDecoder]
 */
fun <T> WithDecodersBuilder<T>.requireDecode() {
    finalDecoder {
        error("Required decoding failed for value: $it")
    }
}

/* =========== - WithSchemaBuilder  - =========== */

/**
 * An interface for builders with schemas.
 *
 * Important Note: this interface might change in
 * the future. It was made to make it easier to
 * implement features for different kids of tweaks
 * with less code and not to be used by regular
 * users.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface WithSchemaBuilder<T> {
    /**
     * The schema of the values.
     *
     * @since 2.0.0
     */
    @AdvancedMonktApi("Use `schema()` instead")
    var schema: Lazy<Schema<T>>?
}

// schema

/**
 * Set the values' schema to be the result of invoking
 * given [block]
 *
 * This will replace the current schema
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithSchemaBuilder<T>.schema(
    block: () -> Schema<T>
) {
    schema = lazy { block() }
}

/**
 * Set the values' schema to be a nullable schema
 * wrapping the schema returned by the given [block].
 *
 * This will replace the current schema.
 *
 * @since 2.0.0
 */
fun <T> WithSchemaBuilder<T?>.nullableSchema(
    block: () -> Schema<T>
) {
    schema { NullableSchema(block()) }
}

/**
 * Set the values' schema to be an array schema
 * with the given builder [block]
 *
 * This will replace the current schema.
 *
 * @since 2.0.0
 */
fun <I> WithSchemaBuilder<List<I>>.arraySchema(
    schema: Schema<I>? = null,
    block: ArraySchemaBuilderBlock<I> = {}
) {
    schema { ArraySchema(schema, block) }
}

/**
 * Set the values' schema to be an enum schema
 * with the given builder [block]
 *
 * This will replace the current schema.
 *
 * @since 2.0.0
 */
fun <T> WithSchemaBuilder<T>.enumSchema(
    map: Map<BsonValue, T>? = null,
    block: EnumSchemaBuilderBlock<T> = {}
) {
    schema { EnumSchema(map, block) }
}

/**
 * Set the values' schema to be an enum schema
 * with the given builder [block]
 *
 * This will replace the current schema.
 *
 * @since 2.0.0
 */
fun <T : Enum<T>> WithSchemaBuilder<T>.enumSchema(
    enumClass: KClass<T>,
    block: EnumSchemaBuilderBlock<T> = {}
) {
    schema { EnumSchema(enumClass, block) }
}

/* ============ - WithCodecBuilder - ============ */

/**
 * An interface for builders with codec listeners.
 *
 * Important Note: this interface might change in
 * the future. It was made to make it easier to
 * implement features for different kids of tweaks
 * with less code and not to be used by regular
 * users.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface WithCodecBuilder<B> {
    /**
     * A list of functions to be executed after
     * the encoding operation.
     */
    @AdvancedMonktApi("Use `onEncode()` instead")
    val onEncode: MutableList<B>

    /**
     * A list of functions to be executed after
     * the decoding operation.
     */
    @AdvancedMonktApi("Use `onDecode()` instead")
    val onDecode: MutableList<B>
}

/**
 * Add the given [block] to be invoked after the
 * encoding operation.
 */
@OptIn(AdvancedMonktApi::class)
fun <B> WithCodecBuilder<B>.onEncode(
    block: B
) {
    onEncode += block
}

/**
 * Add the given [block] to be invoked after the
 * decoding operation.
 */
@OptIn(AdvancedMonktApi::class)
fun <B> WithCodecBuilder<B>.onDecode(
    block: B
) {
    onDecode += block
}

/* ========== - WithDeferredBuilder  - ========== */

/**
 * An interface for builders with deferred code.
 *
 * Important Note: this interface might change in
 * the future. It was made to make it easier to
 * implement features for different kids of tweaks
 * with less code and not to be used by regular
 * users.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface WithDeferredBuilder {
    /**
     * Code to be invoked on this builder before
     * building.
     */
    @AdvancedMonktApi("Use `deferred()` instead")
    val deferred: MutableList<() -> Unit>
}

/**
 * Add the given [block] to be invoked after
 * building.
 */
@AdvancedMonktApi
fun <B : WithDeferredBuilder> B.deferred(
    block: B.() -> Unit
) {
    deferred += { block(this) }
}
