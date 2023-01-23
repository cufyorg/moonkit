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

import org.cufy.bson.BsonArray
import org.cufy.monkt.*
import org.cufy.monkt.internal.*

/**
 * A schema wrapper for a list of values.
 *
 * @param T the type of the wrapped schema.
 * @since 2.0.0
 */
interface ArraySchema<T> : ElementSchema<List<T>> {
    /**
     * The wrapped schema.
     */
    val schema: Schema<T>
}

/**
 * A block of code invoked after array schema
 * operations.
 */
typealias ArraySchemaCodecBlock<T> =
        ArraySchemaCodecScope<T>.() -> Unit

/**
 * A block of code invoked after array schema
 * operations  with a result.
 */
typealias ReturnArraySchemaCodecBlock<T, R> =
        ArraySchemaCodecScope<T>.() -> R

/**
 * A block of code invoked to fill in options in
 * [ArraySchemaBuilder].
 */
typealias ArraySchemaBuilderBlock<T> =
        ArraySchemaBuilder<T>.() -> Unit

/**
 * A scope passed to array schema operation
 * listeners.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ArraySchemaCodecScope<T>(
    /**
     * The schema.
     */
    val schema: ArraySchema<T>,
    /**
     * The instance.
     */
    val instance: List<T>,
    /**
     * The source array.
     */
    val array: BsonArray
)

/**
 * A builder for creating an [ArraySchema]
 *
 * @author LSafer
 * @since 2.0.0
 */
interface ArraySchemaBuilder<T> :
    WithOptionsBuilder<List<T>, List<T>>,
    WithDecodersBuilder<T>,
    WithEncodersBuilder<T>,
    WithSchemaBuilder<T>,
    WithCodecBuilder<ArraySchemaCodecBlock<T>>,
    WithDeferredBuilder {

    /**
     * Build the schema.
     *
     * This will invoke the deferred code and
     * removes it.
     *
     * @since 2.0.0
     */
    fun build(): ArraySchema<T>
}

/**
 * Obtain a new [ArraySchemaBuilder].
 *
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
fun <T> ArraySchemaBuilder(): ArraySchemaBuilder<T> {
    return ArraySchemaBuilderImpl()
}

/**
 * Construct a new [ArraySchema] with the given
 * [block]
 *
 * @param schema the initial items' schema.
 * @param block the builder block.
 * @return a new array schema.
 * @since 2.0.0
 */
fun <T> ArraySchema(
    schema: Schema<T>? = null,
    block: ArraySchemaBuilderBlock<T> = {}
): ArraySchema<T> {
    val builder = ArraySchemaBuilder<T>()
    schema?.let { builder.schema { it } }
    builder.apply(block)
    return builder.build()
}

//

@Suppress("UNCHECKED_CAST")
@OptIn(AdvancedMonktApi::class)
val <T, C> OptionScope<List<T>, List<T>, C>.arraySchema: ArraySchema<T>
    get() = declaration as? ArraySchema<T>
        ?: error("Option was not declared in an ArraySchema")
