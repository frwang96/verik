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
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtPkg
import verikc.kt.resolve.*
import verikc.kt.symbol.KtSymbolTable
import verikc.kt.symbol.KtSymbolTableBuilder
import verikc.main.StatusPrinter
import verikc.base.config.ProjectConfig

object KtDriver {

    fun parse(projectConfig: ProjectConfig): KtCompilationUnit {
        StatusPrinter.info("parsing input files", 1)

        val deferredFiles = HashMap<Symbol, Deferred<KtFile>>()
        for (pkgSymbol in projectConfig.symbolContext.pkgs()) {
            for (fileSymbol in projectConfig.symbolContext.files(pkgSymbol)) {
                deferredFiles[fileSymbol] = GlobalScope.async {
                    parseFile(pkgSymbol, fileSymbol, projectConfig)
                }
            }
        }

        val pkgs = ArrayList<KtPkg>()
        for (pkgSymbol in projectConfig.symbolContext.pkgs()) {
            val files = ArrayList<KtFile>()
            for (fileSymbol in projectConfig.symbolContext.files(pkgSymbol)) {
                runBlocking {
                    files.add(deferredFiles[fileSymbol]!!.await())
                }
            }
            pkgs.add(KtPkg(pkgSymbol, files))
        }

        return KtCompilationUnit(pkgs)
    }

    fun drive(projectConfig: ProjectConfig, compilationUnit: KtCompilationUnit) {
        val symbolTable = KtSymbolTable()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                KtSymbolTableBuilder.buildFile(
                    pkg.pkgSymbol,
                    file.fileSymbol,
                    symbolTable,
                    projectConfig.symbolContext
                )
            }
        }

        KtResolverTypeSymbol.resolve(compilationUnit, symbolTable)
        KtResolverTypeContent.resolve(compilationUnit, symbolTable)
        KtResolverFunction.resolve(compilationUnit, symbolTable)
        KtResolverProperty.resolve(compilationUnit, symbolTable)
        KtResolverStatement.resolve(compilationUnit, symbolTable)
    }

    private fun parseFile(pkgSymbol: Symbol, fileSymbol: Symbol, projectConfig: ProjectConfig): KtFile {
        val fileConfig = projectConfig.symbolContext.fileConfig(fileSymbol)
        val txtFile = fileConfig.copyFile.readText()
        val alFile = AlRuleParser.parseKotlinFile(fileSymbol, txtFile)
        val ktFile = KtFile(alFile, pkgSymbol, fileSymbol, projectConfig.symbolContext)
        StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.projectDir)}", 2)
        return ktFile
    }
}
