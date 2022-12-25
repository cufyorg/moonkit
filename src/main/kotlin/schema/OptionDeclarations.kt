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

import org.cufy.bson.Pathname
import org.cufy.monkt.*
import kotlin.reflect.KProperty

/**
 * A signal that can be enqueued.
 *
 * @param O the result of the signal.
 * @author LSafer
 * @since 2.0.0
 */
interface Signal<O>

/**
 * A handler that takes multiple signals and
 * handle them on a single operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface SignalHandler {
    /**
     * Return true if this handler can handle the
     * given [signal].
     */
    @AdvancedMonktApi("Called by monkt internally")
    fun canHandle(signal: Signal<*>): Boolean

    /**
     * Respond to the given [signals].
     *
     * The returned list is the responses to the
     * given [signals]. The order of the
     * responses matches the order of the
     * signals.
     *
     * If signal 'x' is in [signals] at the index
     * 'y' then its response will be at the returned
     * list at index 'y'.
     *
     * All the given [signals] must be passed
     * to [canHandle] and returned true.
     */
    @AdvancedMonktApi("Called by monkt internally")
    suspend fun handle(signals: List<Signal<*>>): List<Any?>
}

/**
 * A property delegate for a signal result.
 *
 *
 * @param O the result of the signal.
 * @author LSafer
 * @since 2.0.0
 */
fun interface SignalProperty<O> {
    operator fun invoke(): O

    operator fun getValue(thisRef: Any?, property: KProperty<*>): O {
        return invoke()
    }

    val value: O get() = invoke()
}

/**
 * The scope an [OptionBlock] will invoke at.
 *
 * @param T the type of the instance.
 * @param C the type of the actual option scope.
 * @author LSafer
 * @since 2.0.0
 */
interface OptionScope<T : Any, M, C> {
    /**
     * The container model.
     */
    val model: Model<*>

    /**
     * The root instance.
     */
    val root: Any

    /**
     * The schema of the [value].
     */
    val schema: Schema<M>

    /**
     * The instance executing on.
     */
    val instance: T

    /**
     * The value executing on.
     */
    val value: M

    /**
     * If this is a field option, then this is the
     * path to the field.
     * If this is an object option, then this is
     * the path to the field containing this
     * object.
     *
     * The pathname starts with the name of the
     * model followed by the names of the fields
     * to this field and separated by a dot `.`
     */
    val pathname: Pathname

    /**
     * The actual option scope.
     */
    @AdvancedMonktApi("Use the extensions provided with the configuration")
    val configuration: C

    /**
     * Enqueue a signal to the root implementation
     * and return a value delegate to the response.
     *
     * The delegate will throw an exception if
     * accessed before calling [wait].
     *
     * @param signal the signal to send to the implementation.
     * @return a delegate to the response of the signal.
     * @since 2.0.0
     */
    @AdvancedMonktApi("Use the extensions provided with the signal")
    fun <I : Signal<O>, O> enqueue(signal: I): SignalProperty<O>

    /**
     * Send the [enqueued][enqueue] signals to
     * the root implementation and wait for the
     * responses.
     *
     * This might not actually wait when not
     * necessary. Invoking this function only
     * guarantees the pending signals gets
     * populated.
     */
    suspend fun wait()
}

/**
 * A code block used to define options with
 * dependencies.
 *
 * @param T the type of the instance.
 * @param M the type of the parameter.
 * @param C the type of the actual option scope.
 * @author LSafer
 * @since 2.0.0
 */
typealias OptionBlock<T, M, C> = suspend OptionScope<T, M, C>.(value: M) -> Unit

/**
 * A code block used to define options with
 * dependencies and a return value.
 *
 * @param T the type of the instance.
 * @param M the type of the parameter.
 * @param C the type of the actual option scope.
 * @param R the type of the return value.
 * @author LSafer
 * @since 2.0.0
 */
typealias ReturnOptionBlock<T, M, C, R> = suspend OptionScope<T, M, C>.(value: M) -> R

/**
 * A code block used to configure option data
 * instances.
 *
 * @param T the type of the instance. (it)
 * @param C the type of the actual option scope.
 * @author LSafer
 * @since 2.0.0
 */
