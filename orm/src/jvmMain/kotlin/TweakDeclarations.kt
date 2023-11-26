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
package org.cufy.monkt

import org.cufy.mongodb.*
import org.cufy.monkt.schema.extension.*

/* ============= --- InitTweak  --- ============= */

/**
 * A class to tweak the monkt init operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class InitTweak {
    /**
     * The tweak for the initialization options
     * performance.
     */
    var init: InitializationTweak = InitializationTweak()

    /**
     * The tweak for the indexes options
     * performance.
     */
    var indexes: IndexesTweak = IndexesTweak()

    /**
     * The mongodb create index operation options.
     */
    var indexOptions: CreateIndexesOptions = CreateIndexesOptions()
}

/* ============= -- DecodeTweak  -- ============= */

/**
 * A class to tweak the decoding operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class DecodeTweak {
    /**
     * The value to be set to the `isNew` property
     * of the decoded instance.
     */
    var isNew: Boolean = true

    /**
     * The value to be set to the `isDeleted`
     * property of the decoded instance.
     */
    var isDeleted: Boolean = false

    /**
     * The tweak for the initialization options
     * performance.
     */
    var init: InitializationTweak = InitializationTweak()

    /**
     * The tweak for the migration options
     * performance.
     */
    var migration: MigrationTweak = MigrationTweak()
}

/* ============= -- EncodeTweak  -- ============= */

/**
 * A class to tweak the encoding operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class EncodeTweak {
    /**
     * The tweak for the normalization options
     * performance.
     */
    var normalization: NormalizationTweak = NormalizationTweak()

    /**
     * The tweak for the validation options
     * performance.
     */
    var validation: ValidationTweak = ValidationTweak()
}

/* ============= --- FindTweak  --- ============= */

/**
 * A class to tweak the behaviour of a find
 * operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class FindTweak {
    /**
     * The mongodb find operation options.
     */
    val options: FindOptions = FindOptions()

    /**
     * The tweak for the decoding operation.
     */
    var decode: DecodeTweak = DecodeTweak()

    init {
        decode.isNew = false
    }
}

/* ============= --- SaveTweak  --- ============= */

/**
 * A class to tweak the behaviour of a save
 * operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
class SaveTweak {
    /**
     * The tweak for the encoding operation.
     */
    var encode: EncodeTweak = EncodeTweak()

    /**
     * The tweak for the writes options
     * performance.
     */
    var writes: WritesTweak = WritesTweak()

    /**
     * The mongodb bulkWrite operation options.
     */
    var bulkWriteOptions: BulkWriteOptions = BulkWriteOptions()

    /**
     * The mongodb update operation options.
     */
    var options: UpdateOptions = UpdateOptions()

    init {
        options.upsert = true
    }
}

/* ============= -- CreateTweak  -- ============= */

/**
 * A class to tweak the behaviour of a create
 * operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
class CreateTweak {
    /**
     * The tweak for the decoding operation.
     */
    var decode: DecodeTweak = DecodeTweak()

    /**
     * The tweak for the save operation.
     */
    var save: SaveTweak = SaveTweak()
}

/* ============= -- DeleteTweak  -- ============= */

/**
 * A class to tweak the behaviour of a delete
 * operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
class DeleteTweak {
    /**
     * The mongodb delete operation options.
     */
    var options: DeleteOptions = DeleteOptions()

    /**
     * The tweak for the deletion options
     * performance.
     */
    var deletion: DeletionTweak = DeletionTweak()
}

/* ============= -- DeleteTweak  -- ============= */

/**
 * A class to tweak the behaviour of a find and
 * delete operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
class FindAndDeleteTweak {
    /**
     * The tweak for the find operation.
     */
    var find: FindTweak = FindTweak()

    /**
     * The tweak for the delete operation.
     */
    var delete: DeleteTweak = DeleteTweak()
}

/* ============= - AggregateTweak - ============= */

/**
 * A class to tweak the behaviour of an aggregate
 * operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
class AggregateTweak {
    /**
     * The mongodb aggregation operation options.
     */
    var options: AggregateOptions = AggregateOptions()

    /**
     * The tweak for the decoding operation.
     */
    var decode: DecodeTweak = DecodeTweak()

    init {
        decode.isNew = false
    }
}
