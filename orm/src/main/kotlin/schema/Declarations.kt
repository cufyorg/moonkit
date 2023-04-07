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
import org.cufy.monkt.*

/**
 * An encoder is a function that converts instances
 * of type [T] into a [BsonElement].
 */
fun interface Encoder<T> {
    /**
     * Return `true` if this encoder can encode
     * the given [value].
     *
     * This function doesn't fully guarantee that
     * [encode] will succeed. It was put only to
     * know if this encoder accepts the [value] or
     * not.
     *
     * @param value the value to be checked.
     * @return true, if this encoder can encode [value].
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun canEncode(value: Any?): Boolean {
        return true
    }

    /**
     * Encode the given [value] to bson.
     *
     * @param value the value to be encoded.
     * @return the encoded bson.
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun encode(value: T): BsonElement
}

/**
 * A decoder is a function that converts a
 * [BsonElement] into an instance of type [T].
 *
 * @author LSafer
 * @since 2.0.0
 */
fun interface Decoder<T> {
    /**
     * Return true if this decoder can decode the
     * given [element].
     *
     * This function doesn't fully guarantee that
     * [encode] will succeed. It was put only to
     * know if this decoder accepts the [element]
     * or not.
     *
     * @param element the value to be checked.
     * @return true, if this decoder can decode [element].
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun canDecode(element: BsonElement): Boolean {
        return true
    }

    /**
     * Decode the given [element] to [T].
     *
     * @param element the value to be decoded.
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun decode(element: BsonElement): T
}

/**
 * A schema defines how to serialize values
 * into bson and vice-versa.
 *
 * Schema equality and hashCode calculations
 * should be based on pointer value.
 * (aka, [System.identityHashCode] and `===`)
 *
 * @author LSafer
 * @since 2.0.0
 */
interface Schema<T> : Encoder<T>, Decoder<T>

/**
 * A schema that supports the options' system.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface ElementSchema<T> : Schema<T> {
    /**
     * Obtain the static options in this schema.
     *
     * @param model the options model.
     * @param pathname the pathname to this schema.
     * @param dejaVu a set of schemas previously visited.
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun obtainStaticOptions(
        model: Model<*>,
        pathname: Pathname,
        dejaVu: Set<Schema<*>>
    ): List<OptionData<Unit, Unit, *>>

    /**
     * Obtain the options in this schema for the
     * given [instance].
     *
     * @param model the options model.
     * @param root the root instance.
     * @param pathname the pathname to this schema.
     * @param instance the instance to get its options.
     * @since 2.0.0
     */
    @AdvancedMonktApi
    fun obtainOptions(
        model: Model<*>,
        root: Any,
        pathname: Pathname,
        instance: T & Any
    ): List<OptionData<*, *, *>>
}
