/*
 *	Copyright 2022-2023 cufy.org
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
package org.cufy.mongodb

import org.cufy.bson.BsonArray
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonElement
import kotlin.time.Duration

/* ============= ------------------ ============= */

/**
 * An enumeration of cursor types.
 *
 * @see com.mongodb.CursorType
 * @since 2.0.0
 */
enum class CursorType {
    /**
     * A non-tailable cursor. This is sufficient
     * for a vast majority of uses.
     *
     * @see com.mongodb.CursorType.NonTailable
     * @since 2.0.0
     */
    NonTailable,

    /**
     * Tailable means the cursor is not closed
     * when the last data is retrieved. Rather,
     * the cursor marks the final object's
     * position. You can resume using the cursor
     * later, from where it was located, if more
     * data were received. Like any "latent cursor",
     * the cursor may become invalid at some point -
     * for example if the final object it
     * references were deleted.
     *
     * @see com.mongodb.CursorType.Tailable
     * @since 2.0.0
     */
    Tailable,

    /**
     * A tailable cursor with a built-in server
     * sleep before returning an empty batch. In
     * most cases this is preferred type of
     * tailable cursor, as it is less resource
     * intensive.
     *
     * @see com.mongodb.CursorType.TailableAwait
     * @since 2.0.0
     */
    TailableAwait
}

/**
 * Indicates which document to return, the
 * original document before change or the document
 * after the change
 *
 * @see com.mongodb.client.model.ReturnDocument
 * @since 2.0.0
 */
enum class ReturnDocument {
    /**
     * Indicates to return the document before the
     * update, replacement, or insert occurred.
     *
     * @see com.mongodb.client.model.ReturnDocument.BEFORE
     * @since 2.0.0
     */
    Before,

    /**
     * Indicates to return the document after the
     * update, replacement, or insert occurred.
     *
     * @see com.mongodb.client.model.ReturnDocument.AFTER
     * @since 2.0.0
     */
    After
}

/**
 * Determines what to return for update operations
 * when using a Change Stream.
 *
 * @see com.mongodb.client.model.changestream.FullDocument
 * @since 2.0.0
 */
enum class FullDocument {
    /**
     * Returns the servers default value in the `fullDocument` field.
     *
     * @see com.mongodb.client.model.changestream.FullDocument.DEFAULT
     * @since 2.0.0
     */
    Default,

    /**
     * When set, the change stream for partial
     * updates will include both a delta
     * describing the changes to the document as
     * well as a copy of the entire document that
     * was changed from __some time__ after the
     * change occurred.
     *
     * @see com.mongodb.client.model.changestream.FullDocument.DEFAULT
     * @since 2.0.0
     */
    UpdateLookup,

    /**
     * Configures the change stream to return the
     * post-image of the modified document for
     * replace and update change events, if it is
     * available.
     *
     * @see com.mongodb.client.model.changestream.FullDocument.WHEN_AVAILABLE
     * @since 2.0.0
     */
    WhenAvailable,

    /**
     * The same behavior as [WhenAvailable] except
     * that an error is raised if the post-image
     * is not available.
     *
     * @see com.mongodb.client.model.changestream.FullDocument.REQUIRED
     * @since 2.0.0
     */
    Required
}

/**
 * A commit quorum specifies how many data-bearing
 * members of a replica set, including the primary,
 * must complete the index builds successfully
 * before the primary marks the indexes as ready.
 *
 * @param value either the mode or the `w` value.
 * @see com.mongodb.CreateIndexCommitQuorum
 * @since 2.0.0
 */
data class CommitQuorum(val value: Any) {
    companion object {
        /**
         * A create index commit quorum of majority.
         *
         * @see com.mongodb.CreateIndexCommitQuorum.MAJORITY
         * @since 2.0.0
         */
        val Majority = CommitQuorum("majority")

        /**
         * A create index commit quorum of voting members.
         *
         * @see com.mongodb.CreateIndexCommitQuorum.VOTING_MEMBERS
         * @since 2.0.0
         */
        val VotingMembers = CommitQuorum("votingMembers")
    }
}

/* ============= ------------------ ============= */

