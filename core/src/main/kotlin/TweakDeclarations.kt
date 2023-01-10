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

import com.mongodb.client.model.*
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
    @AdvancedMonktApi("Use `init()` instead")
    var initializationTweak: InitializationTweak = InitializationTweak()

    /**
     * The tweak for the indexes options
     * performance.
     */
    @AdvancedMonktApi("Use `indexes()` instead")
    var indexesTweak: IndexesTweak = IndexesTweak()

    /**
     * The mongodb create index operation options.
     */
    @AdvancedMonktApi("Use `indexOptions()` instead")
    var indexOptions: CreateIndexOptions = CreateIndexOptions()
}

/**
 * Configure the initialization options
 * performance with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun InitTweak.init(block: InitializationTweak.() -> Unit) {
    initializationTweak.apply(block)
}

/**
 * Configure the indexes options
 * performance with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun InitTweak.indexes(block: IndexesTweak.() -> Unit) {
    indexesTweak.apply(block)
}

/**
 * Configure the mongodb create index operation
 * options with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun InitTweak.indexOptions(block: CreateIndexOptionsScope.() -> Unit) {
    indexOptions = indexOptions.configure(block)
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
    @AdvancedMonktApi("Use `init()` instead")
    var initializationTweak: InitializationTweak = InitializationTweak()

    /**
     * The tweak for the migration options
     * performance.
     */
    @AdvancedMonktApi("Use `migration()` instead")
    var migrationTweak: MigrationTweak = MigrationTweak()
}

/**
 * Configure the initialization options
 * performance with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun DecodeTweak.init(block: InitializationTweak.() -> Unit) {
    initializationTweak.apply(block)
}

/**
 * Configure the migration options performance
 * with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun DecodeTweak.migration(block: MigrationTweak.() -> Unit) {
    migrationTweak.apply(block)
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
    @AdvancedMonktApi("Use `normalization()` instead")
    var normalizationTweak: NormalizationTweak = NormalizationTweak()

    /**
     * The tweak for the validation options
     * performance.
     */
    @AdvancedMonktApi("Use `validation()` instead")
    var validationTweak: ValidationTweak = ValidationTweak()
}

/**
 * Configure the normalization options performance
 * with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun EncodeTweak.normalization(block: NormalizationTweak.() -> Unit) {
    normalizationTweak.apply(block)
}

/**
 * Configure the validation options performance
 * with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun EncodeTweak.validation(block: ValidationTweak.() -> Unit) {
    validationTweak.apply(block)
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
     * A block to configure the find publisher
     * before being used.
     */
    @AdvancedMonktApi("Use `op()` instead")
    var publisherBlock: FindPublisherScope.() -> Unit = {}

    /**
     * The tweak for the decoding operation.
     */
    @AdvancedMonktApi("Use `decode()` instead")
    var decodeTweak: DecodeTweak = DecodeTweak()

    init {
        decode { isNew = false }
    }
}

/**
 * Add the given [block] to configure the find
 * publisher before being used.
 */
@OptIn(AdvancedMonktApi::class)
fun FindTweak.op(block: FindPublisherScope.() -> Unit) {
    publisherBlock.let { current ->
        publisherBlock = {
            current()
            block()
        }
    }
}

/**
 * Configure the instance decoding with the given
 * [block].
 */
@OptIn(AdvancedMonktApi::class)
fun FindTweak.decode(block: DecodeTweak.() -> Unit) {
    decodeTweak.apply(block)
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
    @AdvancedMonktApi("Use `encode()` instead")
    var encodeTweak: EncodeTweak = EncodeTweak()

    /**
     * The tweak for the writes options
     * performance.
     */
    @AdvancedMonktApi("Use `writes()` instead")
    var writesTweak: WritesTweak = WritesTweak()

    /**
     * The mongodb bulkWrite operation options.
     */
    @AdvancedMonktApi("Use `bulkOptions()` instead")
    var bulkWriteOptions: BulkWriteOptions = BulkWriteOptions()

    /**
     * The mongodb update operation options.
     */
    @AdvancedMonktApi("Use `options()` instead")
    var updateOptions: UpdateOptions = UpdateOptions()

    init {
        options { upsert = true }
    }
}

/**
 * Configure the instance encoding with the given
 * [block].
 */