typealias OptionDataBlock<T, M, C> = OptionData<T, M, C>.() -> Unit

/**
 * A code block used to configure option data
 * instances and return a value.
 *
 * @param T the type of the instance. (it)
 * @param C the type of the actual option scope.
 * @param R the type of the return value.
 * @author LSafer
 * @since 2.0.0
 */
typealias ReturnOptionDataBlock<T, M, C, R> = OptionData<T, M, C>.() -> R

/**
 * An option is an optimized way of defining schema
 * options. Instead of determining the configuration
 * all at once.
 *
 * An option block is a way to get dependency
 * data in a merged queries for all options. It
 * does that using a multi-stage option definition.
 * Each stage declares the dependencies of the next
 * stages. and the final stage being the actual
 * option definition.
 *
 * Such system is very hard to use without syntax
 * tricks. And the syntax trick used here is using
 * delegate properties. The properties are obtained
 * using [OptionScope.enqueue] and not populated
 * until [OptionScope.wait] is invoked.
 *
 * @param T the type of the instance.
 * @param M the type of the parameter.
 * @param C the type of the option configuration.
 * @author LSafer
 * @since 2.0.0
 */
class Option<T : Any, M, C>(
    /**
     * The option's configuration.
     *
     * The way of passing data between option
     * [block] and the option handler.
     *
     * First, the option handler populate it with
     * extra context data.
     * Then, the [block] populate it with the
     * option data.
     */
    val configuration: C,
    /**
     * The option's block. the script to be
     * invoked to populate [configuration].
     */
    val block: OptionBlock<T, M, C>
)

/**
 * The data necessary for invoking an option.
 *
 * @param T the type of the instance.
 * @param M the type of the parameter.
 * @param C the type of the option configuration.
 * @author LSafer
 * @since 2.0.0
 */
class OptionData<T : Any, M, C>(
    /**
     * The container model.
     */
    val model: Model<*>,
    /**
     * The root instance.
     *
     * Will be [Unit] if static.
     */
    val root: Any,
    /**
     * The path to the option.
     */
    val pathname: Pathname,
    /**
     * The schema of the [value].
     */
    val schema: Schema<M>,
    /**
     * The option's instance.
     *
     * [Unit] for static options.
     */
    val instance: T,
    /**
     * The option's value.
     *
     * [Unit] for static options.
     */
    val value: M,
    /**
     * The option's configuration.
     *
     * The way of passing data between option
     * [block] and the option handler.
     *
     * First, the option handler populate it with
     * extra context data.
     * Then, the [block] populate it with the
     * option data.
     */
    val configuration: C,
    /**
     * The option's block. the script to be
     * invoked to populate [configuration].
     */
    val block: OptionBlock<T, M, C>
)

/**
 * Construct a new option data from the given
 * arguments.
 *
 * @param model the container model.
 * @param root the root instance.
 * @param pathname the path to the option.
 * @param schema the schema for the value.
 * @param instance the option's instance.
 * @param value the option's value.
 * @param option the option object.
 */
fun <T : Any, M, C> OptionData(
    model: Model<*>,
    root: Any,
    pathname: Pathname,
    schema: Schema<M>,
    instance: T,
    value: M,
    option: Option<T, M, C>
) = OptionData(
    model,
    root,
    pathname,
    schema,
    instance,
    value,
    option.configuration,
    option.block
)

/**
 * Construct a new static option data from the
 * given arguments.
 *
 * @param model the container model.
 * @param pathname the path to the option.
 * @param option the option object.
 */
fun <C> OptionData(
    model: Model<*>,
    pathname: Pathname,
    option: Option<Unit, Unit, C>
) = OptionData(
    model,
    Unit,
    pathname,
    UnitSchema,
    Unit,
    Unit,
    option.configuration,
    option.block
)

/**
 * A shortcut function for calling [OptionScope.wait].
 *
 * @param block a block to be invoked after waiting.
 * @return the returned value from [block].
 */
suspend fun <T : Any, M, C, R> OptionScope<T, M, C>.then(
    block: suspend OptionScope<T, M, C>.() -> R
): R {
    wait()
    return block(this)
}
