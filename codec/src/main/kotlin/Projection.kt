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

import org.cufy.bson.BsonDocument
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.reflect

/**
 * An interface to implement a codec by delegating
 * to another codec.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface CodecOf<I, O> : Codec<I, O> {
    /**
     * The delegated codec.
     *
     * @since 2.0.0
     */
    val codec: Codec<I, O>

    @AdvancedCodecApi
    override fun encode(value: Any?) = codec.encode(value)

    @AdvancedCodecApi
    override fun decode(value: Any?) = codec.decode(value)
}

typealias DocumentCodecOf<I> = CodecOf<I, BsonDocument>

/**
 * An interface simplifying projection
 * implementations.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface Projection<O> {
    /**
     * The projected element.
     */
    val element: O
}

typealias DocumentProjection = Projection<BsonDocument>

/**
 * A codec implementation of [Projection].
 *
 * @author LSafer
 * @since 2.0.0
 */
@ExperimentalCodecApi
class ProjectionCodec<I : Projection<O>, O>(
    val constructor: (O) -> I
) : Codec<I, O> {
    private lateinit var inT: KType
    private lateinit var outT: KType

    init {
        @OptIn(ExperimentalReflectionOnLambdas::class)
        val reflection = constructor.reflect()
                ?: error("Cannot reflect the constructor")

        inT = reflection.returnType
        outT = reflection.parameters.single().type
    }

    @AdvancedCodecApi
    override fun encode(value: Any?): Result<O> {
        return inT.safeCast<I>(value).mapCatching {
            it.element
        }
    }

    @AdvancedCodecApi
    override fun decode(value: Any?): Result<I> {
        return outT.safeCast<O>(value).mapCatching {
            constructor(it)
        }
    }

    private fun <T> KType.safeCast(value: Any?): Result<T> {
        @Suppress("UNCHECKED_CAST")
        return when (value) {
            null -> when {
                isMarkedNullable -> success(null as T)
                else -> failure(CodecException(
                    "Cannot encode/decode null; expected $this"
                ))
            }
            else -> when {
                jvmErasure.isInstance(value) -> success(value as T)
                else -> failure(CodecException(
                    "Cannot encode/decode ${value::class}; expected $this"
                ))
            }
        }
    }
}

/**
 * A helper function to create a codec from a projection constructor.
 */
@ExperimentalCodecApi
operator fun <I : Projection<O>, O> ((O) -> I).getValue(t: Any?, p: KProperty<*>) =
    ProjectionCodec(this)
