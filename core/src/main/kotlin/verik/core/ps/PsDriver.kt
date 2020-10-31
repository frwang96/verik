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

package verik.core.ps

import verik.core.base.ast.LineException
import verik.core.main.FileHeaderBuilder
import verik.core.main.StatusPrinter
import verik.core.main.config.PkgConfig
import verik.core.main.config.ProjectConfig
import verik.core.ps.ast.PsCompilationUnit
import verik.core.ps.ast.PsFile
import verik.core.ps.ast.PsPkg
import verik.core.ps.symbol.PsSymbolTable
import verik.core.ps.symbol.PsSymbolTableBuilder
import verik.core.rf.ast.RfCompilationUnit
import verik.core.sv.ast.SvFile
import verik.core.sv.build.SvSourceBuilder
import verik.core.sv.build.indent
import java.io.File

object PsDriver {

    fun drive(projectConfig: ProjectConfig, compilationUnit: RfCompilationUnit) {
        val pkgs = ArrayList<PsPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<PsFile>()
            for (file in pkg.files) {
                try {
                    files.add(PsFile(file))
                } catch (exception: LineException) {
                    exception.file = file.file
                    throw exception
                }
            }
            pkgs.add(PsPkg(pkg.pkg, files))
        }
        buildCompilationUnit(projectConfig, PsCompilationUnit(pkgs))
    }

    private fun buildCompilationUnit(projectConfig: ProjectConfig, compilationUnit: PsCompilationUnit) {
        val symbolTable = PsSymbolTable()
        projectConfig.symbolContext.processFiles {
            PsSymbolTableBuilder.buildFile(compilationUnit.file(it), symbolTable)
        }

        StatusPrinter.info("writing output files", 1)
        val order = ArrayList<File>()
        for (pkg in compilationUnit.pkgs) {
            val pkgFiles = ArrayList<String>()
            for (file in pkg.files) {
                val fileConfig = projectConfig.symbolContext.fileConfig(file.file)
                val pkgFile =  file.extractPkgFile()
                if (pkgFile != null) {
                    buildFile(projectConfig, fileConfig.file, fileConfig.outPkgFile, pkgFile)
                    pkgFiles.add(fileConfig.outPkgFile.name)
                }
            }
            if (pkgFiles.isNotEmpty()) {
                val pkgConfig = projectConfig.symbolContext.pkgConfig(pkg.pkg)
                buildPkgWrapperFile(projectConfig, pkgConfig, pkgFiles)
                order.add(pkgConfig.pkgWrapperFile.relativeTo(projectConfig.buildOutDir))
            }
        }
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                val fileConfig = projectConfig.symbolContext.fileConfig(file.file)
                val moduleFile = file.extractModuleFile(symbolTable)
                if (moduleFile != null) {
                    buildFile(projectConfig, fileConfig.file, fileConfig.outModuleFile, moduleFile)
                    order.add(fileConfig.outModuleFile.relativeTo(projectConfig.buildOutDir))
                }
            }
        }
        buildOrderFile(projectConfig, order)
    }

    private fun buildFile(
            projectConfig: ProjectConfig,
            inFile: File,
            outFile: File,
            file: SvFile
    ) {
        val fileHeader = FileHeaderBuilder.build(projectConfig, inFile, outFile)
        val builder = SvSourceBuilder(projectConfig.compile.labelLines, fileHeader)
        file.build(builder)
        outFile.parentFile.mkdirs()
        outFile.writeText(builder.toString())

        StatusPrinter.info("+ ${outFile.relativeTo(projectConfig.projectDir)}", 2)
    }

    private fun buildPkgWrapperFile(
            projectConfig: ProjectConfig,
            pkgConfig: PkgConfig,
            pkgFiles: List<String>
    ) {
        val fileHeader = FileHeaderBuilder.build(projectConfig, pkgConfig.dir, pkgConfig.pkgWrapperFile)
        val builder = SvSourceBuilder(false, fileHeader)

        builder.appendln("package ${pkgConfig.pkgSv};")
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")
            builder.appendln()
            pkgFiles.forEach {
                builder.appendln("`include \"$it\"")
            }
        }
        builder.appendln("endpackage")
        pkgConfig.pkgWrapperFile.parentFile.mkdirs()
        pkgConfig.pkgWrapperFile.writeText(builder.toString())

        StatusPrinter.info("+ ${pkgConfig.pkgWrapperFile.relativeTo(projectConfig.projectDir)}", 2)
    }

    private fun buildOrderFile(
            projectConfig: ProjectConfig,
            order: List<File>
    ) {
        val builder = StringBuilder()
        builder.appendLine(projectConfig.compile.top)
        order.forEach {
            builder.appendLine(it)
        }
        projectConfig.orderFile.writeText(builder.toString())

        StatusPrinter.info("+ ${projectConfig.orderFile.relativeTo(projectConfig.projectDir)}", 2)
    }
}