/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

import io.verik.importer.common.TextFile

/**
 * Context that stores the output text files to be written to the build directory.
 */
class OutputContext {

    var preprocessorTextFile: TextFile? = null
    lateinit var reportTextFile: TextFile
    var sourceTextFiles: List<TextFile> = listOf()

    fun getTextFiles(): List<TextFile> {
        val textFiles = ArrayList<TextFile>()
        preprocessorTextFile?.let { textFiles.add(it) }
        textFiles.add(reportTextFile)
        textFiles.addAll(sourceTextFiles)
        return textFiles.sortedBy { it.path }
    }
}
