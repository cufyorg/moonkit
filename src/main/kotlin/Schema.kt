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
package org.cufy.mangaka

import org.bson.BsonValue
import org.cufy.mangaka.internal.constructorStub
import org.cufy.mangaka.internal.formatterStub

// Functions

/**
 * The type of a schema constructor function.
 *
 * A constructor function is expected to receive a
 * bson value and attempt to convert it to an
 * instance of [T].
 *
 * Unless critical, this function is not
 * expected to throw an error because of the
 * type or state of the given [bson] value.
 *
 * If the given [bson] is null, this means
 * that the value is not present and a default
 * value might be returned. It does not
 * explicitly mean to return null.
 *
 * If returned null, this means the constructor
 * failed to construct the value. Or the
 * constructor is explicitly ignoring the value.
 *
 * @receiver the execution scope.
 * @param bson the bson value.
 * @return a new instance of [T].
 * @author LSafer
 * @since 1.0.0
 */
@Suppress("KDocUnresolvedReference")
typealias Constructor<D, O, T> =
        suspend SchemaScope<D, O, T>.(bson: BsonValue?) -> T?

/**
 * The type of a schema formatter function.
 *
 * A formatter function is expected to receive an
 * instance of [T] and attempt to obtain a bson
 * value from it.
 *
 * Unless critical, this function is not
 * expected to throw an error because of the
 * type or state of the given [value].
 *
 * If the given [value] is null, this means
 * that the value is not present and a default
 * value might be returned. It does not
 * explicitly mean to return null.
 *
 * If returned null, this means the formatter
 * failed to format the value. Or the formatter
 * is explicitly ignoring the value.
 *
 * @receiver the execution scope.
 * @param value the instance of [T].
 * @return a bson value.
 * @author LSafer
 * @since 1.0.0
 */
@Suppress("KDocUnresolvedReference")
typealias Formatter<D, O, T> =
        suspend SchemaScope<D, O, T>.(value: T?) -> BsonValue?

/**
 * The type of a schema validator function.
 *
 * A validator function is expected to receive an
 * instance of [T] and return a list of errors
 * from validating it.
 *
 * Unless critical, this function is not
 * expected to throw an error because of the
 * type or state of the given [value].
 *
 * If the given [value] is null, this means
 * that the value is not present. It does not
 * explicitly mean to return an empty list.
 *
 * If returned an empty list, this means the
 * given [value] has passed this validator.
 *
 * @receiver the execution scope.
 * @param value an instance of [T].
 * @return the validation errors.
 * @author LSafer
 * @since 1.0.0
 */
@Suppress("KDocUnresolvedReference")
typealias Validator<D, O, T> =
        suspend SchemaScope<D, O, T>.(value: T?) -> List<Throwable>

// Classes

/**
 * An execution scope for executing functions of
 * the schema type [ObjectSchema].
 *
 * @param T the type of instances the functions of
 *          the schema operates with.
 * @author LSafer
 * @since 1.0.0
 */
interface SchemaScope<D, O, T> {
    /**
     * The field name. (if this is a schema of a field)
     *
     * @since 1.0.0
     */
    val name: String

    /**
     * The path to where the target value can be
     * reached in the document it is stored at.
     *
     * @since 1.0.0
     */
    val path: String

    /**
     * The model of the root document.
     *
     * @since 1.0.0
     */
    val model: Model<D & Any>

    /**
     * The hosting object.
     *
     * @since 1.0.0
     */
    val self: O

    /**
     * The root document.
     *
     * @since 1.0.0
     */
    val document: D?
}

/**
 * A schema is a mutable instance containing basic
 * functions required to construct (deserialize
 * from bson), format (serialize to bson) and
 * validate some instance of type [T].
 * The functions of the schema executes in a
 * disposable instance scope [SchemaScope] created
 * somewhere before the execution and disposed
 * sometime later after the execution.
 *
 * @param D the type of the root document the
 *          schema is operating on.
 * @param O if this schema is a field schema, the
 *          type of the containing object.
 *          Otherwise, it should be `Unit`.
 * @param T the type of instances the functions of
 *          the schema operates with.
 * @author LSafer
 * @since 1.0.0
 */
interface Schema<D, O, T> {
    /**
     * The constructor function of the schema.
     *
     * This function is used to construct a new
     * instance of [T] from a bson value.
     *
     * @since 1.0.0
     */
    var constructor: Constructor<D, O, T>

    /**
     * The formatter function of the schema.
     *
     * This function is used to obtain a bson
     * value from an instance of [T].
     *
     * @since 1.0.0
     */
    var formatter: Formatter<D, O, T>

    /**
     * The validator function of the schema.
     *
     * This function is used to validate an
     * instance of [T].
     *
     * @since 1.0.0
     */
    var validator: Validator<D, O, T>
}

// Constructors

/**
 * Construct a new [SchemaScope].
 *
 * @param path the path to be set.
 * @return a new schema scope.
 * @since 1.0.0
 */
fun <D, O, T> SchemaScope(
    name: String = "",
    path: String = "",
    model: Model<D & Any>,
    document: D? = null,
    self: O
) = object : SchemaScope<D, O, T> {
    override val name = name
    override val path = path
    override val model = model
    override val self = self
    override val document = document
}

/**
 * Construct a new [ObjectSchema].
 *
 * @param constructor the constructor to be set.
 * @param formatter the formatter to be set.
 * @param validator the validator to be set.
 * @return a new schema.
 * @since 1.0.0
 */
fun <D, O, T> Schema(
    constructor: Constructor<D, O, T> = { constructorStub(path) },
    formatter: Formatter<D, O, T> = { formatterStub(path) },
    validator: Validator<D, O, T> = { emptyList() },
) = object : Schema<D, O, T> {
    override var constructor = constructor
    override var formatter = formatter
    override var validator = validator
}

// Interceptors

/**
 * Apply the given [interceptor] to this schema.
 *
 * @param interceptor the interceptor to be applied.
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.onConstruct(
    interceptor: suspend SchemaScope<D, O, T>.(
        bson: BsonValue?,
        constructor: Constructor<D, O, T>
    ) -> T?
) {
    this.constructor.let { constructor ->
        this.constructor = { bson ->
            interceptor(bson, constructor)
        }
    }
}

/**
 * Apply the given [interceptor] to this schema.
 *
 * @param interceptor the interceptor to be applied.
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.onFormat(
    interceptor: suspend SchemaScope<D, O, T>.(
        value: T?,
        formatter: Formatter<D, O, T>
    ) -> BsonValue?
) {
    this.formatter.let { formatter ->
        this.formatter = { document ->
            interceptor(document, formatter)
        }
    }
}

/**
 * Apply the given [interceptor] to this schema.
 *
 * @param interceptor the interceptor to be applied.
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.onValidate(
    interceptor: suspend SchemaScope<D, O, T>.(
        value: T?,
        validator: Validator<D, O, T>
    ) -> List<Throwable>
) {
    this.validator.let { validator ->
        this.validator = { document ->
            interceptor(document, validator)
        }
    }
}
