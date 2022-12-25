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

import com.mongodb.ReadPreference
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.CreateViewOptions
import com.mongodb.reactivestreams.client.ClientSession
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.cufy.bson.Bson
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.document

/* ============= --- runCommand --- ============= */

/**
 * Executes command in the context of the current
 * database.
 *
 * @param session the client session with which to associate this operation.
 * @param command the command to be run.
 * @param preference the [ReadPreference] to be used when executing the command.
 * @return the command result
 * @since 2.0.0
 * @see MonktDatabase.runCommand
 */
suspend fun MonktDatabase.runCommandSuspend(
    command: Bson,
    preference: ReadPreference = ReadPreference.primary(),
    session: ClientSession? = null
): BsonDocument? {
    val publisher = runCommand(command, preference, session)
    return publisher.awaitFirstOrNull()
}

/* ============= ------ drop ------ ============= */

/**
 * Drops this database.
 *
 * @param session the client session with which to associate this operation
 * @since 2.0.0
 * @see MonktDatabase.drop
 */
suspend fun MonktDatabase.dropSuspend(
    session: ClientSession? = null
) {
    val publisher = drop(session)
    publisher.awaitFirstOrNull()
}

/* ========== - listCollectionNames  - ========== */

/**
 * Gets the names of all the collections in this
 * database.
 *
 * @param session the client session with which to associate this operation
 * @return all the names of all the collections in this database
 * @since 2.0.0
 * @see MonktDatabase.listCollectionNames
 */
suspend fun MonktDatabase.listCollectionNamesSuspend(
    session: ClientSession? = null
): List<String> {
    val publisher = listCollectionNames(session)
    return publisher.asFlow().toList()
}

/* ============ - listCollections  - ============ */

/**
 * Finds all the collections in this database.
 *
 * @param session the client session with which to associate this operation.
 * @param block the publisher block.
 * @return the collections list
 * @since 2.0.0
 * @see MonktDatabase.listCollections
 */
suspend fun MonktDatabase.listCollectionsSuspend(
    session: ClientSession? = null,
    block: ListCollectionsPublisherScope.() -> Unit = {}
): List<BsonDocument> {
    var publisher = listCollections(session)
    publisher = publisher.configure(block)
    return publisher.asFlow().toList()
}

/* ============ - createCollection - ============ */

/**
 * Create a new collection with the selected options
 *
 * @param session the client session with which to associate this operation
 * @param name the name for the new collection to create
 * @param options various options for creating the collection
 * @since 2.0.0
 * @see MonktDatabase.createCollection
 */
suspend fun MonktDatabase.createCollectionSuspend(
    name: String,
    session: ClientSession? = null,
    options: CreateCollectionOptions
) {
    val publisher = createCollection(name, options, session)
    publisher.awaitFirstOrNull()
}

/**
 * Create a new collection with the selected options
 *
 * @param session the client session with which to associate this operation
 * @param name the name for the new collection to create
 * @param block various options block for creating the collection
 * @since 2.0.0
 * @see MonktDatabase.createCollection
 */
suspend fun MonktDatabase.createCollectionSuspend(
    name: String,
    session: ClientSession? = null,
    block: CreateCollectionOptionsScope.() -> Unit = {}
) {
    var options = CreateCollectionOptions()
    options = options.configure(block)
    createCollectionSuspend(name, session, options)
}

/* ============= --- createView --- ============= */

/**
 * Creates a view with the given name, backing
 * collection/view name, aggregation pipeline, and
 * options that defines the view.
 *
 * @param session the client session with which to associate this operation
 * @param name the name of the view to create
 * @param on the backing collection/view for the view
 * @param pipeline the pipeline that defines the view
 * @param options various options for creating the view
 * @since 2.0.0
 * @see MonktDatabase.createView
 */
suspend fun MonktDatabase.createViewSuspend(
    name: String,
    on: String,
    pipeline: List<Bson>,
    session: ClientSession? = null,
    options: CreateViewOptions
) {
    val publisher = createView(name, on, pipeline, options, session)
    publisher.awaitFirstOrNull()
}

/**
 * Creates a view with the given name, backing
 * collection/view name, aggregation pipeline, and
 * options that defines the view.
 *
 * @param session the client session with which to associate this operation
 * @param name the name of the view to create
 * @param on the backing collection/view for the view
 * @param pipeline the pipeline that defines the view
 * @param block various options block for creating the view
 * @since 2.0.0
 * @see MonktDatabase.createView
 */
suspend fun MonktDatabase.createViewSuspend(
    name: String,
    on: String,
    pipeline: List<Bson>,
    session: ClientSession? = null,
    block: CreateViewOptionsScope.() -> Unit = {}
) {
    var options = CreateViewOptions()
    options = options.configure(block)
    createViewSuspend(name, on, pipeline, session, options)
}

/**
 * Creates a view with the given name, backing
 * collection/view name, aggregation pipeline, and
 * options that defines the view.
 *
 * @param session the client session with which to associate this operation
 * @param name the name of the view to create
 * @param on the backing collection/view for the view
 * @param pipeline the pipeline that defines the view
 * @param block various options block for creating the view
 * @since 2.0.0
 * @see MonktDatabase.createView
 */
suspend fun MonktDatabase.createViewSuspend(
    name: String,
    on: String,
    vararg pipeline: BsonDocumentBlock,
    session: ClientSession? = null,
    block: CreateViewOptionsScope.() -> Unit = {}
) {
    createViewSuspend(name, on, pipeline.map { document(it) }, session, block)
}

/* ============= ----- watch  ----- ============= */

// TODO watch

/* ============= --- aggregate  --- ============= */

/**
 * Runs an aggregation framework pipeline on the
 * database for pipeline stages that do not
 * require an underlying collection, such as
 * `$currentOp` and `$listLocalSessions`.
 *
 * @param session the client session with which to associate this operation
 * @param pipeline the aggregation pipeline
 * @param block the publisher configuration block.
 * @return an iterable containing the result of the aggregation operation
 * @since 2.0.0
 * @see MonktDatabase.aggregate
 */
suspend fun MonktDatabase.aggregateSuspend(
    pipeline: List<Bson>,
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<BsonDocument> {
    var publisher = aggregate(pipeline, session)
    publisher = publisher.configure(block)
    return publisher.asFlow().toList()
}

/**
 * Runs an aggregation framework pipeline on the
 * database for pipeline stages that do not
 * require an underlying collection, such as
 * `$currentOp` and `$listLocalSessions`.
 *
 * @param session the client session with which to associate this operation
 * @param pipeline the aggregation pipeline
 * @param block the publisher configuration block.
 * @return an iterable containing the result of the aggregation operation
 * @since 2.0.0
 * @see MonktDatabase.aggregate
 */
suspend fun MonktDatabase.aggregateSuspend(
    vararg pipeline: BsonDocumentBlock,
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<BsonDocument> {
    return aggregateSuspend(pipeline.map { document(it) }, session, block)
}

/* ============= ------------------ ============= */

/* ============= - dropCollection - ============= */

/**
 * Gets a collection then drops it from the database.
 *
 * @param name the name of the collection to drop
 * @param session the client session with which to associate this operation.
 * @return the collection
 * @since 2.0.0
 * @see MonktDatabase.getCollection
 * @see MonktCollection.drop
 */
suspend fun MonktDatabase.dropCollectionSuspend(
    name: String,
    session: ClientSession? = null
) {
    getCollection(name).dropSuspend(session)
}
