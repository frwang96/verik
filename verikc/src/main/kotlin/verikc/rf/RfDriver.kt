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

package verikc.rf

import verikc.main.config.ProjectConfig
import verikc.rf.ast.RfCompilationUnit
import verikc.rf.ast.RfFile
import verikc.rf.ast.RfPkg
import verikc.rf.check.RfCheckerConnection
import verikc.rf.reify.RfReifier
import verikc.rf.symbol.RfSymbolTable
import verikc.rf.symbol.RfSymbolTableBuilder
import verikc.vk.ast.VkCompilationUnit

object RfDriver {

    fun drive(projectConfig: ProjectConfig, compilationUnit: VkCompilationUnit): RfCompilationUnit {
        val pkgs = ArrayList<RfPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<RfFile>()
            for (file in pkg.files) {
                files.add(RfFile(file))
            }
            pkgs.add(RfPkg(pkg.pkg, files))
        }
        return RfCompilationUnit(pkgs).also { processCompilationUnit(projectConfig, it) }
    }

    private fun processCompilationUnit(projectConfig: ProjectConfig, compilationUnit: RfCompilationUnit) {
        val symbolTable = RfSymbolTable()

        // build symbol table
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                RfSymbolTableBuilder.buildFile(compilationUnit.file(file), symbolTable)
            }
        }

        // reify types
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                RfReifier.reifyFile(compilationUnit.file(file), symbolTable)
            }
        }

        // check connections
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                RfCheckerConnection.check(compilationUnit.file(file), symbolTable)
            }
        }
    }
}
