/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.common

import java.nio.file.Path

/**
 * Data class that represents a text file.
 */
data class TextFile(
    val path: Path,
    val content: String
)
