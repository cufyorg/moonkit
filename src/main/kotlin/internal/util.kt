package org.cufy.mangaka.internal

import org.cufy.mangaka.Constructor
import org.cufy.mangaka.Formatter
import org.cufy.mangaka.Validator

fun <D, O, T> Constructor<in D, in O, out T>.safeCast(): Constructor<D, O, T> {
    @Suppress("UNCHECKED_CAST")
    return this as Constructor<D, O, T>
}

fun <D, O, T> Formatter<in D, in O, in T>.safeCast(): Formatter<D, O, T> {
    @Suppress("UNCHECKED_CAST")
    return this as Formatter<D, O, T>
}

fun <D, O, T> Validator<in D, in O, in T>.safeCast(): Validator<D, O, T> {
    @Suppress("UNCHECKED_CAST")
    return this as Validator<D, O, T>
}