/**
 * A MongoDB namespace, which includes a database
 * name and collection name.
 *
 * @see com.mongodb.MongoNamespace
 * @since 2.0.0
 */
data class MongoNamespace(
    /**
     * The database name.
     *
     * @see com.mongodb.MongoNamespace.getDatabaseName
     * @since 2.0.0
     */
    val database: String,
    /**
     * Gets the collection name.
     *
     * @see com.mongodb.MongoNamespace.getCollectionName
     * @since 2.0.0
     */
    val collection: String,
) {
    override fun toString() =
        "$database.$collection"
}

/**
 * A model describing the creation of a single index.
 *
 * @see com.mongodb.client.model.IndexModel
 * @since 2.0.0
 */
data class CreateIndexModel(
    /**
     * Gets the index keys.
     *
     * @see com.mongodb.client.model.IndexModel.keys
     * @since 2.0.0
     */
    val keys: BsonDocument,
    /**
     * Gets the index options.
     *
     * @see com.mongodb.client.model.IndexModel.options
     * @since 2.0.0
     */
    val options: CreateIndexOptions = CreateIndexOptions(),
)

/**
 * The default options for a collection to apply
 * on the creation of indexes.
 *
 * @see com.mongodb.client.model.IndexOptionDefaults
 * @since 2.0.0
 */
data class IndexOptionDefaults(
    /**
     * Sets the default storage engine options
     * document for indexes.
     *
     * @see com.mongodb.client.model.IndexOptionDefaults.storageEngine
     * @since 2.0.0
     */
    val storageEngine: BsonDocument? = null,
)

/**
 * Options for cluster index on a collection.
 *
 * @see com.mongodb.client.model.ClusteredIndexOptions
 * @since 2.0.0
 */
data class ClusteredIndexOptions(
    /**
     * The index key.
     *
     * @see com.mongodb.client.model.ClusteredIndexOptions.getKey
     * @since 2.0.0
     */
    val key: BsonDocument,
    /**
     * Whether the index entries must be unique
     *
     * @see com.mongodb.client.model.ClusteredIndexOptions.isUnique
     * @since 2.0.0
     */
    val unique: Boolean = false,
    /**
     * Sets the index name.
     *
     * @see com.mongodb.client.model.ClusteredIndexOptions.name
     * @since 2.0.0
     */
    val name: String? = null,
)

/**
 * Options for change stream pre- and post- images.
 *
 * @see com.mongodb.client.model.ChangeStreamPreAndPostImagesOptions
 * @since 2.0.0
 */
data class ChangeStreamPreAndPostImagesOptions(
    /**
     * Gets whether change stream pre- and post-
     * images are enabled for the collection.
     *
     * @see com.mongodb.client.model.ChangeStreamPreAndPostImagesOptions.enabled
     * @since 2.0.0
     */
    val enabled: Boolean,
)

/* ============= ------------------ ============= */

/**
 * Collation support allows the specific
 * configuration of how character cases are
 * handled.
 *
 * @see com.mongodb.client.model.CollationCaseFirst
 * @since 2.0.0
 */
enum class CollationCaseFirst {
    /**
     * Uppercase first.
     *
     * @see com.mongodb.client.model.CollationCaseFirst.UPPER
     * @since 2.0.0
     */
    Upper,

    /**
     * Lowercase first.
     *
     * @see com.mongodb.client.model.CollationCaseFirst.LOWER
     * @since 2.0.0
     */
    Lower,

    /**
     * Off.
     *
     * @see com.mongodb.client.model.CollationCaseFirst.OFF
     * @since 2.0.0
     */
    Off
}

/**
 * Collation support allows the specific
 * configuration of how differences between
 * characters are handled.
 *
 * @see com.mongodb.client.model.CollationStrength
 * @since 2.0.0
 */
enum class CollationStrength {
    /**
     * Strongest level, denote difference between
     * base characters.
     *
     * @see com.mongodb.client.model.CollationStrength.PRIMARY
     * @since 2.0.0
     */
    Primary,

    /**
     * Accents in characters are considered
     * secondary differences.
     *
     * @see com.mongodb.client.model.CollationStrength.SECONDARY
     * @since 2.0.0
     */
    Secondary,

