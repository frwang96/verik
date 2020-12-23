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

package verikc.ps

import verikc.base.config.PkgConfig
import verikc.base.config.ProjectConfig
import verikc.main.StatusPrinter
import verikc.ps.ast.PsCompilationUnit
import verikc.ps.ast.PsFile
import verikc.ps.ast.PsPkg
import verikc.ps.pass.PsPass
import verikc.ps.symbol.PsSymbolTable
import verikc.ps.symbol.PsSymbolTableBuilder
import verikc.rf.ast.RfCompilationUnit
import verikc.sv.ast.SvFile
import verikc.sv.build.SvSourceBuilder
import verikc.sv.build.indent
import java.io.File

object PsDriver {

    fun drive(projectConfig: ProjectConfig, compilationUnit: RfCompilationUnit) {
        val symbolTable = PsSymbolTable()
        build(compilationUnit).also {
            pass(it, symbolTable)
            extract(projectConfig, it, symbolTable)
        }
    }

    fun build(compilationUnit: RfCompilationUnit): PsCompilationUnit {
        val pkgs = ArrayList<PsPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<PsFile>()
            for (file in pkg.files) {
                files.add(PsFile(file))
            }
            pkgs.add(PsPkg(pkg.config, files))
        }
        return PsCompilationUnit(pkgs)
    }

    fun pass(compilationUnit: PsCompilationUnit, symbolTable: PsSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            PsSymbolTableBuilder.buildPkg(pkg, symbolTable)
        }

        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                PsPass.passFile(file, symbolTable)
            }
        }
    }

    private fun extract(projectConfig: ProjectConfig, compilationUnit: PsCompilationUnit, symbolTable: PsSymbolTable) {
        StatusPrinter.info("writing output files", 1)
        val order = ArrayList<File>()
        for (pkg in compilationUnit.pkgs) {
            val pkgFiles = ArrayList<String>()
            for (file in pkg.files) {
                val pkgFile = file.extractPkgFile()
                if (pkgFile != null) {
                    buildSourceFile(projectConfig, file.config.file, file.config.outPkgFile, pkgFile)
                    pkgFiles.add(file.config.outPkgFile.name)
                }
            }
            if (pkgFiles.isNotEmpty()) {
                buildPkgWrapperFile(projectConfig, pkg.config, pkgFiles)
                order.add(pkg.config.pkgWrapperFile.relativeTo(projectConfig.pathConfig.outDir))
            }
        }
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                val moduleFile = file.extractModuleFile(symbolTable)
                if (moduleFile != null) {
                    buildSourceFile(projectConfig, file.config.file, file.config.outModuleFile, moduleFile)
                    order.add(file.config.outModuleFile.relativeTo(projectConfig.pathConfig.outDir))
                }
            }
        }
        buildOrderFile(projectConfig, order)
    }

    private fun buildSourceFile(projectConfig: ProjectConfig, inFile: File, outFile: File, file: SvFile) {
        val fileHeader = projectConfig.header(inFile, outFile)
        val builder = SvSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)
        file.build(builder)
        outFile.parentFile.mkdirs()
        outFile.writeText(builder.toString())

        StatusPrinter.info("+ ${outFile.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
    }

    private fun buildPkgWrapperFile(projectConfig: ProjectConfig, pkgConfig: PkgConfig, pkgFiles: List<String>) {
        val fileHeader = projectConfig.header(pkgConfig.dir, pkgConfig.pkgWrapperFile)
        val builder = SvSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)

        builder.appendln("package ${pkgConfig.identifierSv};")
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

        StatusPrinter.info("+ ${pkgConfig.pkgWrapperFile.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
    }

    private fun buildOrderFile(projectConfig: ProjectConfig, order: List<File>) {
        val builder = StringBuilder()
        builder.appendLine(projectConfig.compileConfig.top)
        order.forEach {
            builder.appendLine(it)
        }
        projectConfig.pathConfig.orderFile.writeText(builder.toString())

        StatusPrinter.info("+ ${projectConfig.pathConfig.orderFile.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
    }
}
