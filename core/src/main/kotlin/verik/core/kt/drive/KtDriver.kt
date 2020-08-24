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

package verik.core.kt.drive

import verik.core.al.AlRuleParser
import verik.core.base.LineException
import verik.core.kt.KtCompilationUnit
import verik.core.kt.KtFile
import verik.core.kt.KtPkg
import verik.core.kt.resolve.KtResolver
import verik.core.kt.symbol.KtSymbolTableBuilder
import verik.core.main.StatusPrinter
import verik.core.main.config.ProjectConfig

object KtDriver {

    fun parse(projectConfig: ProjectConfig): KtCompilationUnit {
        StatusPrinter.info("parsing input files", 1)
        val pkgs = ArrayList<KtPkg>()
        for (pkg in projectConfig.symbolContext.pkgs()) {
            val files = ArrayList<KtFile>()
            for (file in projectConfig.symbolContext.files(pkg)) {
                try {
                    val fileConfig = projectConfig.symbolContext.fileConfig(file)
                    val txtFile = fileConfig.copyFile.readText()
                    val alFile = AlRuleParser.parseKotlinFile(txtFile)
                    files.add(KtFile(alFile, file, projectConfig.symbolContext))
                    StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.projectDir)}", 2)
                } catch (exception: LineException) {
                    exception.file = file
                    throw exception
                }
            }
            pkgs.add(KtPkg(pkg, files))
        }
        return KtCompilationUnit(pkgs)
    }

    fun drive(projectConfig: ProjectConfig, compilationUnit: KtCompilationUnit) {
        val symbolTable = KtSymbolTableBuilder.build(projectConfig.symbolContext)
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                try {
                    KtSymbolTableBuilder.buildFile(compilationUnit.file(file), symbolTable)
                } catch (exception: LineException) {
                    exception.file = file
                    throw exception
                }
            }
        }

        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                try {
                    KtResolver.resolve(compilationUnit.file(file), symbolTable)
                } catch (exception: LineException) {
                    exception.file = file
                    throw exception
                }
            }
        }
    }
}