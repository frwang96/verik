/*
 * Copyright (c) 2020 Francis Wang
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
import verikc.kt.resolve.*
import verikc.kt.symbol.KtSymbolTable
import verikc.kt.symbol.KtSymbolTableBuilder

object KtStageDriver {

    fun parse(compilationUnit: AlCompilationUnit, symbolContext: SymbolContext): KtCompilationUnit {
        return KtCompilationUnit(compilationUnit, symbolContext)
    }

    fun resolve(compilationUnit: KtCompilationUnit): KtSymbolTable {
        val symbolTable = KtSymbolTable()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                KtSymbolTableBuilder.buildFile(pkg.config, file.config, symbolTable)
            }
        }

        KtResolverTypeSymbol.resolve(compilationUnit, symbolTable)
        KtResolverTypeContent.resolve(compilationUnit, symbolTable)
        KtResolverFunction.resolve(compilationUnit, symbolTable)
        KtResolverProperty.resolve(compilationUnit, symbolTable)
        KtResolverBlock.resolve(compilationUnit, symbolTable)

        return symbolTable
    }
}