    /**
     * Upper and lower case differences in
     * characters are distinguished at the
     * tertiary level. The server default.
     *
     * @see com.mongodb.client.model.CollationStrength.TERTIARY
     * @since 2.0.0
     */
    Tertiary,

    /**
     * When punctuation is ignored at level 1-3,
     * an additional level can be used to
     * distinguish words with and without
     * punctuation.
     *
     * @see com.mongodb.client.model.CollationStrength.QUATERNARY
     * @since 2.0.0
     */
    Quaternary,

    /**
     * When all other levels are equal, the
     * identical level is used as a tiebreaker.
     * The Unicode code point values of the NFD
     * form of each string are compared at this
     * level, just in case there is no difference
     * at levels 1-4
     *
     * @see com.mongodb.client.model.CollationStrength.IDENTICAL
     * @since 2.0.0
     */
    Identical
}

/**
 * Collation support allows the specific
 * configuration of whether or not spaces and
 * punctuation are considered base characters.
 *
 * @see com.mongodb.client.model.CollationAlternate
 * @since 2.0.0
 */
enum class CollationAlternate {
    /**
     * Non-ignorable
     *
     * @see com.mongodb.client.model.CollationAlternate.NON_IGNORABLE
     * @since 2.0.0
     */
    NonIgnorable,

    /**
     * Shifted
     *
     * @see com.mongodb.client.model.CollationAlternate.SHIFTED
     * @since 2.0.0
     */
    Shifted
}

/**
 * Collation support allows the specific
 * configuration of whether or not spaces and
 * punctuation are considered base characters.
 *
 * @see com.mongodb.client.model.CollationMaxVariable
 * @since 2.0.0
 */
enum class CollationMaxVariable {
    /**
     * Punct
     *
     * @see com.mongodb.client.model.CollationMaxVariable.PUNCT
     * @since 2.0.0
     */
    Punct,

    /**
     * Shifted
     *
     * @see com.mongodb.client.model.CollationMaxVariable.SPACE
     * @since 2.0.0
     */
    Space
}

/**
 * The options regarding collation support in MongoDB 3.4+
 *
 * @see com.mongodb.client.model.Collation
 * @since 2.0.0
 */
data class Collation(
    /**
     * The locale
     *
     * @see com.mongodb.client.model.Collation.getLocale
     * @since 2.0.0
     */
    val locale: String? = null,
    /**
     * The case level value.
     *
     * @see com.mongodb.client.model.Collation.getCaseLevel
     * @since 2.0.0
     */
    val caseLevel: Boolean? = null,
    /**
     * The collation case first value
     *
     * @see com.mongodb.client.model.Collation.getCaseFirst
     * @since 2.0.0
     */
    val caseFirst: CollationCaseFirst? = null,
    /**
     * The collation strength
     *
     * @see com.mongodb.client.model.Collation.getStrength
     * @since 2.0.0
     */
    val strength: CollationStrength? = null,
    /**
     * Returns the numeric ordering, if true will
     * order numbers based on numerical order and
     * not collation order.
     *
     * @see com.mongodb.client.model.Collation.getNumericOrdering
     * @since 2.0.0
     */
    val numericOrdering: Boolean? = null,
    /**
     * The collation alternate
     *
     * @see com.mongodb.client.model.Collation.getAlternate
     * @since 2.0.0
     */
    val alternate: CollationAlternate? = null,
    /**
     * The maxVariable
     *
     * @see com.mongodb.client.model.Collation.getMaxVariable
     * @since 2.0.0
     */
    val maxVariable: CollationMaxVariable? = null,
    /**
     * The normalization value
     *
     * @see com.mongodb.client.model.Collation.getNormalization
     * @since 2.0.0
     */
    val normalization: Boolean? = null,
    /**
     * The backwards value
     *
     * @see com.mongodb.client.model.Collation.getBackwards
     * @since 2.0.0
     */
    val backwards: Boolean? = null,
)

/* ============= ------------------ ============= */

/**
 * A read concern allows clients to choose a level
 * of isolation for their reads.
 *
 * @see com.mongodb.ReadConcern
 * @since 2.0.0
 */
