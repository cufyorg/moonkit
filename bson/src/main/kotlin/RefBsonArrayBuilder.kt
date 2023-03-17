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
package org.cufy.bson

/**
 * A block of code building a bson array with a generic `ref`.
 *
 * @since 2.0.0
 */
typealias RefBsonArrayBlock<T> = RefBsonArrayBuilder<T>.() -> Unit

/**
 * A builder building a [BsonArray] with a generic [ref].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class RefBsonArrayBuilder<T>(
    @BsonBuildMarker
    val ref: T,
    array: BsonArray = BsonArray()
) : BsonArrayBuilder(array)

/**
 * Construct a new bson array using the given
 * builder [block] with [ref].
 */
@BsonBuildMarker
fun <T> array(
    ref: T,
    block: RefBsonArrayBlock<T>
): BsonArray {
    val builder = RefBsonArrayBuilder(ref)
    builder.apply(block)
    return builder.array
}

/**
 * Return a pair containing [this] and the result
 * from creating an array with the given [block].
 */
@BsonBuildMarker
infix fun <T> T.tobarray(
    block: RefBsonArrayBlock<T>
): Pair<T, BsonArray> {
    return this to array(this, block)
}

/**
 * Apply the given [block] to this array with [ref].
 */
fun <T> BsonArray.configure(
    ref: T,
    block: RefBsonArrayBlock<T>
) {
    val builder = RefBsonArrayBuilder(ref, this)
    builder.apply(block)
}
