/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.main

import io.verik.core.config.FileConfig
import io.verik.core.config.PkgConfig

data class FileTable(
        val pkgs: List<FileTablePkg>
) {

    companion object {

        operator fun invoke(pkgConfigs: List<Pair<PkgConfig, List<FileConfig>>>): FileTable {
            val pkgs = ArrayList<FileTablePkg>()
            for ((pkgConfig, fileConfigs) in pkgConfigs) {
                val files = ArrayList<FileTableFile>()
                for (fileConfig in fileConfigs) {
                    files.add(FileTableFile(fileConfig, Symbol(pkgs.size + 1, files.size + 1)))
                }
                pkgs.add(FileTablePkg(pkgConfig, Symbol(pkgs.size + 1), files))
            }
            return FileTable(pkgs)
        }
    }
}

data class FileTablePkg(
        val config: PkgConfig,
        val symbol: Symbol,
        val files: List<FileTableFile>
)

data class FileTableFile(
        val config: FileConfig,
        val symbol: Symbol
)
