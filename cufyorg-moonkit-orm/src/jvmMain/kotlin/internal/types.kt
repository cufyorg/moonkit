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
package org.cufy.monkt.internal

import org.cufy.monkt.schema.Signal
import org.cufy.monkt.schema.SignalHandler

/**
 * An option invocation is a wrapper over an
 * executing option code that pauses when it needs
 * some signals been responded at.
 *
 * This is a very stateful instance, and it mimics
 * the behaviour of [Iterator].
 *
 * The invocation doesn't start until the first
 * [OptionInvocation.next] is invoked.
 */
internal interface OptionInvocation<T, C> {
    /**
     * Returns true, if there is more code to be
     * resolved.
     *
     * This function is expected to be pure.
     */
    suspend fun hasNext(): Boolean

    /**
     * Invoke the next option code with the
     * responses of the previous signals until the
     * responses of the next signals is required.
     * Then, return those signals.
     *
     * @param items the responses of the signals
     *               of the previous invocation.
     * @return the signals for the next invocation.
     */
    suspend fun next(items: List<Any?>): List<Signal<*>>
}

internal interface OptionsInvocation {
    suspend fun hasNext(): Boolean

    suspend fun next(handlers: List<SignalHandler>)
}
