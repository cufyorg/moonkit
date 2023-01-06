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

import org.cufy.bson.BsonType
import org.cufy.bson.BsonUndefinedType
import org.cufy.bson.BsonValue
import org.cufy.monkt.*
import java.util.function.Predicate
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

/* ========== - WithCoercersBuilder  - ========== */

/**
 * An interface for builders with coercers.
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
interface WithCoercersBuilder<T> {
    /**
     * A list of coercers to be used when a direct
     * decoding fails.
     */
    @AdvancedMonktApi("Use `coercer()` instead")
    val coercers: MutableList<Coercer<out T>>

    /**
     * A function to be invoked when coercion
     * fails for some value.
     */
    @AdvancedMonktApi("Use `finalDecoder()` instead")
    var finalDecoder: Decoder<out T>?
}

// coercers

/**
 * Add the given [coercer].
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithCoercersBuilder<T>.coercer(
    coercer: Coercer<out T>
) {
    coercers += coercer
}

/**
 * Add a scalar coercer that uses the
 * given [block].
 */
fun <T> WithCoercersBuilder<T>.coercer(
    block: ScalarCoercerBuilderBlock<T>
) {
    coercer(ScalarCoercer(block))
}

/**
 * Add a default coercer that uses the given [decoder].
 *
 * This will only cover the value `undefined`. To
 * set a default value for when the coercion fails
 * you might use [finalDecoder] instead.
 */
fun <T> WithCoercersBuilder<T>.default(
    decoder: Decoder<out T>
) {
    coercer {
        accept(BsonUndefinedType)
        decode(decoder)
    }
}

// finalDecoder

/**
 * The final decoder to be used when all
 * coercers failed.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithCoercersBuilder<T>.finalDecoder(
    block: Decoder<out T>
) {
    finalDecoder = block
}

/**
 * Make sure this field is decoded and not skipped
 * after coercion failure.
 *
 * This will override the currently set [finalDecoder]
 */
fun <T> WithCoercersBuilder<T>.requireDecode() {
    finalDecoder {
        error("Required Coercing failed for value: $it")
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
 * Set the values' schema to be an array schema
 * with the given builder [block]
 *
 * This will replace the current schema.
 *
 * @since 2.0.0
 */
@ExperimentalMonktApi
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
@ExperimentalMonktApi
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
@ExperimentalMonktApi
fun <T : Enum<T>> WithSchemaBuilder<T>.enumSchema(
    enumClass: KClass<T>,
    block: EnumSchemaBuilderBlock<T> = {}
) {
    schema { EnumSchema(enumClass, block) }
}

/* ============= - ScalarBuilder  - ============= */

/**
 * An interface for builders building scalar types.
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
interface WithDecoderBuilder<T> {
    @AdvancedMonktApi("Use `decode()` instead")
    var decoder: Decoder<out T>?

    @AdvancedMonktApi("Use `accept()` instead")
    val types: MutableList<BsonType>

    @AdvancedMonktApi("Use `accept()` instead")
    val predicate: MutableList<Predicate<BsonValue>>
}

//

/**
 * Set the decoder and the predicate from the given
 * [coercer].
 *
 * This will replace the current decoder
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithDecoderBuilder<T>.coercer(
    coercer: Coercer<out T>
) {
    this.predicate += Predicate { coercer.canDecode(it) }
    this.decoder = coercer
}

/**
 * Set the decoder and the predicate from a
 * deterministic coercer that uses the given
 * [block].
 *
 * This will replace the current decoder
 *
 * @since 2.0.0
 */
fun <T> WithDecoderBuilder<T>.deterministic(
    block: DeterministicCoercerBlock<T>
) {
    coercer(DeterministicCoercer(block))
}

// types

/**
 * Add the given [types] to the accepted types.
 *
 * This will not add them to the predicate.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithDecoderBuilder<T>.expect(
    vararg types: BsonType
) {
    this.types += types
}

// predicate

/**
 * Add the given [predicate].
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithDecoderBuilder<T>.accept(
    predicate: Predicate<BsonValue>
) {
    this.predicate += predicate
}

// decoder

/**
 * Set the decoder to the given [decoder].
 *
 * This will replace the current decoder.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithDecoderBuilder<T>.decode(
    decoder: Decoder<out T>
) {
    this.decoder = decoder
}

//

/**
 * Add the given [types] to the accepted types and
 * add a predicate that allows the matching values.
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> WithDecoderBuilder<T>.accept(
    vararg types: BsonType
) {
    this.types += types
    this.predicate += Predicate { it.bsonType in types }
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
