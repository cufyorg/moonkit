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
import org.cufy.bson.java
import org.cufy.bson.kt
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val CursorType.java: JavaCursorType
    get() = when (this) {
        CursorType.NonTailable -> JavaCursorType.NonTailable
        CursorType.Tailable -> JavaCursorType.Tailable
        CursorType.TailableAwait -> JavaCursorType.TailableAwait
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ReturnDocument.java: JavaReturnDocument
    get() = when (this) {
        ReturnDocument.Before -> JavaReturnDocument.BEFORE
        ReturnDocument.After -> JavaReturnDocument.AFTER
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val FullDocument.java: JavaFullDocument
    get() = when (this) {
        FullDocument.Default -> JavaFullDocument.DEFAULT
        FullDocument.UpdateLookup -> JavaFullDocument.UPDATE_LOOKUP
        FullDocument.WhenAvailable -> JavaFullDocument.WHEN_AVAILABLE
        FullDocument.Required -> JavaFullDocument.REQUIRED
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val CommitQuorum.java: JavaCommitQuorum
    get() = when (this) {
        CommitQuorum.Majority -> JavaCommitQuorum.MAJORITY
        CommitQuorum.VotingMembers -> JavaCommitQuorum.VOTING_MEMBERS
        else -> when (value) {
            is Int -> JavaCommitQuorum.create(value)
            is String -> JavaCommitQuorum.create(value)
            else -> error("CommitQuorum.value must be either a String or an Int")
        }
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val MongoNamespace.java: JavaMongoNamespace
    get() {
        return JavaMongoNamespace(database, collection)
    }

/**
 * Return a kotlin version of this.
 */
val JavaMongoNamespace.kt: MongoNamespace
    get() {
        return MongoNamespace(databaseName, collectionName)
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val CreateIndexModel.java: JavaCreateIndexModel
    get() {
        return JavaCreateIndexModel(keys.java, options.java)
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val IndexOptionDefaults.java: JavaIndexOptionDefaults
    get() {
        return JavaIndexOptionDefaults()
            .storageEngine(storageEngine?.java)
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ClusteredIndexOptions.java: JavaClusteredIndexOptions
    get() {
        return JavaClusteredIndexOptions(key.java, unique).apply {
            name?.let { name(it) }
        }
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ChangeStreamPreAndPostImagesOptions.java: JavaChangeStreamPreAndPostImagesOptions
    get() {
        return JavaChangeStreamPreAndPostImagesOptions(enabled)
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val CollationCaseFirst.java: JavaCollationCaseFirst
    get() {
        return when (this) {
            CollationCaseFirst.Upper -> JavaCollationCaseFirst.UPPER
            CollationCaseFirst.Lower -> JavaCollationCaseFirst.LOWER
            CollationCaseFirst.Off -> JavaCollationCaseFirst.OFF
        }
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val CollationStrength.java: JavaCollationStrength
    get() {
        return when (this) {
            CollationStrength.Primary -> JavaCollationStrength.PRIMARY
            CollationStrength.Secondary -> JavaCollationStrength.SECONDARY
            CollationStrength.Tertiary -> JavaCollationStrength.TERTIARY
            CollationStrength.Quaternary -> JavaCollationStrength.QUATERNARY
            CollationStrength.Identical -> JavaCollationStrength.IDENTICAL
        }
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val CollationAlternate.java: JavaCollationAlternate
    get() {
        return when (this) {
            CollationAlternate.NonIgnorable -> JavaCollationAlternate.NON_IGNORABLE
            CollationAlternate.Shifted -> JavaCollationAlternate.SHIFTED
        }
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val CollationMaxVariable.java: JavaCollationMaxVariable
    get() {
        return when (this) {
            CollationMaxVariable.Punct -> JavaCollationMaxVariable.PUNCT
            CollationMaxVariable.Space -> JavaCollationMaxVariable.SPACE
        }
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val Collation.java: JavaCollation
    get() {
        return JavaCollation.builder()
            .locale(locale)
            .caseLevel(caseLevel)
            .collationCaseFirst(caseFirst?.java)
            .collationStrength(strength?.java)
            .numericOrdering(numericOrdering)
            .collationAlternate(alternate?.java)
            .collationMaxVariable(maxVariable?.java)
            .normalization(normalization)
            .backwards(backwards)
            .build()
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ReadConcern.java: JavaReadConcern
    get() = when (this) {
        ReadConcern.Default -> JavaReadConcern.DEFAULT
        ReadConcern.Local -> JavaReadConcern.LOCAL
        ReadConcern.Majority -> JavaReadConcern.MAJORITY
        ReadConcern.Linearizable -> JavaReadConcern.LINEARIZABLE
        ReadConcern.Snapshot -> JavaReadConcern.SNAPSHOT
        ReadConcern.Available -> JavaReadConcern.AVAILABLE
    }

/**
 * Return a kotlin version of this.
 */
val JavaReadConcern.kt: ReadConcern
    get() = when (this) {
        JavaReadConcern.DEFAULT -> ReadConcern.Default
        JavaReadConcern.LOCAL -> ReadConcern.Local
        JavaReadConcern.MAJORITY -> ReadConcern.Majority
        JavaReadConcern.LINEARIZABLE -> ReadConcern.Linearizable
        JavaReadConcern.SNAPSHOT -> ReadConcern.Snapshot
        JavaReadConcern.AVAILABLE -> ReadConcern.Available
        else -> error("Unexpected read concern $this")
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val WriteConcern.java: JavaWriteConcern
    get() = when (this) {
        WriteConcern.Acknowledged -> JavaWriteConcern.ACKNOWLEDGED
        WriteConcern.W1 -> JavaWriteConcern.W1
        WriteConcern.W2 -> JavaWriteConcern.W2
        WriteConcern.W3 -> JavaWriteConcern.W3
        WriteConcern.Unacknowledged -> JavaWriteConcern.UNACKNOWLEDGED
        WriteConcern.Journaled -> JavaWriteConcern.JOURNALED
        WriteConcern.Majority -> JavaWriteConcern.MAJORITY
        else -> when (w) {
            is Int -> JavaWriteConcern(w)
            is String -> JavaWriteConcern(w)
            else -> error("w must be String or Int")
        }
            .run { timeout?.let { withWTimeout(it.inWholeMilliseconds, TimeUnit.MILLISECONDS) } ?: this }
            .run { journal?.let { withJournal(it) } ?: this }
    }

/**
 * Return a kotlin version of this.
 */
val JavaWriteConcern.kt: WriteConcern
    get() = when (this) {
        JavaWriteConcern.ACKNOWLEDGED -> WriteConcern.Acknowledged
        JavaWriteConcern.W1 -> WriteConcern.W1
        JavaWriteConcern.W2 -> WriteConcern.W2
        JavaWriteConcern.W3 -> WriteConcern.W3
        JavaWriteConcern.UNACKNOWLEDGED -> WriteConcern.Unacknowledged
        JavaWriteConcern.JOURNALED -> WriteConcern.Journaled
        JavaWriteConcern.MAJORITY -> WriteConcern.Majority
        else -> WriteConcern(
            wObject,
            getWTimeout(TimeUnit.MILLISECONDS)?.toDuration(DurationUnit.MILLISECONDS),
            journal
        )
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val TransactionOptions.java: JavaTransactionOptions
    get() {
        return JavaTransactionOptions.builder()
            .readConcern(readConcern?.java)
            .writeConcern(writeConcern?.java)
            .readPreference(readPreference)
            .maxCommitTime(maxCommitTime?.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .build()
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ValidationLevel.java: JavaValidationLevel
    get() = when (this) {
        ValidationLevel.Off -> JavaValidationLevel.OFF
        ValidationLevel.Strict -> JavaValidationLevel.STRICT
        ValidationLevel.Moderate -> JavaValidationLevel.MODERATE
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ValidationAction.java: JavaValidationAction
    get() = when (this) {
        ValidationAction.Error -> JavaValidationAction.ERROR
        ValidationAction.Warn -> JavaValidationAction.WARN
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ValidationOptions.java: JavaValidationOptions
    get() {
        return JavaValidationOptions()
            .validator(validator?.java)
            .validationLevel(level?.java)
            .validationAction(action?.java)
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val TimeSeriesGranularity.java: JavaTimeSeriesGranularity
    get() = when (this) {
        TimeSeriesGranularity.Seconds -> JavaTimeSeriesGranularity.SECONDS
        TimeSeriesGranularity.Minutes -> JavaTimeSeriesGranularity.MINUTES
        TimeSeriesGranularity.Hours -> JavaTimeSeriesGranularity.HOURS
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val TimeSeriesOptions.java: JavaTimeSeriesOptions
    get() {
        return JavaTimeSeriesOptions(timeField)
            .metaField(metaField)
            .granularity(granularity?.java)
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val WriteModel.java: JavaWriteModel
    get() = when (this) {
        is DeleteOneModel -> java
        is DeleteManyModel -> java
        is InsertOneModel -> java
        is ReplaceOneModel -> java
        is UpdateOneModel -> java
        is UpdateManyModel -> java
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val DeleteOneModel.java: JavaDeleteOneModel
    get() {
        return JavaDeleteOneModel(
            filter.java,
            options.java
        )
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val DeleteManyModel.java: JavaDeleteManyModel
    get() {
        return JavaDeleteManyModel(
            filter.java,
            options.java
        )
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val InsertOneModel.java: JavaInsertOneModel
    get() {
        return JavaInsertOneModel(
            document.java
        )
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val ReplaceOneModel.java: JavaReplaceOneModel
    get() {
        return JavaReplaceOneModel(
            filter.java,
            replacement.java,
            options.java
        )
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val UpdateOneModel.java: JavaUpdateOneModel
    get() = when (update) {
        is BsonDocument -> {
            JavaUpdateOneModel(
                filter.java,
                update.java,
                options.java
            )
        }

        is BsonArray -> {
            JavaUpdateOneModel(
                filter.java,
                update.map { (it as BsonDocument).java },
                options.java
            )
        }

        else -> error("UpdateOneModel.update is expected to be either a document or an array of documents.")
    }

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val UpdateManyModel.java: JavaUpdateManyModel
    get() = when (update) {
        is BsonDocument -> {
            JavaUpdateManyModel(
                filter.java,
                update.java,
                options.java
            )
        }

        is BsonArray -> {
            JavaUpdateManyModel(
                filter.java,
                update.map { (it as BsonDocument).java },
                options.java
            )
        }

        else -> error("UpdateManyModel.update is expected to be either a document or an array of documents.")
    }

/* ============= ------------------ ============= */

/**
 * Return the kotlin version of this bson element.
 */
val JavaDeleteResult.kt: DeleteResult
    get() = when {
        wasAcknowledged() -> {
            DeleteResult(
                acknowledged = true,
                deletedCount = deletedCount
            )
        }

        else -> {
            DeleteResult(
                acknowledged = false,
                deletedCount = -1
            )
        }
    }

/* ============= ------------------ ============= */

/**
 * Return the kotlin version of this bson element.
 */
val JavaInsertOneResult.kt: InsertOneResult
    get() = when {
        wasAcknowledged() -> {
            InsertOneResult(
                acknowledged = true,
                insertedId = insertedId?.kt
            )
        }

        else -> {
            InsertOneResult(
                acknowledged = false,
                insertedId = null
            )
        }
    }

/* ============= ------------------ ============= */

/**
 * Return the kotlin version of this bson element.
 */
val JavaInsertManyResult.kt: InsertManyResult
    get() = when {
        wasAcknowledged() -> {
            InsertManyResult(
                acknowledged = true,
                inserts = insertedIds.mapValues { it.value.kt }
            )
        }

        else -> {
            InsertManyResult(
                acknowledged = false,
                inserts = emptyMap()
            )
        }
    }

/* ============= ------------------ ============= */

/**
 * Return the kotlin version of this bson element.
 */
val JavaUpdateResult.kt: UpdateResult
    get() = when {
        wasAcknowledged() -> {
            UpdateResult(
                acknowledged = true,
                matchedCount = matchedCount,
                modifiedCount = modifiedCount,
                upsertedId = upsertedId?.kt
            )
        }

        else -> {
            UpdateResult(
                acknowledged = false,
                matchedCount = -1,
                modifiedCount = -1,
                upsertedId = null
            )
        }
    }

/* ============= ------------------ ============= */

/**
 * Return the kotlin version of this bson element.
 */
val JavaBulkWriteResult.kt: BulkWriteResult
    get() = when {
        wasAcknowledged() -> {
            BulkWriteResult(
                acknowledged = true,
                insertedCount = insertedCount,
                matchedCount = matchedCount,
                deletedCount = deletedCount,
                modifiedCount = modifiedCount,
                inserts = inserts.associate { it.index to it.id.kt },
                upserts = upserts.associate { it.index to it.id.kt }
            )
        }

        else -> {
            BulkWriteResult(
                acknowledged = false,
                insertedCount = -1,
                matchedCount = -1,
                deletedCount = -1,
                modifiedCount = -1,
                inserts = emptyMap(),
                upserts = emptyMap()
            )
        }
    }

/* ============= ------------------ ============= */
