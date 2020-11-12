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

package verik.core.rf

import verik.core.base.ast.LineException
import verik.core.main.config.ProjectConfig
import verik.core.rf.ast.RfCompilationUnit
import verik.core.rf.ast.RfFile
import verik.core.rf.ast.RfPkg
import verik.core.rf.check.RfCheckerConnection
import verik.core.rf.reify.RfReifier
import verik.core.rf.symbol.RfSymbolTable
import verik.core.rf.symbol.RfSymbolTableBuilder
import verik.core.vk.ast.VkCompilationUnit

object RfDriver {

    fun drive(projectConfig: ProjectConfig, compilationUnit: VkCompilationUnit): RfCompilationUnit {
        val pkgs = ArrayList<RfPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<RfFile>()
            for (file in pkg.files) {
                try {
                    files.add(RfFile(file))
                } catch (exception: LineException) {
                    exception.file = file.file
                    throw exception
                }
            }
            pkgs.add(RfPkg(pkg.pkg, files))
        }
        return RfCompilationUnit(pkgs).also { processCompilationUnit(projectConfig, it) }
    }

    private fun processCompilationUnit(projectConfig: ProjectConfig, compilationUnit: RfCompilationUnit) {
        val symbolTable = RfSymbolTable()

        // build symbol table
        projectConfig.symbolContext.processFiles {
            RfSymbolTableBuilder.buildFile(compilationUnit.file(it), symbolTable)
        }

        // reify types
        projectConfig.symbolContext.processFiles {
            RfReifier.reifyFile(compilationUnit.file(it), symbolTable)
        }

        // check connections
        projectConfig.symbolContext.processFiles {
            RfCheckerConnection.check(compilationUnit.file(it), symbolTable)
        }
    }
}