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

import verikc.al.ast.AlCompilationUnit
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtPkg
import verikc.kt.resolve.*
import verikc.kt.symbol.KtSymbolTable
import verikc.kt.symbol.KtSymbolTableBuilder

object KtDriver {

    fun parse(compilationUnit: AlCompilationUnit, symbolContext: SymbolContext): KtCompilationUnit {
        val pkgs = ArrayList<KtPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<KtFile>()
            for (file in pkg.files) {
                files.add(KtFile(file.tree, file.config, symbolContext))
            }
            pkgs.add(KtPkg(pkg.config, files))
        }
        return KtCompilationUnit(pkgs)
    }

    fun resolve(compilationUnit: KtCompilationUnit) {
        val symbolTable = KtSymbolTable()
        resolve(compilationUnit, symbolTable)
    }

    fun resolve(compilationUnit: KtCompilationUnit, symbolTable: KtSymbolTable) {
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
}
