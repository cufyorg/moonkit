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
package org.cufy.mongodb.gridfs.internal

import kotlinx.coroutines.runBlocking
import org.cufy.mongodb.ExperimentalMongodbApi
import org.cufy.mongodb.gridfs.MongoDownload
import java.io.InputStream

@ExperimentalMongodbApi
internal class MongoDownloadInputStream(private val descriptor: MongoDownload) : InputStream() {
    override fun read(): Int {
        val bytes = ByteArray(1)
        runBlocking { descriptor.read(bytes) }
        return bytes[0].toInt()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return runBlocking { descriptor.read(b, off, len) }
    }

    override fun close() {
        descriptor.close()
    }
}