enum class ReadConcern {
    /**
     * Use the servers default read concern.
     *
     * @see com.mongodb.ReadConcern.DEFAULT
     * @since 2.0.0
     */
    Default,

    /**
     * The local read concern.
     *
     * @see com.mongodb.ReadConcern.LOCAL
     * @since 2.0.0
     */
    Local,

    /**
     * The majority read concern.
     *
     * @see com.mongodb.ReadConcern.MAJORITY
     * @since 2.0.0
     */
    Majority,

    /**
     * The linearizable read concern.
     *
     * @see com.mongodb.ReadConcern.LINEARIZABLE
     * @since 2.0.0
     */
    Linearizable,

    /**
     * The snapshot read concern.
     *
     * @see com.mongodb.ReadConcern.SNAPSHOT
     * @since 2.0.0
     */
    Snapshot,

    /**
     * The available read concern.
     *
     * @see com.mongodb.ReadConcern.AVAILABLE
     * @since 2.0.0
     */
    Available
}

/**
 * Controls the acknowledgment of write operations
 * with various options.
 *
 * @see com.mongodb.WriteConcern
 * @since 2.0.0
 */
data class WriteConcern(
    /**
     * The `w` value, which must be a [String] or
     * a none-negative [Int].
     *
     * - `0` : Don't wait for acknowledgement from the server.
     * - `1` : Wait for acknowledgement, but don't wait for secondaries to replicate.
     * - `>=2` : Wait for one or more secondaries to also acknowledge.
     * - `"majority"` : Wait for a majority of data bearing nodes to acknowledge.
     * - `"<tag set name>"` : Wait for one or more secondaries to also acknowledge based on a tag set name.
     *
     * @see com.mongodb.WriteConcern.getWObject
     * @since 2.0.0
     */
    val w: Any? = null,
    /**
     * How long to wait for secondaries to
     * acknowledge before failing, which must
     * be `>=0`.
     *
     * - `0` : Indefinite.
     * - `>0` : Time to wait in milliseconds.
     *
     * @see com.mongodb.WriteConcern.getWTimeout
     * @since 2.0.0
     */
    val timeout: Duration? = null,
    /**
     * If true block until write operations have
     * been committed to the journal. Cannot be
     * used in combination with fsync. Write
     * operations will fail with an exception if
     * this option is used when the server is
     * running without journaling.
     *
     * @see com.mongodb.WriteConcern.getJournal
     * @since 2.0.0
     */
    val journal: Boolean? = null,
) {
    companion object {
        /**
         * Write operations that use this write
         * concern will wait for acknowledgement,
         * using the default write concern
         * configured on the server.
         *
         * @see com.mongodb.WriteConcern.ACKNOWLEDGED
         * @since 2.0.0
         */
        val Acknowledged = WriteConcern()

        /**
         * Write operations that use this write
         * concern will wait for acknowledgement
         * from a single member.
         *
         * @see com.mongodb.WriteConcern.W1
         * @since 2.0.0
         */
        val W1 = WriteConcern(1)

        /**
         * Write operations that use this write
         * concern will wait for acknowledgement
         * from two members.
         *
         * @see com.mongodb.WriteConcern.W2
         * @since 2.0.0
         */
        val W2 = WriteConcern(2)

        /**
         * Write operations that use this write
         * concern will wait for acknowledgement
         * from three members.
         *
         * @see com.mongodb.WriteConcern.W3
         * @since 2.0.0
         */
        val W3 = WriteConcern(3)

        /**
         * Write operations that use this write
         * concern will return as soon as the
         * message is written to the socket.
         * Exceptions are raised for network
         * issues, but not server errors.
         *
         * @see com.mongodb.WriteConcern.UNACKNOWLEDGED
         * @since 2.0.0
         */
        val Unacknowledged = WriteConcern(0)

        /**
         * Write operations wait for the server to
         * group commit to the journal file on disk.
         *
         * @see com.mongodb.WriteConcern.JOURNALED
         * @since 2.0.0
         */
        val Journaled = WriteConcern(journal = true)

        /**
         * Exceptions are raised for network issues,
         * and server errors; waits on a majority
         * of servers for the write operation.
         *
         * @see com.mongodb.WriteConcern.MAJORITY
         * @since 2.0.0
         */
        val Majority = WriteConcern("majority")
    }
}

