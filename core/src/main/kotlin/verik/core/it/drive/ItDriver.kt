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

        val itCompilationUnit = ItCompilationUnit(pkgs)
        val symbolTable = reify(projectConfig, itCompilationUnit)
        extract(projectConfig, itCompilationUnit, symbolTable)
    }

    private fun reify(projectConfig: ProjectConfig, compilationUnit: ItCompilationUnit): ItSymbolTable {
        val symbolTable = ItSymbolTable()
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                try {
                    ItSymbolTableBuilder.buildFile(compilationUnit.file(file), symbolTable)
                } catch (exception: LineException) {
                    exception.file = file
                    throw exception
                }
            }
        }

        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                try {
                    ItReifier.reify(compilationUnit.file(file), symbolTable)
                } catch (exception: LineException) {
                    exception.file = file
                    throw exception
                }
            }
        }

        return symbolTable
    }

    private fun extract(projectConfig: ProjectConfig, compilationUnit: ItCompilationUnit, symbolTable: ItSymbolTable) {
        StatusPrinter.info("writing output files", 1)
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                try {
                    val fileConfig = projectConfig.symbolContext.fileConfig(file)
                    val svFile = compilationUnit.file(file).extract(symbolTable)
                    val fileHeader = FileHeaderBuilder.build(projectConfig, fileConfig.file, fileConfig.outFile)
                    val builder = SvSourceBuilder(projectConfig.compile.labelLines, fileHeader)
                    svFile.build(builder)

                    fileConfig.outFile.parentFile.mkdirs()
                    fileConfig.outFile.writeText(builder.toString())

                    StatusPrinter.info("+ ${fileConfig.outFile.relativeTo(projectConfig.projectDir)}", 2)
                } catch (exception: LineException) {
                    exception.file = file
                    throw exception
                }
            }
        }
    }
}