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

package verik.core.it.drive

import verik.core.base.LineException
import verik.core.it.ItCompilationUnit
import verik.core.it.ItFile
import verik.core.it.ItPkg
import verik.core.it.check.ItConnectionChecker
import verik.core.it.reify.ItReifier
import verik.core.it.symbol.ItSymbolTable
import verik.core.it.symbol.ItSymbolTableBuilder
import verik.core.main.FileHeaderBuilder
import verik.core.main.StatusPrinter
import verik.core.main.config.ProjectConfig
import verik.core.sv.build.SvSourceBuilder
import verik.core.vk.VkCompilationUnit

object ItDriver {

    fun drive(projectConfig: ProjectConfig, compilationUnit: VkCompilationUnit) {
        val pkgs = ArrayList<ItPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<ItFile>()
            for (file in pkg.files) {
                try {
                    files.add(ItFile(file))
                } catch (exception: LineException) {
                    exception.file = file.file
                    throw exception
                }
            }
            pkgs.add(ItPkg(pkg.pkg, files))
        }
        processFiles(projectConfig, ItCompilationUnit(pkgs))
    }

    private fun processFiles(projectConfig: ProjectConfig, compilationUnit: ItCompilationUnit) {
        val symbolTable = ItSymbolTable()

        // build symbol table
        projectConfig.symbolContext.processFiles {
            ItSymbolTableBuilder.buildFile(compilationUnit.file(it), symbolTable)
        }

        // reify types
        projectConfig.symbolContext.processFiles {
            ItReifier.reify(compilationUnit.file(it), symbolTable)
        }

        // check connections
        projectConfig.symbolContext.processFiles {
            ItConnectionChecker.check(compilationUnit.file(it), symbolTable)
        }

        // extract files
        projectConfig.symbolContext.processFiles {
            val fileConfig = projectConfig.symbolContext.fileConfig(it)
            val svFile = compilationUnit.file(it).extract(symbolTable)
            val fileHeader = FileHeaderBuilder.build(projectConfig, fileConfig.file, fileConfig.outFile)
            val builder = SvSourceBuilder(projectConfig.compile.labelLines, fileHeader)
            svFile.build(builder)

            fileConfig.outFile.parentFile.mkdirs()
            fileConfig.outFile.writeText(builder.toString())

            StatusPrinter.info("+ ${fileConfig.outFile.relativeTo(projectConfig.projectDir)}", 2)
        }
    }
}