/**
 * Options to apply to transactions. The default
 * values for the options depend on context. For
 * options specified per-transaction, the default
 * values come from the default transaction
 * options. For the default transaction options
 * themselves, the default values come from the
 * MongoClient on which the session was started.
 *
 * @see com.mongodb.TransactionOptions
 * @since 2.0.0
 */
data class TransactionOptions(
    /**
     * The read concern.
     *
     * @see com.mongodb.TransactionOptions.readConcern
     * @since 2.0.0
     */
    val readConcern: ReadConcern? = null,
    /**
     * The write concern.
     *
     * @see com.mongodb.TransactionOptions.writeConcern
     * @since 2.0.0
     */
    val writeConcern: WriteConcern? = null,
    /**
     * The read preference.
     *
     * @see com.mongodb.TransactionOptions.readPreference
     * @since 2.0.0
     */
    val readPreference: ReadPreference? = null,
    /**
     * The maximum amount of time to allow a
     * single commitTransaction command to
     * execute. The default is null, which places
     * no limit on the execution time.
     *
     * @see com.mongodb.TransactionOptions.getMaxCommitTime
     * @since 2.0.0
     */
    val maxCommitTime: Duration? = null,
)

/* ============= ------------------ ============= */

/**
 * Determines how strictly MongoDB applies the
 * validation rules to existing documents during
 * an insert or update.
 *
 * @see com.mongodb.client.model.ValidationLevel
 * @since 2.0.0
 */
enum class ValidationLevel {
    /**
     * No validation for inserts or updates.
     *
     * @see com.mongodb.client.model.ValidationLevel.OFF
     * @since 2.0.0
     */
    Off,

    /**
     * Apply validation rules to all inserts and all updates.
     *
     * @see com.mongodb.client.model.ValidationLevel.STRICT
     * @since 2.0.0
     */
    Strict,

    /**
     * Applies validation rules to inserts and to updates on existing valid documents.
     *
     * @see com.mongodb.client.model.ValidationLevel.MODERATE
     * @since 2.0.0
     */
    Moderate
}

/**
 * Determines whether to error on invalid
 * documents or just warn about the violations but
 * allow invalid documents.
 *
 * @see com.mongodb.client.model.ValidationAction
 * @since 2.0.0
 */
enum class ValidationAction {
    /**
     * Documents must pass validation before the
     * write occurs. Otherwise, the write
     * operation fails.
     *
     * @see com.mongodb.client.model.ValidationAction.ERROR
     * @since 2.0.0
     */
    Error,

    /**
     * Documents do not have to pass validation.
     * If the document fails validation, the write
     * operation logs the validation failure to
     * the mongod logs.
     *
     * @see com.mongodb.client.model.ValidationAction.WARN
     * @since 2.0.0
     */
    Warn
}

/**
 * Validation options for documents being inserted
 * or updated in a collection
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.ValidationOptions
 */
data class ValidationOptions(
    /**
     * Sets the validation rules for all.
     *
     * @see com.mongodb.client.model.ValidationOptions.validator
     * @since 2.0.0
     */
    val validator: BsonDocument? = null,
    /**
     * Sets the validation level that determines
     * how strictly MongoDB applies the validation
     * rules to existing documents during an
     * insert or update.
     *
     * @see com.mongodb.client.model.ValidationOptions.validationLevel
     * @since 2.0.0
     */
    val level: ValidationLevel? = null,
    /**
     * Sets the [ValidationAction] that determines
     * whether to error on invalid documents or
     * just warn about the violations but allow
     * invalid documents.
     *
     * @see com.mongodb.client.model.ValidationOptions.validationAction
     * @since 2.0.0
     */
    val action: ValidationAction? = null,
)

/* ============= ------------------ ============= */

/**
 * An enumeration of time-series data granularity.
 *
 * It describes the units one would use to
 * describe the expected interval between
 * subsequent measurements for a time-series.
 *
 * @since 2.0.0
 * @see com.mongodb.client.model.TimeSeriesGranularity
 */
