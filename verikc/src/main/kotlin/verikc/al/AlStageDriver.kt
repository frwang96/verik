/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.al

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import verikc.al.ast.AlCompilationUnit
import verikc.al.ast.AlFile
import verikc.al.ast.AlPkg
import verikc.base.config.ProjectConfig
import verikc.base.symbol.Symbol
import verikc.main.StatusPrinter

object AlStageDriver {

    fun parse(projectConfig: ProjectConfig): AlCompilationUnit {
        StatusPrinter.info("parsing input files", 1)

        val deferredFiles = HashMap<Symbol, Deferred<AlFile>>()
        for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
            for (fileConfig in pkgConfig.fileConfigs) {
                deferredFiles[fileConfig.symbol] = GlobalScope.async {
                    AlFileParser.parse(fileConfig).also {
                        StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
                    }
                }
            }
        }

        val pkgs = ArrayList<AlPkg>()
        for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
            val files = ArrayList<AlFile>()
            for (fileConfig in pkgConfig.fileConfigs) {
                runBlocking {
                    files.add(deferredFiles[fileConfig.symbol]!!.await())
                }
            }
            pkgs.add(AlPkg(pkgConfig, files))
        }

        return AlCompilationUnit(pkgs)
    }
}