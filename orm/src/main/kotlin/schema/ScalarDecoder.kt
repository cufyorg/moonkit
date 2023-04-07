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

import org.cufy.bson.BsonElement
import org.cufy.bson.BsonType
import org.cufy.monkt.*
import org.cufy.monkt.internal.*

/**
 * A decoder for scalar values.
 */
interface ScalarDecoder<T> : Decoder<T> {
    /**
     * The types this scalar is targeting.
     *
     * @since 2.0.0
     */
    val types: List<BsonType>

    @OptIn(AdvancedMonktApi::class)
    override fun canDecode(element: BsonElement): Boolean

    @OptIn(AdvancedMonktApi::class)
    override fun decode(element: BsonElement): T
}

/**
 * A block of code invoked to fill in options in
 * [ScalarDecoderBuilder].
 */
typealias ScalarDecoderBuilderBlock<T> =
        ScalarDecoderBuilder<T>.() -> Unit

/**
 * A builder for creating a [ScalarDecoder]
 *
 * @author LSafer
 * @since 2.0.0
 */
interface ScalarDecoderBuilder<T> :
    WithDeferredBuilder {

    /**
     * The expected bson types.
     */
    @AdvancedMonktApi("Use `accept()` instead")
    val types: MutableList<BsonType>

    /**
     * The decoding predicate blocks.
     *
     * Any block returns `true` will mean the
     * decoding is possible.
     */
    @AdvancedMonktApi("Use `canDecode()` instead")
    val canDecodeBlocks: MutableList<(BsonElement) -> Boolean>

    /**
     * The decoding block.
     */
    @AdvancedMonktApi("Use `decode()` instead")
    var decodeBlock: ((BsonElement) -> T)? // REQUIRED

    /**
     * Build the decoder.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun build(): ScalarDecoder<T>
}

/**
 * Obtain a new [ScalarDecoderBuilder].
 *
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T> ScalarDecoderBuilder(): ScalarDecoderBuilder<T> {
    return ScalarDecoderBuilderImpl()
}

/**
 * Construct a new [ScalarDecoder] with the given
 * [block]
 *
 * @param block the builder block.
 * @return a new scalar coercer.
 * @since 2.0.0
 */
fun <T> ScalarDecoder(
    block: ScalarDecoderBuilderBlock<T> = {}
): ScalarDecoder<T> {
    val builder = ScalarDecoderBuilder<T>()
    builder.apply(block)
    return builder.build()
}

// types

/**
 * Add the given [types] to the accepted types.
 *
 * This will not add them to the predicate.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> ScalarDecoderBuilder<T>.expect(
    vararg types: BsonType
) {
    this.types += types
}

// canDecodeBlocks

/**
 * Add the given [block] to determine if the schema
 * can decode some value.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> ScalarDecoderBuilder<T>.canDecode(
    block: (BsonElement) -> Boolean
) {
    this.canDecodeBlocks += block
}

/**
 * Add the given [types] to the accepted types and
 * add a predicate that allows the matching values.
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> ScalarDecoderBuilder<T>.canDecode(
    vararg types: BsonType
) {
    this.types += types
    this.canDecodeBlocks += { it.type in types }
}

// decodeBlock

/**
 * Set the decoder function to be the given [block]
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> ScalarDecoderBuilder<T>.decode(
    block: (BsonElement) -> T
) {
    this.decodeBlock = block
}

// decodeBlock

@OptIn(AdvancedMonktApi::class)
fun <T> ScalarDecoderBuilder<T>.deterministic(
    block: DeterministicDecoderBlock<T>
) {
    this.canDecodeBlocks += {
        val scope = DeterministicDecoderScope<T>()
        scope.apply { block(it) }
        scope.isDecoded
    }
    this.decodeBlock = {
        val scope = DeterministicDecoderScope<T>()
        scope.apply { block(it) }
        scope.decodedValue
    }
}
