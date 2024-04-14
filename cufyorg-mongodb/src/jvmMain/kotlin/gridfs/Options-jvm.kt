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

import org.cufy.bson.java
import org.cufy.mongodb.java
import java.util.concurrent.TimeUnit

/* ============= ------------------ ============= */

/**
 * Return a java version of this.
 */
val UploadOptions.java: JavaUploadOptions
    get() {
        return JavaUploadOptions()
            .chunkSizeBytes(chunkSizeBytes)
    }

/* ============= ------------------ ============= */

/**
 * Apply the given [options] to this publisher.
 */
fun JavaDownloadPublisher.apply(options: DownloadOptions): JavaDownloadPublisher {
    options.bufferSizeBytes?.let { bufferSizeBytes(it) }
    return this
}

/* ============= ------------------ ============= */

/**
 * Apply the given [options] to this publisher.
 */
fun JavaBucketFindPublisher.apply(options: BucketFindOptions): JavaBucketFindPublisher {
    limit(options.limit)
    skip(options.skip)
    sort(options.sort?.java)
    noCursorTimeout(options.noCursorTimeout)
    maxTime(options.maxTime.inWholeMilliseconds, TimeUnit.MILLISECONDS)
    collation(options.collation?.java)
    options.batchSize?.let { batchSize(it) }
    return this
}

/* ============= ------------------ ============= */
