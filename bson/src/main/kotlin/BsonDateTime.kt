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

import kotlinx.datetime.Instant
import java.util.*

/* ============= ------------------ ============= */

/**
 * A representation of the BSON DateTime type.
 *
 * @see org.bson.BsonDateTime
 * @since 2.0.0
 */
data class BsonDateTime(val value: Long) : BsonElement {
    override val type: BsonType get() = BsonType.DateTime

    override fun equals(other: Any?) =
        other is BsonDateTime && other.value == value

    override fun hashCode() =
        value.hashCode()

    override fun toString() =
        Date(value).toString()
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonDateTime] with the given [value].
 *
 * @since 2.0.0
 */
fun BsonDateTime(value: Date): BsonDateTime {
    return BsonDateTime(value.time)
}

/**
 * Return a [BsonDateTime] with the given [value].
 *
 * @since 2.0.0
 */
fun BsonDateTime(value: Instant): BsonDateTime {
    return BsonDateTime(value.toEpochMilliseconds())
}

/* ============= ------------------ ============= */

/**
 * Return a [BsonDateTime] with the value of this.
 */
inline val Date.bson: BsonDateTime get() = BsonDateTime(this)

/**
 * Return a [BsonDateTime] with the value of this or [BsonNull] if this is `null`.
 */
val Date?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/**
 * Return a [BsonDateTime] with the value of this.
 */
inline val Instant.bson: BsonDateTime get() = BsonDateTime(this)

/**
 * Return a [BsonDateTime] with the value of this or [BsonNull] if this is `null`.
 */
val Instant?.bson: BsonElement get() = this?.let { bson } ?: null.bson

/* ============= ------------------ ============= */

/**
 * Return a [Date] with the value of this.
 */
fun BsonDateTime.toDate(): Date {
    return Date(value)
}

/**
 * Return a [Instant] with the value of this.
 */
fun BsonDateTime.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(value)
}

/* ============= ------------------ ============= */