enum class TimeSeriesGranularity {
    /**
     * Seconds-level granularity.
     *
     * If granularity of a time-series collection
     * is unspecified, this is the default value.
     *
     * @see com.mongodb.client.model.TimeSeriesGranularity.SECONDS
     * @since 2.0.0
     */
    Seconds,

    /**
     * Minutes-level granularity.
     *
     * @see com.mongodb.client.model.TimeSeriesGranularity.MINUTES
     * @since 2.0.0
     */
    Minutes,

    /**
     * Hours-level granularity.
     *
     * @see com.mongodb.client.model.TimeSeriesGranularity.HOURS
     * @since 2.0.0
     */
    Hours
}

/**
 * Options related to the creation of time-series collections.
 *
 * @see com.mongodb.client.model.TimeSeriesOptions
 * @since 2.0.0
 */
data class TimeSeriesOptions(
    /**
     * The name of the field holding the time value.
     *
     * @see com.mongodb.client.model.TimeSeriesOptions.timeField
     * @since 2.0.0
     */
    val timeField: String,
    /**
     * Sets the name of the meta field.
     *
     * The name of the field which contains metadata in each time series document. The metadata in the specified field should be data
     * that is used to label a unique series of documents. The metadata should rarely, if ever, change.  This field is used to group
     * related data and may be of any BSON type, except for array. This name may not be the same as the {@code timeField} or "_id".
     *
     * @see com.mongodb.client.model.TimeSeriesOptions.metaField
     * @since 2.0.0
     */
    val metaField: String? = null,
    /**
     * Gets the granularity of the time-series data.
     *
     * @see com.mongodb.client.model.TimeSeriesOptions.granularity
     * @since 2.0.0
     */
    val granularity: TimeSeriesGranularity? = null,
)

/* ============= ------------------ ============= */

/**
 * A base class for models that can be used in a
 * bulk write operations.
 *
 * @see com.mongodb.client.model.WriteModel
 * @since 2.0.0
 */
sealed interface WriteModel

/**
 * A model describing the removal of at most one
 * document matching the query filter.
 *
 * @see com.mongodb.client.model.DeleteOneModel
 * @since 2.0.0
 */
data class DeleteOneModel(
    /**
     * The query filter.
     *
     * @see com.mongodb.client.model.DeleteOneModel.getFilter
     * @since 2.0.0
     */
    val filter: BsonDocument,
    /**
     * The options to apply.
     *
     * @see com.mongodb.client.model.DeleteOneModel.getOptions
     * @since 2.0.0
     */
    val options: DeleteOptions = DeleteOptions(),
) : WriteModel

/**
 * A model describing the removal of all documents
 * matching the query filter.
 *
 * @see com.mongodb.client.model.DeleteManyModel
 * @since 2.0.0
 */
data class DeleteManyModel(
    /**
     * The query filter.
     *
     * @see com.mongodb.client.model.DeleteManyModel.getFilter
     * @since 2.0.0
     */
    val filter: BsonDocument,
    /**
     * The options to apply.
     *
     * @see com.mongodb.client.model.DeleteManyModel.getOptions
     * @since 2.0.0
     */
    val options: DeleteOptions = DeleteOptions(),
) : WriteModel

/**
 * A model describing an insert of a single document.
 *
 * @see com.mongodb.client.model.InsertOneModel
 * @since 2.0.0
 */
data class InsertOneModel(
    /**
     * The document to insert.
     *
     * @see com.mongodb.client.model.InsertOneModel.getDocument
     * @since 2.0.0
     */
    val document: BsonDocument,
) : WriteModel

/**
 * A model describing the replacement of at most
 * one document that matches the query filter.
 *
 * @see com.mongodb.client.model.ReplaceOneModel
 * @since 2.0.0
 */
