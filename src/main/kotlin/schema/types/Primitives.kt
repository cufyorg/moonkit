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
package org.cufy.mangaka.schema.types

import org.bson.*
import org.bson.types.ObjectId
import org.cufy.mangaka.Id
import org.cufy.mangaka.schema.ScalarSchema
import org.cufy.mangaka.schema.deserialize
import org.cufy.mangaka.schema.serialize

/**
 * The schema for [String] and [BsonString].
 *
 * @since 1.0.0
 */
val StringSchema = ScalarSchema {
    serialize { BsonString(it) }
    deserialize {
        when (it) {
            is BsonString -> it.value
            else -> ""
        }
    }
}

/**
 * A schema for [Boolean] and [BsonBoolean].
 *
 * @since 1.0.0
 */
val BooleanSchema = ScalarSchema {
    serialize { BsonBoolean(it) }
    deserialize {
        when (it) {
            is BsonBoolean -> it.value
            else -> false
        }
    }
}

/**
 * A schema for [Int] and [BsonInt32].
 *
 * @since 1.0.0
 */
val Int32Schema = ScalarSchema {
    serialize { BsonInt32(it) }
    deserialize {
        when (it) {
            is BsonInt32 -> it.value
            else -> 0
        }
    }
}

/**
 * A schema for [Long] and [BsonInt64].
 *
 * @since 1.0.0
 */
val Int64Schema = ScalarSchema {
    serialize { BsonInt64(it) }
    deserialize {
        when (it) {
            is BsonInt64 -> it.value
            else -> 0L
        }
    }
}

/**
 * A schema for [Double] and [BsonDouble].
 *
 * @since 1.0.0
 */
val DoubleSchema = ScalarSchema {
    serialize { BsonDouble(it) }
    deserialize {
        when (it) {
            is BsonDouble -> it.value
            else -> 0.0
        }
    }
}

/**
 * A schema for [ObjectId] and [BsonObjectId].
 *
 * @since 1.0.0
 */
val ObjectIdSchema = ScalarSchema {
    serialize { BsonObjectId(it) }
    deserialize {
        when (it) {
            is BsonObjectId -> it.value
            else -> ObjectId()
        }
    }
}

/**
 * A schema for [Id] and both [BsonObjectId] and [BsonString].
 *
 * @since 1.0.0
 */
fun <T> IdSchema() = ScalarSchema<Id<T>> {
    serialize {
        when {
            ObjectId.isValid(it.value) ->
                BsonObjectId(ObjectId(it.value))
            else -> BsonString(it.value)
        }
    }
    deserialize {
        when (it) {
            is BsonObjectId -> Id(it.value)
            is BsonString -> Id(it.value)
            else -> Id()
        }
    }
}
