/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.message.SourceLocation

/**
 * Preprocessor fragment that represents a fragment of a text file after preprocessing. [isOriginal] indicates that the
 * content comes directly from a text file and not a macro expansion.
 */
data class PreprocessorFragment(
    val location: SourceLocation,
    val content: String,
    val isOriginal: Boolean
)
