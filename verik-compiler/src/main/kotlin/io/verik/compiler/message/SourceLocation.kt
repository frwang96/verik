/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.message

import java.nio.file.Path

/**
 * Location within the original source code.
 */
data class SourceLocation(
    val path: Path,
    val line: Int,
    val column: Int
) {

    companion object {

        val NULL = SourceLocation(Path.of("."), 0, 0)
    }
}
