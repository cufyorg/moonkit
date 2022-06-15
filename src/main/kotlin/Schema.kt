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
import org.cufy.mangaka.internal.validatorStub

// Functions

/**
 * The type of a schema constructor function.
 *
 * A constructor function is expected to receive a
 * bson value and attempt to convert it to an
 * instance of [T].
 *
 * @author LSafer
 * @since 1.0.0
 */
fun interface Constructor<D, O, T> {
    /**
     * This function is expected to receive a
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
     * @param fallback the fallback constructor.
     * @return a new instance of [T].
     * @author LSafer
     * @since 1.0.0
     */
    suspend fun SchemaScope<D, O, T>.construct(
        bson: BsonValue?,
        fallback: Constructor<D, O, T>
    ): T?

    /**
     * Invoke this constructor with the given arguments.
     *
     * @since 1.0.0
     */
    suspend operator fun invoke(
        scope: SchemaScope<D, O, T>,
        bson: BsonValue?,
        fallback: Constructor<D, O, T> = Default()
    ) = scope.construct(bson, fallback)

    companion object {
        /**
         * Obtain a constructor that always
         * returns null.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Default() =
            Constructor<D, O, T> { _, _ -> null }

        /**
         * Obtain a constructor that always
         * invokes the fallback.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Fallback() =
            Constructor<D, O, T> { p, fb -> fb(this, p) }

        /**
         * Obtain a constructor that always
         * throws an error.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Stub() =
            Constructor<D, O, T> { _, _ -> constructorStub(path) }
    }
}

/**
 * The type of a schema formatter function.
 *
 * A formatter function is expected to receive an
 * instance of [T] and attempt to obtain a bson
 * value from it.
 *
 * @author LSafer
 * @since 1.0.0
 */
fun interface Formatter<D, O, T> {
    /**
     * This function is expected to receive an
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
    suspend fun SchemaScope<D, O, T>.format(
        value: T?,
        fallback: Formatter<D, O, T>
    ): BsonValue?

    /**
     * Invoke this formatter with the given arguments.
     *
     * @since 1.0.0
     */
    suspend operator fun invoke(
        scope: SchemaScope<D, O, T>,
        value: T?,
        fallback: Formatter<D, O, T> = Default()
    ) = scope.format(value, fallback)

    companion object {
        /**
         * Obtain a formatter that always
         * returns null.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Default() =
            Formatter<D, O, T> { _, _ -> null }

        /**
         * Obtain a formatter that always
         * invokes the fallback.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Fallback() =
            Formatter<D, O, T> { p, fb -> fb(this, p) }

        /**
         * Obtain a formatter that always
         * throws an error.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Stub() =
            Formatter<D, O, T> { _, _ -> formatterStub(path) }
    }
}

/**
 * The type of a schema validator function.
 *
 * A validator function is expected to receive an
 * instance of [T] and return a list of errors
 * from validating it.
 *
 * @author LSafer
 * @since 1.0.0
 */
fun interface Validator<D, O, T> {
    /**
     * This function is expected to receive an
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
    suspend fun SchemaScope<D, O, T>.validate(
        value: T?,
        fallback: Validator<D, O, T>
    ): List<Throwable>

    /**
     * Invoke this validator with the given arguments.
     *
     * @since 1.0.0
     */
    suspend operator fun invoke(
        scope: SchemaScope<D, O, T>,
        value: T?,
        fallback: Validator<D, O, T> = Default()
    ) = scope.validate(value, fallback)

    companion object {
        /**
         * Obtain a constructor that always
         * returns an empty list.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Default() =
            Validator<D, O, T> { _, _ -> emptyList() }

        /**
         * Obtain a validator that always
         * invokes the fallback.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Fallback() =
            Validator<D, O, T> { p, fb -> fb(this, p) }

        /**
         * Obtain a validator that always
         * throws an error.
         *
         * @since 1.0.0
         */
        fun <D, O, T> Stub() =
            Validator<D, O, T> { _, _ -> validatorStub(path) }
    }
}

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
    constructor: Constructor<D, O, T> = Constructor.Stub(),
    formatter: Formatter<D, O, T> = Formatter.Stub(),
    validator: Validator<D, O, T> = Validator.Default(),
) = object : Schema<D, O, T> {
    override var constructor = constructor
    override var formatter = formatter
    override var validator = validator
}

// Setters

/**
 * Set the constructor of this schema to be the
 * given [function] overriding any constructor
 * that was applied previously.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.constructor(
    function: SchemaScope<D, O, T>.(bson: BsonValue?) -> T?
) {
    this.constructor = Constructor { bson, _ ->
        function(bson)
    }
}

/**
 * Set the formatter of this schema to be the
 * given [function] overriding any formatter that
 * was applied previously.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.formatter(
    function: SchemaScope<D, O, T>.(value: T?) -> BsonValue?
) {
    this.formatter = Formatter { value, _ ->
        function(value)
    }
}

/**
 * Set the validator of this schema to be the
 * given [function] overriding any validator that
 * was applied previously.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.validator(
    function: SchemaScope<D, O, T>.(value: T?) -> List<Throwable>
) {
    this.validator = Validator { value, _ ->
        function(value)
    }
}

// Infix

/**
 * Return a new schema from combining the receiver
 * schema with the given [other] schema.
 *
 * @since 1.0.0
 */
operator fun <D, O, T> Schema<in D, in O, T>.plus(
    other: Schema<in D, in O, T>
): Schema<D, O, T> {
    val receiver = this
    val schema = Schema<D, O, T>()
    schema.constructor = Constructor { bson, fallback ->
        receiver.constructor.safeCast()(this, bson, fallback)
            ?: other.constructor.safeCast()(this, bson, fallback)
    }
    schema.formatter = Formatter { value, fallback ->
        receiver.formatter.safeCast()(this, value, fallback)
            ?: other.formatter.safeCast()(this, value, fallback)
    }
    schema.validator = Validator { value, fallback ->
        receiver.validator.safeCast()(this, value, fallback) +
                other.validator.safeCast()(this, value, fallback)
    }
    return schema
}

/**
 * Construct a new constructor that executes
 * this constructor with the given [constructor]
 * as its fallback.
 *
 * @since 1.0.0
 */
operator fun <D, O, T> Constructor<D, O, T>.plus(
    constructor: Constructor<D, O, T>
): Constructor<D, O, T> {
    val self = this
    return Constructor { bson, fallback ->
        self(this, bson, constructor + fallback)
    }
}

/**
 * Construct a new formatter that executes
 * this formatter with the given [formatter]
 * as its fallback.
 *
 * @since 1.0.0
 */
operator fun <D, O, T> Formatter<D, O, T>.plus(
    formatter: Formatter<D, O, T>
): Formatter<D, O, T> {
    val self = this
    return Formatter { value, fallback ->
        self(this, value, formatter + fallback)
    }
}

/**
 * Construct a new validator that executes
 * this validator with the given [validator]
 * as its fallback.
 *
 * @since 1.0.0
 */
operator fun <D, O, T> Validator<D, O, T>.plus(
    validator: Validator<D, O, T>
): Validator<D, O, T> {
    val self = this
    return Validator { value, fallback ->
        self(this, value, validator + fallback)
    }
}

// Interceptors

/**
 * Apply the given constructor [interceptor] to this schema.
 *
 * @param interceptor the interceptor to be applied.
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.onConstruct(
    interceptor: Constructor<D, O, T>
) {
    this.constructor.let {
        this.constructor = interceptor + it
    }
}

/**
 * Apply the given formatter [interceptor] to this schema.
 *
 * @param interceptor the interceptor to be applied.
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.onFormat(
    interceptor: Formatter<D, O, T>
) {
    this.formatter.let {
        this.formatter = interceptor + it
    }
}

/**
 * Apply the given validator [interceptor] to this schema.
 *
 * @param interceptor the interceptor to be applied.
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.onValidate(
    interceptor: Validator<D, O, T>
) {
    this.validator.let {
        this.validator = interceptor + it
    }
}

// Almost Internal

/**
 * Cast this constructor to bounds it can accept.
 *
 * @since 1.0.0
 */
fun <D, O, T> Constructor<in D, in O, out T>.safeCast(): Constructor<D, O, T> {
    @Suppress("UNCHECKED_CAST")
    return this as Constructor<D, O, T>
}

/**
 * Cast this formatter to bounds it can accept.
 *
 * @since 1.0.0
 */
fun <D, O, T> Formatter<in D, in O, in T>.safeCast(): Formatter<D, O, T> {
    @Suppress("UNCHECKED_CAST")
    return this as Formatter<D, O, T>
}

/**
 * Cast this validator to bounds it can accept.
 *
 * @since 1.0.0
 */
fun <D, O, T> Validator<in D, in O, in T>.safeCast(): Validator<D, O, T> {
    @Suppress("UNCHECKED_CAST")
    return this as Validator<D, O, T>
}
