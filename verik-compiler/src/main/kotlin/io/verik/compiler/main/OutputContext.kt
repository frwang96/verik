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

class OutputContext {

    lateinit var configTextFile: TextFile
    var targetPackageTextFile: TextFile? = null
    lateinit var basicPackageSourceTextFiles: List<TextFile>
    lateinit var rootPackageSourceTextFiles: List<TextFile>
    lateinit var packageTextFiles: List<TextFile>
    lateinit var sourcesTextFile: TextFile

    fun getTextFiles(): List<TextFile> {
        val textFiles = ArrayList<TextFile>()
        textFiles.add(configTextFile)
        targetPackageTextFile?.let { textFiles.add(it) }
        textFiles.addAll(basicPackageSourceTextFiles)
        textFiles.addAll(rootPackageSourceTextFiles)
        textFiles.addAll(packageTextFiles)
        textFiles.add(sourcesTextFile)
        return textFiles.sortedBy { it.path }
    }
}
