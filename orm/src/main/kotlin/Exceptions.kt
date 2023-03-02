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

/**
 * Base monkt exception.
 *
 * Thrown for expected usage errors.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class MonktException : RuntimeException {
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(
        message: String? = null,
        cause: Throwable? = null,
        enableSuppression: Boolean = true,
        writableStackTrace: Boolean = true
    ) : super(message, cause, enableSuppression, writableStackTrace)

    companion object {
        private const val serialVersionUID: Long = 5786501671027284894L
    }
}

/**
 * Base monkt error.
 *
 * Thrown for unexpected library errors.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class MonktError : Error {
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(
        message: String? = null,
        cause: Throwable? = null,
        enableSuppression: Boolean = true,
        writableStackTrace: Boolean = true
    ) : super(message, cause, enableSuppression, writableStackTrace)

    companion object {
        private const val serialVersionUID: Long = -2534605968066431192L
    }
}

/**
 * Thrown if a validation error thrown by monkt.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ValidationException : MonktException {
    /**
     * The message label.
     */
    val label: String?

    /**
     * The message content.
     */
    val content: String?

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        enableSuppression: Boolean = true,
        writableStackTrace: Boolean = true
    ) : super(message, cause, enableSuppression, writableStackTrace) {
        content = message
        label = null
    }

    constructor(
        label: String?,
        message: String?,
        cause: Throwable? = null,
        enableSuppression: Boolean = true,
        writableStackTrace: Boolean = true
    ) : super(formatMessage(label, message), cause, enableSuppression, writableStackTrace) {
        this.content = message
        this.label = label
    }

    companion object {
        private const val serialVersionUID: Long = -709743821901577253L

        private fun formatMessage(label: String?, content: String?): String? {
            val noLabel = label.isNullOrBlank()
            val noContent = content.isNullOrBlank()
            return when {
                noLabel && noContent -> null
                noLabel -> content
                noContent -> label
                else -> "$label: $content"
            }
        }
    }
}