data class ReplaceOneModel(
    /**
     * Gets the query filter.
     *
     * @see com.mongodb.client.model.ReplaceOneModel.getFilter
     * @since 2.0.0
     */
    val filter: BsonDocument,
    /**
     * Gets the document which will replace the
     * document matching the query filter.
     *
     * @see com.mongodb.client.model.ReplaceOneModel.getReplacement
     * @since 2.0.0
     */
    val replacement: BsonDocument,
    /**
     * Gets the ReplaceOptions to apply.
     *
     * @see com.mongodb.client.model.ReplaceOneModel.getReplaceOptions
     * @since 2.0.0
     */
    val options: ReplaceOptions = ReplaceOptions(),
) : WriteModel

/**
 * A model describing an update to at most one
 * document that matches the query filter.
 * The update to apply must include only update
 * operators.
 *
 * @see com.mongodb.client.model.UpdateOneModel
 * @since 2.0.0
 */
data class UpdateOneModel(
    /**
     * The query filter.
     *
     * @see com.mongodb.client.model.UpdateOneModel.getFilter
     * @since 2.0.0
     */
    val filter: BsonDocument,
    /**
     * Gets the document or document list
     * specifying the updates to apply to the
     * matching document.
     * The update to apply must include only
     * update operators.
     *
     * This property MUST be either of type
     * [BsonDocument] or [BsonArray] that has only
     * [BsonDocument] items.
     *
     * @see com.mongodb.client.model.UpdateOneModel.getUpdate
     * @see com.mongodb.client.model.UpdateOneModel.getUpdatePipeline
     * @since 2.0.0
     */
    val update: BsonElement,
    /**
     * Gets the options to apply.
     *
     * @see com.mongodb.client.model.UpdateOneModel.getOptions
     * @since 2.0.0
     */
    val options: UpdateOptions = UpdateOptions(),
) : WriteModel

/**
 * A model describing an update to all documents
 * that matches the query filter. The update to
 * apply must include only update operators.
 *
 * @see com.mongodb.client.model.UpdateManyModel
 * @since 2.0.0
 */
data class UpdateManyModel(
    /**
     * The query filter.
     *
     * @see com.mongodb.client.model.UpdateManyModel.getFilter
     * @since 2.0.0
     */
    val filter: BsonDocument,
    /**
     * Gets the document or document list
     * specifying the updates to apply to the
     * matching document.
     * The update to apply must include only
     * update operators.
     *
     * This property MUST be either of type
     * [BsonDocument] or [BsonArray] that has only
     * [BsonDocument] items.
     *
     * @see com.mongodb.client.model.UpdateManyModel.getUpdate
     * @see com.mongodb.client.model.UpdateManyModel.getUpdatePipeline
     * @since 2.0.0
     */
    val update: BsonElement,
    /**
     * Gets the options to apply.
     *
     * @see com.mongodb.client.model.UpdateManyModel.getOptions
     * @since 2.0.0
     */
    val options: UpdateOptions = UpdateOptions(),
) : WriteModel

/* ============= ------------------ ============= */

// TODO ChangeStreamDocument
// TODO ConnectionString
// TODO MongoDriverInformation
// TODO MongoClientSettings
// TODO ReadPreference

/* ============= ------------------ ============= */

/**
 * The results from a delete operation.
 *
 * @see com.mongodb.client.result.DeleteResult
 * @author LSafer
 * @since 2.0.0
 */
data class DeleteResult(
    /**
     * True, if the deletion was acknowledged.
     *
     * @see com.mongodb.client.result.DeleteResult.wasAcknowledged
     * @since 2.0.0
     */
    val acknowledged: Boolean,
    /**
     * The number of documents that was deleted.
     *
     * Will be `-1` if not [acknowledged].
     *
     * @see com.mongodb.client.result.DeleteResult.getDeletedCount
     * @since 2.0.0
     */
    val deletedCount: Long = -1,
)

/**
 * The results from an insert one operation.
 *
 * @see com.mongodb.client.result.InsertOneResult
 * @author LSafer
 * @since 2.0.0
 */
data class InsertOneResult(
    /**
     * True, if the write was acknowledged.
     *
     * @see com.mongodb.client.result.InsertOneResult.wasAcknowledged
     * @since 2.0.0
     */
    val acknowledged: Boolean,
    /**
     * The `_id` of the inserted document if
     * available, otherwise `null`
     *
     * Will be `null` if no [acknowledged]
     *
     * @see com.mongodb.client.result.InsertOneResult.getInsertedId
     * @since 2.0.0
     */
    val insertedId: BsonElement?,
)

