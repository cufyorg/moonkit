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
import org.cufy.monkt.AdvancedMonktApi
import org.cufy.monkt.ExperimentalMonktApi
import org.cufy.monkt.internal.ScalarSchemaBuilderImpl
import kotlin.reflect.typeOf

/**
 * A schema for scalar values.
 *
 * @param T the type of the runtime value.
 * @since 2.0.0
 */
interface ScalarSchema<T> : Schema<T>, ScalarDecoder<T> {
    @Suppress("UnnecessaryOptInAnnotation")
    @OptIn(AdvancedMonktApi::class)
    override fun canDecode(element: BsonElement): Boolean

    @OptIn(AdvancedMonktApi::class)
    override fun canEncode(value: Any?): Boolean

    @Suppress("UnnecessaryOptInAnnotation")
    @OptIn(AdvancedMonktApi::class)
    override fun decode(element: BsonElement): T

    @OptIn(AdvancedMonktApi::class)
    override fun encode(value: T): BsonElement
}

/**
 * A block of code invoked to fill in options in
 * [ScalarSchemaBuilder].
 */
typealias ScalarSchemaBuilderBlock<T> =
        ScalarSchemaBuilder<T>.() -> Unit

/**
 * A builder for creating a [ScalarSchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
interface ScalarSchemaBuilder<T> :
    ScalarDecoderBuilder<T>,
    WithDeferredBuilder {

    @AdvancedMonktApi("Use `canEncode()` instead")
    val canEncodeBlocks: MutableList<(Any?) -> Boolean>

    /**
     * The wrapped encoder
     */
    @AdvancedMonktApi("Use `encode()` instead")
    var encodeBlock: ((T) -> BsonElement)? // REQUIRED

    /**
     * Build the schema.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    override fun build(): ScalarSchema<T>
}

/**
 * Obtain a new [ScalarSchemaBuilder].
 *
 * @since 2.0.0
 */
fun <T> ScalarSchemaBuilder(): ScalarSchemaBuilder<T> {
    return ScalarSchemaBuilderImpl()
}

/**
 * Construct a new [ScalarSchema] with the given
 * [block]
 *
 * @param block the builder block.
 * @return a new scalar schema.
 * @since 2.0.0
 */
fun <T> ScalarSchema(
    block: ScalarSchemaBuilderBlock<T> = {}
): ScalarSchema<T> {
    val builder = ScalarSchemaBuilder<T>()
    builder.apply(block)
    return builder.build()
}

// encodeBlock

/**
 * Set the encoder function to be the given [block]
 *
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
fun <T> ScalarSchemaBuilder<T>.encode(
    block: (T) -> BsonElement
) {
    this.encodeBlock = block
}

// canEncodeBlocks

/**
 * Add the given [block] to determine if the schema
 * can encode some value.
 */
@OptIn(AdvancedMonktApi::class)
fun <T> ScalarSchemaBuilder<T>.canEncode(
    block: (Any?) -> Boolean
) {
    this.canEncodeBlocks += block
}

@ExperimentalMonktApi("Might be removed in the future")
@OptIn(AdvancedMonktApi::class)
inline fun <reified T> ScalarSchemaBuilder<T>.canEncode() {
    val klass = T::class
    val isNullable = typeOf<T>().isMarkedNullable
    this.canEncodeBlocks += {
        klass.isInstance(it) &&
                (it != null || isNullable)
    }
}
