/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

import io.verik.importer.common.TextFile
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isReadable

object IncludedTextFileReader {

    private val INCLUDE_PATTERN = Regex("\\s*`include\\s+\"(.+)\".*")

    fun read(
        inputFileContexts: List<InputFileContext>,
        includeDirs: List<Path>
    ): HashMap<Path, TextFile> {
        val includedPaths = ArrayList<Path>()
        inputFileContexts.forEach {
            includedPaths.addAll(getIncludedPaths(it.textFile, includeDirs))
        }
        val includedTextFiles = HashMap<Path, TextFile>()
        while (includedPaths.isNotEmpty()) {
            val includedPath = includedPaths.removeLast()
            if (includedPath !in includedTextFiles) {
                val textFile = Platform.readTextFile(includedPath)
                includedTextFiles[includedPath] = textFile
                includedPaths.addAll(getIncludedPaths(textFile, includeDirs))
            }
        }
        return includedTextFiles
    }

    private fun getIncludedPaths(textFile: TextFile, includeDirs: List<Path>): List<Path> {
        val includedPaths = ArrayList<Path>()
        textFile.content.lines().forEachIndexed { index, line ->
            val match = INCLUDE_PATTERN.matchEntire(line)
            if (match != null) {
                val location = SourceLocation(textFile.path, index + 1, 0)
                val path = Paths.get(match.destructured.component1())
                if (path.isAbsolute) {
                    if (path.isReadable()) {
                        includedPaths.add(path)
                    } else {
                        Messages.INCLUDED_FILE_NOT_FOUND.on(location, path)
                    }
                } else {
                    val includedPath = searchIncludeDirs(path, textFile, includeDirs)
                    if (includedPath != null) {
                        includedPaths.add(includedPath)
                    } else {
                        Messages.INCLUDED_FILE_NOT_FOUND.on(location, path)
                    }
                }
            }
        }
        return includedPaths
    }

    private fun searchIncludeDirs(path: Path, textFile: TextFile, includeDirs: List<Path>): Path? {
        val textFileResolvedPath = textFile.path.resolveSibling(path)
        if (textFileResolvedPath.isReadable()) {
            return textFileResolvedPath.toAbsolutePath()
        }
        includeDirs.forEach {
            val resolvedPath = it.resolve(path)
            if (it.isReadable()) {
                return resolvedPath.toAbsolutePath()
            }
        }
        return null
    }
}
