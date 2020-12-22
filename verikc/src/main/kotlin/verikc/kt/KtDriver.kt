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
import verikc.alx.AlxTreeParser
import verikc.base.config.FileConfig
import verikc.base.config.ProjectConfig
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtPkg
import verikc.kt.resolve.*
import verikc.kt.symbol.KtSymbolTable
import verikc.kt.symbol.KtSymbolTableBuilder
import verikc.main.StatusPrinter

object KtDriver {

    fun parse(projectConfig: ProjectConfig): KtCompilationUnit {
        StatusPrinter.info("parsing input files", 1)

        val deferredFiles = HashMap<Symbol, Deferred<KtFile>>()
        for (pkgConfig in projectConfig.compilationUnit.pkgConfigs) {
            for (fileConfig in pkgConfig.fileConfigs) {
                deferredFiles[fileConfig.symbol] = GlobalScope.async {
                    parseFile(fileConfig, projectConfig)
                }
            }
        }

        val pkgs = ArrayList<KtPkg>()
        for (pkgConfig in projectConfig.compilationUnit.pkgConfigs) {
            val files = ArrayList<KtFile>()
            for (fileConfig in pkgConfig.fileConfigs) {
                runBlocking {
                    files.add(deferredFiles[fileConfig.symbol]!!.await())
                }
            }
            pkgs.add(KtPkg(pkgConfig, files))
        }

        return KtCompilationUnit(pkgs)
    }

    fun drive(compilationUnit: KtCompilationUnit) {
        val symbolTable = KtSymbolTable()
        drive(compilationUnit, symbolTable)
    }

    fun drive(compilationUnit: KtCompilationUnit, symbolTable: KtSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                KtSymbolTableBuilder.buildFile(pkg.config, file.config, symbolTable)
            }
        }

        KtResolverTypeSymbol.resolve(compilationUnit, symbolTable)
        KtResolverTypeContent.resolve(compilationUnit, symbolTable)
        KtResolverFunction.resolve(compilationUnit, symbolTable)
        KtResolverProperty.resolve(compilationUnit, symbolTable)
        KtResolverStatement.resolve(compilationUnit, symbolTable)
    }

    private fun parseFile(fileConfig: FileConfig, projectConfig: ProjectConfig): KtFile {
        val txtFile = fileConfig.copyFile.readText()
        val alFile = AlxTreeParser.parseKotlinFile(fileConfig.symbol, txtFile)
        val ktFile = KtFile(alFile, fileConfig, projectConfig.symbolContext)
        StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.projectDir)}", 2)
        return ktFile
    }
}
