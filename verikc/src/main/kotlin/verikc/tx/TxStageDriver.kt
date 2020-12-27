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

package verikc.tx

import verikc.base.config.ProjectConfig
import verikc.main.StatusPrinter
import verikc.sv.ast.SvCompilationUnit
import verikc.sv.ast.SvFile
import verikc.sv.ast.SvPkg
import verikc.sv.build.SvSourceBuilder
import verikc.sv.build.indent
import verikc.tx.ast.TxCompilationUnit
import verikc.tx.ast.TxFile
import verikc.tx.ast.TxPkg
import java.io.File

object TxStageDriver {

    fun build(compilationUnit: SvCompilationUnit, projectConfig: ProjectConfig): TxCompilationUnit {
        val pkgs = ArrayList<TxPkg>()
        val order = ArrayList<File>()
        for (pkg in compilationUnit.pkgs) {
            val pkgFiles = pkg.pkgFiles.map {
                TxFile(it.config, buildFileString(projectConfig, it.config.file, it.config.outPkgFile, it))
            }
            val wrapperString = if (pkgFiles.isNotEmpty()) {
                order.add(pkg.config.pkgWrapperFile.relativeTo(projectConfig.pathConfig.outDir))
                buildWrapperString(projectConfig, pkg)
            } else null

            val moduleFiles = pkg.moduleFiles.map {
                order.add(it.config.outModuleFile.relativeTo(projectConfig.pathConfig.outDir))
                TxFile(it.config, buildFileString(projectConfig, it.config.file, it.config.outModuleFile, it))
            }

            pkgs.add(TxPkg(pkg.config, moduleFiles, pkgFiles, wrapperString))
        }
        val orderString = buildOrderString(projectConfig, order)
        return TxCompilationUnit(pkgs, orderString)
    }

    fun write(compilationUnit: TxCompilationUnit, projectConfig: ProjectConfig) {
        StatusPrinter.info("writing output files")
        projectConfig.pathConfig.orderFile.parentFile.mkdirs()
        projectConfig.pathConfig.orderFile.writeText(compilationUnit.orderString)
        StatusPrinter.info("+ ${projectConfig.pathConfig.orderFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)

        for (pkg in compilationUnit.pkgs) {
            if (pkg.wrapperString != null) {
                pkg.config.pkgWrapperFile.parentFile.mkdirs()
                pkg.config.pkgWrapperFile.writeText(pkg.wrapperString)
                StatusPrinter.info("+ ${pkg.config.pkgWrapperFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
            }
            for (file in pkg.moduleFiles) {
                file.config.outModuleFile.parentFile.mkdirs()
                file.config.outModuleFile.writeText(file.string)
                StatusPrinter.info("+ ${file.config.outModuleFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
            }
            for (file in pkg.pkgFiles) {
                file.config.outPkgFile.parentFile.mkdirs()
                file.config.outPkgFile.writeText(file.string)
                StatusPrinter.info("+ ${file.config.outPkgFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
            }
        }
    }

    private fun buildFileString(projectConfig: ProjectConfig, inFile: File, outFile: File, file: SvFile): String {
        val fileHeader = projectConfig.header(inFile, outFile)
        val builder = SvSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)
        file.build(builder)
        return builder.toString()
    }

    private fun buildWrapperString(projectConfig: ProjectConfig, pkg: SvPkg): String {
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
        return builder.toString()
    }

    private fun buildOrderString(projectConfig: ProjectConfig, order: List<File>): String {
        val builder = StringBuilder()
        builder.appendLine(projectConfig.compileConfig.top.substring(1))
        order.forEach {
            builder.appendLine(it)
        }
        return builder.toString()
    }
}