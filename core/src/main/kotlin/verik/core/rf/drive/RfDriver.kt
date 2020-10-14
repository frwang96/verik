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

package verik.core.rf.drive

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.main.FileHeaderBuilder
import verik.core.main.StatusPrinter
import verik.core.main.config.ProjectConfig
import verik.core.rf.RfCompilationUnit
import verik.core.rf.RfFile
import verik.core.rf.RfPkg
import verik.core.rf.check.RfConnectionChecker
import verik.core.rf.reify.RfReifier
import verik.core.rf.symbol.RfSymbolTable
import verik.core.rf.symbol.RfSymbolTableBuilder
import verik.core.sv.build.SvSourceBuilder
import verik.core.vk.VkCompilationUnit

object RfDriver {

    fun drive(projectConfig: ProjectConfig, compilationUnit: VkCompilationUnit) {
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
        processFiles(projectConfig, RfCompilationUnit(pkgs))
    }

    private fun processFiles(projectConfig: ProjectConfig, compilationUnit: RfCompilationUnit) {
        val symbolTable = RfSymbolTable()

        // build symbol table
        projectConfig.symbolContext.processFiles {
            RfSymbolTableBuilder.buildFile(compilationUnit.file(it), symbolTable)
        }

        // reify types
        projectConfig.symbolContext.processFiles {
            RfReifier.reify(compilationUnit.file(it), symbolTable)
        }

        // check connections
        projectConfig.symbolContext.processFiles {
            RfConnectionChecker.check(compilationUnit.file(it), symbolTable)
        }

        // build files
        projectConfig.symbolContext.processFiles {
            buildModuleFile(projectConfig, compilationUnit, symbolTable, it)
            buildPkgFile(projectConfig, it)
        }
        buildOrderFile(projectConfig)
    }

    private fun buildModuleFile(
            projectConfig: ProjectConfig,
            compilationUnit: RfCompilationUnit,
            symbolTable: RfSymbolTable,
            file: Symbol,
    ) {
        val fileConfig = projectConfig.symbolContext.fileConfig(file)
        val svFile = compilationUnit.file(file).extract(symbolTable)
        val fileHeader = FileHeaderBuilder.build(projectConfig, fileConfig.file, fileConfig.outFileModule)
        val builder = SvSourceBuilder(projectConfig.compile.labelLines, fileHeader)
        svFile.build(builder)

        fileConfig.outFileModule.parentFile.mkdirs()
        fileConfig.outFileModule.writeText(builder.toString())

        StatusPrinter.info("+ ${fileConfig.outFileModule.relativeTo(projectConfig.projectDir)}", 2)
    }

    private fun buildPkgFile(
            projectConfig: ProjectConfig,
            file: Symbol,
    ) {
        val fileConfig = projectConfig.symbolContext.fileConfig(file)
        val fileHeader = FileHeaderBuilder.build(projectConfig, fileConfig.file, fileConfig.outFilePkg)
        val builder = SvSourceBuilder(projectConfig.compile.labelLines, fileHeader)

        fileConfig.outFilePkg.parentFile.mkdirs()
        fileConfig.outFilePkg.writeText(builder.toString())

        StatusPrinter.info("+ ${fileConfig.outFilePkg.relativeTo(projectConfig.projectDir)}", 2)
    }

    private fun buildOrderFile(
            projectConfig: ProjectConfig,
    ) {
        val builder = StringBuilder()
        builder.appendLine(projectConfig.compile.top)
        projectConfig.symbolContext.processFiles {
            val fileConfig = projectConfig.symbolContext.fileConfig(it)
            builder.appendLine(fileConfig.outFileModule.relativeTo(projectConfig.buildOutDir))
        }
        projectConfig.orderFile.writeText(builder.toString())

        StatusPrinter.info("+ ${projectConfig.orderFile.relativeTo(projectConfig.projectDir)}", 2)
    }
}