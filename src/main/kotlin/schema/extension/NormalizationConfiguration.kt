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

/* ============= ---- Protocol ---- ============= */

/**
 * Options to tweak the behaviour of Normalization
 * performance.
 */
class NormalizationTweak :
    WithFiltersTweak<NormalizationConfiguration> {

    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    override val filters: MutableList<ReturnOptionDataBlock<*, *, NormalizationConfiguration, Boolean>> =
        mutableListOf()
}

/**
 * The normalize option configuration.
 *
 * Covers [normalization]
 *
 * @author LSafer
 * @since 2.0.0
 */
open class NormalizationConfiguration

/* ============= --- Extensions --- ============= */

// builder

/**
 * Add the given [block] to be invoked before
 * instance decoding to normalize the instance.
 *
 * @param block the option block.
 * @since 2.0.0
 * @see InitializationConfiguration
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.normalization(
    block: OptionBlock<T, M, NormalizationConfiguration>
) {
    option(NormalizationConfiguration(), block)
}

// monkt

/**
 * Run the normalize options for the given [instances].
 */
@AdvancedMonktApi("This is a part of the encoding process")
suspend fun Monkt.performNormalization(
    instances: List<Any>,
    tweak: NormalizationTweak
) {
    var options = instances.flatMap {
        Document.obtainOptions<NormalizationConfiguration>(it)
    }

    options = options.filter {
        tweak.filters.all { filter ->
            filter(it)
        }
    }

    performOption(options)
}
