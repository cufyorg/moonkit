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
 * Options to tweak the behaviour of validation
 * performance.
 */
class ValidationTweak :
    WithFiltersTweak<ValidationConfiguration> {

    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    override val filters: MutableList<ReturnOptionDataBlock<*, *, ValidationConfiguration, Boolean>> =
        mutableListOf()
}

/**
 * The validation option configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ValidationConfiguration {
    /**
     * The validation errors so far.
     */
    val errors: MutableList<Throwable> = mutableListOf()
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * Add the given validation [error].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, ValidationConfiguration>.error(
    error: Throwable
) {
    configuration.errors += error
}

// builder

/**
 * Add the given validation [block].
 *
 * @param block the validation block.
 * @return 2.0.0
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.validation(
    block: OptionBlock<T, M, ValidationConfiguration>
) {
    val configuration = ValidationConfiguration()
    option(configuration, block)
}

// monkt

/**
 * Run the validation options for the given [instances].
 *
 * @return the validation errors.
 */
@AdvancedMonktApi("This is a part of the validation process")
suspend fun Monkt.performValidation(
    instances: List<Any>,
    tweak: ValidationTweak
): List<Throwable> {
    var options = instances.flatMap {
        Document.obtainOptions<ValidationConfiguration>(it)
    }

    options = options.filter {
        tweak.filters.all { filter ->
            filter(it)
        }
    }

    performOption(options)

    return options.flatMap { it.configuration.errors }
}