/**
 * The results from an insert many operation.
 *
 * @see com.mongodb.client.result.InsertManyResult
 * @author LSafer
 * @since 2.0.0
 */
data class InsertManyResult(
    /**
     * True, if the write was acknowledged.
     *
     * @see com.mongodb.client.result.InsertManyResult.wasAcknowledged
     * @since 2.0.0
     */
    val acknowledged: Boolean,
    /**
     * A map of ids of inserted items mapped by index.
     *
     * Will be an [emptyMap] if not [acknowledged]
     *
     * @see com.mongodb.client.result.InsertManyResult.getInsertedIds
     * @since 2.0.0
     */
    val inserts: Map<Int, BsonElement>,
)

/**
 * The results from an update operation.
 *
 * @see com.mongodb.client.result.UpdateResult
 * @author LSafer
 * @since 2.0.0
 */
data class UpdateResult(
    /**
     * True, if the update was acknowledged.
     *
     * @see com.mongodb.client.result.UpdateResult.wasAcknowledged
     * @since 2.0.0
     */
    val acknowledged: Boolean,
    /**
     * The number of documents that was matched.
     *
     * Will be `-1` if not [acknowledged].
     *
     * @see com.mongodb.client.result.UpdateResult.getMatchedCount
     * @since 2.0.0
     */
    val matchedCount: Long,
    /**
     * The number of documents that was modified.
     *
     * Will be `-1` if not [acknowledged].
     *
     * @see com.mongodb.client.result.UpdateResult.getModifiedCount
     * @since 2.0.0
     */
    val modifiedCount: Long,
    /**
     * If the update resulted in an inserted
     * document, gets the _id of the inserted
     * document, otherwise null.
     *
     * Will be `null` if no [acknowledged]
     *
     * @see com.mongodb.client.result.UpdateResult.getUpsertedId
     * @since 2.0.0
     */
    val upsertedId: BsonElement?,
)

/**
 * The results from a bulk write operation.
 *
 * @see com.mongodb.bulk.BulkWriteResult
 * @author LSafer
 * @since 2.0.0
 */
data class BulkWriteResult(
    /**
     * True, if the write was acknowledged.
     *
     * @see com.mongodb.bulk.BulkWriteResult.wasAcknowledged
     * @since 2.0.0
     */
    val acknowledged: Boolean,
    /**
     * The number of documents that was inserted.
     *
     * Will be `-1` if not [acknowledged].
     *
     * @see com.mongodb.bulk.BulkWriteResult.getInsertedCount
     * @since 2.0.0
     */
    val insertedCount: Int,
    /**
     * The number of documents that was matched.
     *
     * Will be `-1` if not [acknowledged].
     *
     * @see com.mongodb.bulk.BulkWriteResult.getMatchedCount
     * @since 2.0.0
     */
    val matchedCount: Int,
    /**
     * The number of documents that was deleted.
     *
     * Will be `-1` if not [acknowledged].
     *
     * @see com.mongodb.bulk.BulkWriteResult.getDeletedCount
     * @since 2.0.0
     */
    val deletedCount: Int,
    /**
     * The number of documents that was modified.
     *
     * Will be `-1` if not [acknowledged].
     *
     * @see com.mongodb.bulk.BulkWriteResult.getModifiedCount
     * @since 2.0.0
     */
    val modifiedCount: Int,
    /**
     * A map of ids of inserted items mapped by index.
     *
     * Will be an [emptyMap] if not [acknowledged]
     *
     * @see com.mongodb.bulk.BulkWriteResult.getInserts
     * @since 2.0.0
     */
    val inserts: Map<Int, BsonElement>,
    /**
     * A map of ids of upserted items mapped by index.
     *
     * Will be an [emptyMap] if not [acknowledged]
     *
     * @see com.mongodb.bulk.BulkWriteResult.getUpserts
     * @since 2.0.0
     */
    val upserts: Map<Int, BsonElement>,
)

/* ============= ------------------ ============= */
