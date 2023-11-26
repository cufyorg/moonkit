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
package org.cufy.mongodb.gridfs

import kotlinx.datetime.toKotlinInstant
import org.cufy.bson.kt

/* ============= ------------------ ============= */

internal typealias JavaMongoFile =
        com.mongodb.client.gridfs.model.GridFSFile

/**
 * Create a new [MongoFile] instance wrapping
 * this file instance.
 *
 * @since 2.0.0
 */
val JavaMongoFile.kt: MongoFile
    get() {
        return MongoFile(
            id = id.kt,
            filename = filename,
            length = length,
            chunkSize = chunkSize,
            uploadDate = uploadDate.toInstant().toKotlinInstant(),
            metadata = metadata?.toBsonDocument()?.kt
        )
    }

/* ============= ------------------ ============= */
