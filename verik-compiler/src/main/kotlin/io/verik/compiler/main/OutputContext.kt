/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.main

import io.verik.compiler.common.TextFile

class OutputContext {

    lateinit var configTextFile: TextFile
    var targetPackageTextFile: TextFile? = null
    var basicPackageSourceTextFiles: List<TextFile> = listOf()
    var basicPackageSourceTextFilesLabeled: List<TextFile> = listOf()
    var rootPackageSourceTextFiles: List<TextFile> = listOf()
    var rootPackageSourceTextFilesLabeled: List<TextFile> = listOf()
    var packageTextFiles: List<TextFile> = listOf()
    lateinit var sourcesTextFile: TextFile

    fun getTextFiles(): List<TextFile> {
        val textFiles = ArrayList<TextFile>()
        textFiles.add(configTextFile)
        targetPackageTextFile?.let { textFiles.add(it) }
        textFiles.addAll(basicPackageSourceTextFiles)
        textFiles.addAll(basicPackageSourceTextFilesLabeled)
        textFiles.addAll(rootPackageSourceTextFiles)
        textFiles.addAll(rootPackageSourceTextFilesLabeled)
        textFiles.addAll(packageTextFiles)
        textFiles.add(sourcesTextFile)
        return textFiles.sortedBy { it.path }
    }
}
