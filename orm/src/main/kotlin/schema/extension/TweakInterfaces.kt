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
package org.cufy.monkt.schema.extension

import org.cufy.monkt.*
import org.cufy.monkt.schema.*
import kotlin.reflect.KCallable

/*
 Important Note: these interface might change in
 the future. It was made to make it easier to
 implement features for different kids of tweaks
 with less code and not to be used by regular
 users.
*/

/* ============ - WithFiltersTweak - ============ */

/**
 * An interface for tweaks with filters.
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
interface WithFiltersTweak<C> {
    /**
     * A list of filters to filter the performance
     * options.
     */
    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    val filters: MutableList<ReturnOptionDataBlock<*, *, C, Boolean>>
}

/**
 * Skip the options that does *not* satisfy the
 * given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun <C> WithFiltersTweak<C>.filter(block: ReturnOptionDataBlock<*, *, C, Boolean>) {
    filters += block
}

/**
 * Skip the options that are *not* in any of the
 * given [pathnames].
 */
fun <C> WithFiltersTweak<C>.filter(vararg pathnames: Pathname) {
    filter { pathname in pathnames }
}

/**
 * Skip the options that are *not* in any of the
 * given [pathnames].
 */
fun <C> WithFiltersTweak<C>.filter(vararg pathnames: String) {
    filter { pathnames.any { pathname.contentEquals(it) } }
}

/**
 * Skip the options that are *not* in any of the
 * given [pathnames].
 */
fun <C> WithFiltersTweak<C>.filter(vararg pathnames: KCallable<*>) {
    filter { pathnames.any { pathname.contentEquals(it.name) } }
}

/**
 * Skip the options that does satisfy the given
 * [block].
 */
fun <C> WithFiltersTweak<C>.skip(block: ReturnOptionDataBlock<*, *, C, Boolean>) {
    filter { !block() }
}

/**
 * Skip the options that are in any of the given
 * [pathnames].
 */
fun <C> WithFiltersTweak<C>.skip(vararg pathnames: Pathname) {
    skip { pathname in pathnames }
}

/**
 * Skip the options that are in any of the given
 * [pathnames].
 */
fun <C> WithFiltersTweak<C>.skip(vararg pathnames: String) {
    skip { pathnames.any { pathname.contentEquals(it) } }
}

/**
 * Skip the options that are in any of the given
 * [pathnames].
 */
fun <C> WithFiltersTweak<C>.skip(vararg pathnames: KCallable<*>) {
    skip { pathnames.any { pathname.contentEquals(it.name) } }
}
