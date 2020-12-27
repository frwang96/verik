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

package verikc.sv

import verikc.base.config.ProjectConfig
import verikc.main.StatusPrinter
import verikc.ps.ast.PsCompilationUnit
import verikc.sv.ast.SvCompilationUnit
import verikc.sv.ast.SvFile
import verikc.sv.ast.SvPkg
import verikc.sv.build.SvSourceBuilder
import verikc.sv.build.indent
import verikc.sv.symbol.SvSymbolTable
import verikc.sv.symbol.SvSymbolTableBuilder
import java.io.File

object SvStageDriver {

    fun extract(compilationUnit: PsCompilationUnit): SvCompilationUnit {
        val symbolTable = SvSymbolTable()
        SvSymbolTableBuilder.build(compilationUnit, symbolTable)
        return compilationUnit.extract(symbolTable)
    }

    fun build(compilationUnit: SvCompilationUnit, projectConfig: ProjectConfig) {
        StatusPrinter.info("writing output files")
        val order = ArrayList<File>()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.pkgFiles) {
                buildSourceFile(projectConfig, file.config.file, file.config.outPkgFile, file)
            }
            if (pkg.pkgFiles.isNotEmpty()) {
                buildPkgWrapperFile(projectConfig, pkg)
                order.add(pkg.config.pkgWrapperFile.relativeTo(projectConfig.pathConfig.outDir))
            }
        }
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.moduleFiles) {
                buildSourceFile(projectConfig, file.config.file, file.config.outModuleFile, file)
                order.add(file.config.outModuleFile.relativeTo(projectConfig.pathConfig.outDir))
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

        StatusPrinter.info("+ ${outFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
    }

    private fun buildPkgWrapperFile(projectConfig: ProjectConfig, pkg: SvPkg) {
        val fileHeader = projectConfig.header(pkg.config.dir, pkg.config.pkgWrapperFile)
        val builder = SvSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)

        builder.appendln("package ${pkg.config.identifierSv};")
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")
            builder.appendln()
            pkg.pkgFiles.forEach {
                builder.appendln("`include \"${it.config.outPkgFile.name}\"")
            }
        }
        builder.appendln("endpackage")
        pkg.config.pkgWrapperFile.parentFile.mkdirs()
        pkg.config.pkgWrapperFile.writeText(builder.toString())

        StatusPrinter.info("+ ${pkg.config.pkgWrapperFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
    }

    private fun buildOrderFile(projectConfig: ProjectConfig, order: List<File>) {
        val builder = StringBuilder()
        builder.appendLine(projectConfig.compileConfig.top.substring(1))
        order.forEach {
            builder.appendLine(it)
        }
        projectConfig.pathConfig.orderFile.writeText(builder.toString())

        StatusPrinter.info("+ ${projectConfig.pathConfig.orderFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
    }
}