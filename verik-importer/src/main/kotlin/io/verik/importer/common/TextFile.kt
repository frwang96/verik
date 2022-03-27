/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.common

import io.verik.importer.message.SourceLocation
import java.nio.file.Path

class TextFile(
    val path: Path,
    val content: String
) {

    fun getEndLocation(): SourceLocation {
        val lines = content.count { it == '\n' }
        return SourceLocation(path, lines + 1, 0)
    }
}
