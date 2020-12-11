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

package verikc.kt

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import verikc.al.AlRuleParser
import verikc.base.ast.Symbol
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtPkg
import verikc.kt.resolve.*
import verikc.kt.symbol.KtSymbolTable
import verikc.kt.symbol.KtSymbolTableBuilder
import verikc.main.StatusPrinter
import verikc.main.config.ProjectConfig

object KtDriver {

    fun parse(projectConfig: ProjectConfig): KtCompilationUnit {
        StatusPrinter.info("parsing input files", 1)

        val deferredFiles = HashMap<Symbol, Deferred<KtFile>>()
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                deferredFiles[file] = GlobalScope.async {
                    parseFile(file, projectConfig)
                }
            }
        }

        val pkgs = ArrayList<KtPkg>()
        for (pkg in projectConfig.symbolContext.pkgs()) {
            val files = ArrayList<KtFile>()
            for (file in projectConfig.symbolContext.files(pkg)) {
                runBlocking {
                    files.add(deferredFiles[file]!!.await())
                }
            }
            pkgs.add(KtPkg(pkg, files))
        }

        return KtCompilationUnit(pkgs)
    }

    fun drive(projectConfig: ProjectConfig, compilationUnit: KtCompilationUnit) {
        val symbolTable = KtSymbolTable()
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                KtSymbolTableBuilder.buildFile(
                    compilationUnit.file(file),
                    symbolTable,
                    projectConfig.symbolContext
                )
            }
        }

        KtResolverTypeSymbol.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverTypeContent.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverFunction.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverProperty.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverStatement.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
    }

    private fun parseFile(file: Symbol, projectConfig: ProjectConfig): KtFile {
        val fileConfig = projectConfig.symbolContext.fileConfig(file)
        val txtFile = fileConfig.copyFile.readText()
        val alFile = AlRuleParser.parseKotlinFile(txtFile)
        val ktFile = KtFile(alFile, file, projectConfig.symbolContext)
        StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.projectDir)}", 2)
        return ktFile
    }
}
