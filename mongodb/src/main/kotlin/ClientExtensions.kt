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

import com.mongodb.ClientSessionOptions
import com.mongodb.reactivestreams.client.ClientSession
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.cufy.bson.BsonDocument

/* =========== - listDatabaseNames  - =========== */

/**
 * Get a list of the database names
 *
 * @param session the client session with which to associate this operation
 * @return an iterable containing all the names of all the databases
 * @since 2.0.0
 * @see MonktClient.listDatabaseNames
 */
suspend fun MonktClient.listDatabaseNamesSuspend(
    session: ClientSession? = null
): List<String> {
    val publisher = listDatabaseNames(session)
    return publisher.asFlow().toList()
}

/* ============= - listDatabases  - ============= */

/**
 * Gets the list of databases
 *
 * @param session the client session with which to associate this operation
 * @return the databases list
 * @param block the publisher block.
 * @since 2.0.0
 * @see MonktClient.listDatabases
 */
suspend fun MonktClient.listDatabasesSuspend(
    session: ClientSession? = null,
    block: ListDatabasesPublisherScope.() -> Unit
): List<BsonDocument> {
    var publisher = listDatabases(session)
    publisher = publisher.configure(block)
    return publisher.asFlow().toList()
}

/* ============= ----- watch  ----- ============= */

// TODO watch

/* ============= -- startSession -- ============= */

/**
 * Creates a client session.
 *
 * @param options the options for the client session
 * @return the client session.
 * @since 2.0.0
 */
suspend fun MonktClient.startSessionSuspend(
    options: ClientSessionOptions
): ClientSession {
    val publisher = startSession(options)
    return publisher.awaitSingle()
}

/**
 * Creates a client session.
 *
 * @param block the options block for the client session
 * @return the client session.
 * @since 2.0.0
 */
suspend fun MonktClient.startSessionSuspend(
    block: ClientSessionOptionsScope.() -> Unit = {}
): ClientSession {
    var options = ClientSessionOptions.builder()
    options = options.configure(block)
    return startSessionSuspend(options.build())
}

/* ============= ------------------ ============= */
