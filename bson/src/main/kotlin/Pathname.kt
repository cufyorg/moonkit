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
 * A pathname is the names to reach a field in a
 * nested object.
 *
 * The pathname of the field 'x' in the object below:
 *
 * ```json
 * { alpha: { beta: { gamma: { x: "" } } } }
 * ```
 *
 * Is: `alpha.beta.gamma.x`
 *
 * @author LSafer
 * @since 2.0.0
 */
data class Pathname(
    /**
     * The path segments.
     *
     * @since 2.0.0
     */
    val segments: List<String>
) : CharSequence {
    /**
     * The pathname string representation.
     */
    private val value by lazy { segments.joinToString(".") }

    override val length: Int get() = value.length

    /**
     * Construct a new pathname from the given [segments].
     *
     * @param segments the pathname segments.
     * @since 2.0.0
     */
    constructor(vararg segments: String)
            : this(segments.asList())

    override fun get(index: Int): Char = value[index]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        value.subSequence(startIndex, endIndex)

    /**
     * Construct a new pathname with the segments
     * of this pathname plus the given [segment].
     *
     * @param segment the segment to be added.
     * @return a new pathname.
     * @since 2.0.0
     */
    operator fun plus(segment: String): Pathname =
        Pathname(segments + segment)

    /**
     * Construct a new pathname with the segments
     * of this pathname plus the segments of given
     * [pathname].
     *
     * @param pathname the pathname to be added.
     * @return a new pathname.
     * @since 2.0.0
     */
    operator fun plus(pathname: Pathname): Pathname =
        Pathname(segments + pathname.segments)

    /**
     * Construct a new pathname with the segments
     * in this pathname starting from the given
     * [from] and ending before the given [to].
     *
     * If [from] is negative. It will be treated
     * as if `segments.size - from` was provided.
     *
     * If [to] is negative. It will be treated as
     * if `segments.size - to` was provided.
     *
     * An empty pathname will be returned if:
     * - [from] was lesser than `0`
     * - [to] was greater than `segments.size`
     * - [from] was greater than [to]
     *
     * @param from the start of the segments.
     * @param to the end of the segments.
     * @return a new pathname.
     * @since 2.0.0
     */
    operator fun invoke(from: Int, to: Int = segments.size): Pathname {
        val size = segments.size

        val end = if (to < 0) size + to else to
        val start = if (from < 0) size + from else from

        if (start < 0 || end > size || start > end)
            return Pathname()

        return Pathname(segments.subList(start, end))
    }

    override fun toString(): String = value
}
