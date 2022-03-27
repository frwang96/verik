/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.message.SourceLocation

data class PreprocessorFragment(
    val location: SourceLocation,
    val content: String,
    val isOriginal: Boolean
)
