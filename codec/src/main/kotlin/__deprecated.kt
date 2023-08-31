package org.cufy.codec

import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.BsonElement
import org.cufy.bson.BsonString
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.reflect

/* ============= ------------------ ============= */

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
@Deprecated(
    "Codecs.String instead",
    ReplaceWith("Codecs.String", "org.cufy.codec.Codecs")
)
inline val `bson string` get() = BsonStringCodec

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
@Deprecated(
    "Codecs.String.Nullable instead",
    ReplaceWith("Codecs.String", "org.cufy.codec.Codecs")
)
inline val `bson nullable string` get() = BsonStringCodec.Nullable

/**
 * The codec for [String] and [BsonString].
 *
 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
@Deprecated(
    "Codecs.String.Array instead",
    ReplaceWith("Codecs.String", "org.cufy.codec.Codecs")
)
inline val `bson string array` get() = BsonStringCodec.Array

/* ============= ------------------ ============= */

/**
 * Invoke the given [block] with the [Codecs] companion object.
 */
@Suppress("FunctionName")
@Deprecated("Use `Codecs` directly", ReplaceWith("block(Codecs)"))
inline fun <I, O> Codecs(block: Codecs.() -> Codec<I, O>): Codec<I, O> {
    return block(Codecs)
}

/* ============= ------------------ ============= */

/**
 * An interface to implement a codec by delegating
 * to another codec.
 *
 * @author LSafer
 * @since 2.0.0
 */
@Deprecated("Use `CodecClass<I, O>({})` or `: Codec<I, O> by codec`")
interface CodecOf<I, O> : Codec<I, O> {
    /**
     * The delegated codec.
     *
     * @since 2.0.0
     */
    val codec: Codec<I, O>

    override fun encode(value: Any?) = codec.encode(value)

    override fun decode(value: Any?) = codec.decode(value)
}

@Suppress("DEPRECATION")
@Deprecated("Use DocumentCodec instead")
typealias DocumentCodecOf<I> = CodecOf<I, BsonDocument>

@Suppress("DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION", "DeprecatedCallableAddReplaceWith")
@Deprecated("DocumentCodecOf is deprecated")
operator fun <I> DocumentCodecOf<I>.invoke(block: BsonDocumentBlock): I {
    return decode(BsonDocument(block), this)
}

/* ============= ------------------ ============= */

/**
 * An interface simplifying projection
 * implementations.
 *
 * @author LSafer
 * @since 2.0.0
 */
@Deprecated("Projection of elements other than Document is not worth the generalization")
interface Projection<O> {
    /**
     * The projected element.
     */
    val element: O
}

@Suppress("DEPRECATION")
@Deprecated("Projection of elements other than Document is not worth the generalization")
typealias DocumentProjection = Projection<BsonDocument>

/**
 * Get the value of the field with the name of the
 * given [codec] and decode it using the given [codec].
 */
@Suppress("TYPEALIAS_EXPANSION_DEPRECATION", "DeprecatedCallableAddReplaceWith", "DEPRECATION")
@Deprecated("DocumentProjection is deprecated")
operator fun <I, O : BsonElement> DocumentProjection.get(codec: FieldCodec<I, O>): I {
    return element[codec]
}

/**
 * A codec implementation of [Projection].
 *
 * @author LSafer
 * @since 2.0.0
 */
@Suppress("DEPRECATION")
@Deprecated("Automatically creating a Projection Codec is not reliable")
class ProjectionCodec<I : Projection<O>, O>(
    val constructor: (O) -> I,
) : Codec<I, O> {
    private lateinit var inT: KType
    private lateinit var outT: KType

    init {
        @OptIn(ExperimentalReflectionOnLambdas::class)
        val reflection = constructor.reflect()
            ?: error("Cannot reflect the constructor")

        inT = reflection.returnType
        outT = reflection.parameters.single().type
    }

    override fun encode(value: Any?): Result<O> {
        return inT.safeCast<I>(value).mapCatching {
            it.element
        }
    }

    override fun decode(value: Any?): Result<I> {
        return outT.safeCast<O>(value).mapCatching {
            constructor(it)
        }
    }

    private fun <T> KType.safeCast(value: Any?): Result<T> {
        @Suppress("UNCHECKED_CAST")
        return when (value) {
            null -> when {
                isMarkedNullable -> Result.success(null as T)
                else -> Result.failure(
                    CodecException(
                        "Cannot encode/decode null; expected $this"
                    )
                )
            }

            else -> when {
                jvmErasure.isInstance(value) -> Result.success(value as T)
                else -> Result.failure(
                    CodecException(
                        "Cannot encode/decode ${value::class}; expected $this"
                    )
                )
            }
        }
    }
}

/**
 * A helper function to create a codec from a projection constructor.
 */
@Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
@Deprecated("Automatically creating a Projection Codec is not reliable")
operator fun <I : Projection<O>, O> ((O) -> I).getValue(t: Any?, p: KProperty<*>) =
    ProjectionCodec(this)

/* ============= ------------------ ============= */
