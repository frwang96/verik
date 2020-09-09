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

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import verik.core.al.AlRuleParser
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtCompilationUnit
import verik.core.kt.KtFile
import verik.core.kt.KtPkg
import verik.core.kt.resolve.*
import verik.core.kt.symbol.KtSymbolTable
import verik.core.kt.symbol.KtSymbolTableBuilder
import verik.core.main.StatusPrinter
import verik.core.main.config.ProjectConfig

object KtDriver {

    fun parse(projectConfig: ProjectConfig): KtCompilationUnit {
        StatusPrinter.info("parsing input files", 1)

        val deferredFiles = HashMap<Symbol, Deferred<KtFile>>()
        projectConfig.symbolContext.processFiles {
            deferredFiles[it] = GlobalScope.async {
                parseFile(it, projectConfig)
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
        projectConfig.symbolContext.processFiles {
            KtSymbolTableBuilder.buildFile(
                    compilationUnit.file(it),
                    symbolTable,
                    projectConfig.symbolContext
            )
        }

        KtResolverTypeSymbol.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverTypeContent.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverFunction.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverProperty.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
        KtResolverStatement.resolve(compilationUnit, symbolTable, projectConfig.symbolContext)
    }

    private fun parseFile(file: Symbol, projectConfig: ProjectConfig): KtFile {
        return try {
            val fileConfig = projectConfig.symbolContext.fileConfig(file)
            val txtFile = fileConfig.file.readText()
            val alFile = AlRuleParser.parseKotlinFile(txtFile)
            val ktFile = KtFile(alFile, file, projectConfig.symbolContext)
            StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.projectDir)}", 2)
            ktFile
        } catch (exception: LineException) {
            exception.file = file
            throw exception
        }
    }
}