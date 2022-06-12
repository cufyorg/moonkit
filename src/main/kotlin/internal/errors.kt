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
package org.cufy.mangaka.internal

import org.cufy.mangaka.MangakaError
import org.cufy.mangaka.MangakaException

/*
Yeah, I know.
 */

internal fun metadataAlreadyInitializedError(value: Any?): Nothing {
    /*
    The only official way to set the metadata would
    be through creating the value with the model.
    So, it's an unexpected error.
     */
    throw MangakaError(
        "Metadata already initialized for value $value"
    )
}

internal fun missingMetadataError(value: Any): Nothing {
    /*
    The developer most likely didn't construct the
    value using the model. So, it's a usage error.
     */
    throw MangakaException(
        "Metadata is not set for $value of class ${value.javaClass}"
    )
}

internal fun constructorFailureError(modelName: String): Nothing {
    /*
    The developer most likely didn't specify a
    proper constructor for the model. If any at
    all. So, it's a usage error.
     */
    throw MangakaException(
        "The constructor of the model $modelName failed to construct a new instance"
    )
}

internal fun formatFailureError(modelName: String): Nothing {
    /*
    The developer most likely didn't specify a
    proper formatter for the model. If any at
    all. So, it's a usage error.
     */
    throw MangakaException(
        "The formatter of the model $modelName failed to format a document."
    )
}

internal fun valueIsDeletedError(value: Any): Nothing {
    /*
    The library forgets the document after deletion.
    So, it's a usage error.
     */
    throw MangakaException(
        "The document already deleted"
    )
}

internal fun mangakaAlreadyInitializedError(): Nothing {
    /*
    The library don't initialize any mangaka
    instance by itself. So, it's a usage error.
     */
    throw MangakaException(
        "Mangaka instance has already been initialized"
    )
}

internal fun nonNormalizableIdError(value: Any): Nothing {
    /*
    It is a usage fault expecting the library to
    handle verbose id values.
     */
    throw MangakaException("Unsupported id type: $value")
}

internal fun constructorStub(path: String): Nothing {
    throw MangakaException(
        "Constructor not defined at $path"
    )
}

internal fun formatterStub(path: String): Nothing {
    throw MangakaException(
        "Formatter not defined at $path"
    )
}

internal fun getterStub(path: String): Nothing {
    throw MangakaException(
        "Getter not defined at $path"
    )
}

internal fun setterStub(path: String): Nothing {
    throw MangakaException(
        "Setter not defined at $path"
    )
}
