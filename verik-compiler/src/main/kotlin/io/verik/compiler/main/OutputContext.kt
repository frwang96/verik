/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import io.verik.compiler.common.TextFile

class OutputContext {

    lateinit var reportTextFile: TextFile
    var targetPackageTextFile: TextFile? = null
    var nonRootPackageTextFiles: List<TextFile> = listOf()
    var rootPackageTextFiles: List<TextFile> = listOf()
    lateinit var wrapperTextFile: TextFile

    fun getTextFiles(): List<TextFile> {
        val textFiles = ArrayList<TextFile>()
        textFiles.add(reportTextFile)
        targetPackageTextFile?.let { textFiles.add(it) }
        textFiles.addAll(nonRootPackageTextFiles)
        textFiles.addAll(rootPackageTextFiles)
        textFiles.add(wrapperTextFile)
        return textFiles.sortedBy { it.path }
    }

    fun getPackageTextFiles(): List<TextFile> {
        val textFiles = ArrayList<TextFile>()
        targetPackageTextFile?.let { textFiles.add(it) }
        textFiles.addAll(nonRootPackageTextFiles)
        textFiles.addAll(rootPackageTextFiles)
        return textFiles.sortedBy { it.path }
    }
}
