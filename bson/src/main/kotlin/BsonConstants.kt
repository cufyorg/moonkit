/*
 *	Copyright 2022-2023 cufy.org and meemer.com
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

/* ============= ------------------ ============= */

/**
 * A representation of the BSON Boolean type.
 *
 * @see org.bson.BsonBoolean
 * @since 2.0.0
 */
sealed interface BsonBoolean : BsonElement {
    override val type: BsonType get() = BsonType.Boolean

    val value: Boolean

    object True : BsonBoolean {
        override val value = true

        override fun hashCode() = 1
        override fun toString() = "true"
    }

    object False : BsonBoolean {
        override val value = false

        override fun hashCode() = 0
        override fun toString() = "false"
    }
}

/**
 * A representation of the BSON Null type.
 *
 * @see org.bson.BsonNull
 * @since 2.0.0
 */
object BsonNull : BsonElement {
    override val type: BsonType get() = BsonType.Null

    override fun hashCode() = 0
    override fun toString() = "null"
}

/**
 * Represents the value associated with the BSON Undefined type.
 *
 * @see org.bson.BsonUndefined
 * @since 2.0.0
 */
object BsonUndefined : BsonElement {
    override val type: BsonType get() = BsonType.Undefined

    override fun hashCode() = 0
    override fun toString() = "undefined"
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonBoolean] representing the given [value].
 *
 * @see org.bson.BsonBoolean
 * @since 2.0.0
 */
fun BsonBoolean(value: Boolean): BsonBoolean {
    return if (value) BsonBoolean.True else BsonBoolean.False
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonBoolean] with the value of this.
 */
inline val Boolean.bson: BsonBoolean get() = BsonBoolean(this)

/**
 * Return a [BsonBoolean] with the value of this or [BsonNull] if this is `null`.
 */
val Boolean?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return [BsonNull].
 *
 * Usage:
 * ```kotlin
 * null.bson
 * ```
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Nothing?.bson: BsonNull get() = BsonNull

/**
 * Return [BsonUndefined].
 *
 * Usage:
 * ```kotlin
 * Unit.bson
 * ```
 *
 * @since 2.0.0
 */
@Suppress("UnusedReceiverParameter")
inline val Unit.bson: BsonUndefined get() = BsonUndefined

/**
 * Return a [BsonBoolean] with the value of this.
 */
@Deprecated("Use .bson extension instead", ReplaceWith("bson"))
val Boolean.b: BsonBoolean get() = bson

/* ============= ------------------ ============= */

/**
 * The global instance of bson true.
 */
@Deprecated(
    "Use .bson extension instead", ReplaceWith(
        "true.bson",
        "org.cufy.bson.bson"
    )
)
@BsonKeywordMarker
val btrue = BsonBoolean.True

/**
 * The global instance of bson false.
 */
@Deprecated(
    "Use .bson extension instead", ReplaceWith(
        "false.bson",
        "org.cufy.bson.bson"
    )
)
@BsonKeywordMarker
val bfalse = BsonBoolean.False

/**
 * The global instance of bson null.
 */
@Deprecated(
    "Use .bson extension instead", ReplaceWith(
        "null.bson",
        "org.cufy.bson.bson"
    )
)
@BsonKeywordMarker
val bnull = BsonNull

/**
 * A global instance of bson undefined.
 */
@Deprecated(
    "Use Unit.bson instead", ReplaceWith(
        "Unit.bson",
        "org.cufy.bson.bson"
    )
)
@BsonKeywordMarker
val bundefined = BsonUndefined

/* ============= ------------------ ============= */