@OptIn(AdvancedMonktApi::class)
fun SaveTweak.encode(block: EncodeTweak.() -> Unit) {
    encodeTweak.apply(block)
}

/**
 * Configure the writes options performance with
 * the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun SaveTweak.writes(block: WritesTweak.() -> Unit) {
    writesTweak.apply(block)
}

/**
 * Configure the mongodb bulkWrite operation
 * options with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun SaveTweak.bulkOptions(block: BulkWriteOptionsScope.() -> Unit) {
    bulkWriteOptions = bulkWriteOptions.configure(block)
}

/**
 * Configure the mongodb update operation options
 * with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun SaveTweak.options(block: UpdateOptionsScope.() -> Unit) {
    updateOptions = updateOptions.configure(block)
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
    @AdvancedMonktApi("Use `decode()` instead")
    var decodeTweak: DecodeTweak = DecodeTweak()

    /**
     * The tweak for the save operation.
     */
    @AdvancedMonktApi("Use delegate functions instead")
    var saveTweak: SaveTweak = SaveTweak()
}

/**
 * Configure the instance decoding with the given
 * [block].
 */
@OptIn(AdvancedMonktApi::class)
fun CreateTweak.decode(block: DecodeTweak.() -> Unit) {
    decodeTweak.apply(block)
}

/**
 * Configure the tweak for the save operation with
 * the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun CreateTweak.save(block: SaveTweak.() -> Unit) {
    saveTweak.apply(block)
}

/**
 * Configure the instance encoding with the given
 * [block].
 */
fun CreateTweak.encode(block: EncodeTweak.() -> Unit) {
    save { encode(block) }
}

/**
 * Configure the writes options performance with
 * the given [block].
 */
fun CreateTweak.writes(block: WritesTweak.() -> Unit) {
    save { writes(block) }
}

/**
 * Configure the mongodb bulkWrite operation
 * options with the given [block].
 */
fun CreateTweak.bulkOptions(block: BulkWriteOptionsScope.() -> Unit) {
    save { bulkOptions(block) }
}

/**
 * Configure the mongodb update operation options
 * with the given [block].
 */
fun CreateTweak.options(block: UpdateOptionsScope.() -> Unit) {
    save { options(block) }
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
    @AdvancedMonktApi("Use `options()` instead")
    var deleteOptions: DeleteOptions = DeleteOptions()

    /**
     * The tweak for the deletion options
     * performance.
     */
    @AdvancedMonktApi("Use `deletion()` instead")
    var deletionTweak: DeletionTweak = DeletionTweak()
}

/**
 * Configure the mongodb delete operation options
 * with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun DeleteTweak.options(block: DeleteOptionsScope.() -> Unit) {
    deleteOptions = deleteOptions.configure(block)
}

/**
 * Configure the deletion options performance with
 * the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun DeleteTweak.deletion(block: DeletionTweak.() -> Unit) {
    deletionTweak.apply(block)
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
    @AdvancedMonktApi("Use delegate functions instead")
    var findTweak: FindTweak = FindTweak()

    /**
     * The tweak for the delete operation.
     */
    @AdvancedMonktApi("Use delegate functions instead")
    var deleteTweak: DeleteTweak = DeleteTweak()
}

/**
 * Configure the tweak for the find operation with
 * the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun FindAndDeleteTweak.find(block: FindTweak.() -> Unit) {
    findTweak.apply(block)
}

/**
 * Add the given [block] to configure the find
 * publisher before being used.
 */
fun FindAndDeleteTweak.op(block: FindPublisherScope.() -> Unit) {
    find { op(block) }
}

/**
 * Configure the instance decoding with the given
 * [block].
 */
fun FindAndDeleteTweak.decode(block: DecodeTweak.() -> Unit) {
    find { decode(block) }
}

/**
 * Configure the tweak for the delete operation
 * with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun FindAndDeleteTweak.delete(block: DeleteTweak.() -> Unit) {
    deleteTweak.apply(block)
}

/**
 * Configure the mongodb delete operation options
 * with the given [block].
 */
fun FindAndDeleteTweak.options(block: DeleteOptionsScope.() -> Unit) {
    delete { options(block) }
}

/**
 * Configure the deletion options performance with
 * the given [block].
 */
fun FindAndDeleteTweak.deletion(block: DeletionTweak.() -> Unit) {
    delete { deletion(block) }
}
