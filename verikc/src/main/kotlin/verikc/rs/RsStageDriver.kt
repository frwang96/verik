/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rs

import verikc.kt.ast.KtCompilationUnit
import verikc.rs.ast.RsCompilationUnit
import verikc.rs.pass.*
import verikc.rs.resolve.RsResolverImport
import verikc.rs.table.RsSymbolTable

object RsStageDriver {

    private const val REPEAT_COUNT = 5

    fun build(compilationUnit: KtCompilationUnit): RsCompilationUnit {
        return RsCompilationUnit(compilationUnit)
    }

    fun resolve(compilationUnit: RsCompilationUnit): RsSymbolTable {
       RsResolverImport.resolve(compilationUnit)

        val symbolTable = RsSymbolTable()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                symbolTable.addFile(file)
            }
        }

        RsPassRegister.pass(compilationUnit, symbolTable)
        RsPassType.pass(compilationUnit, symbolTable)
        RsPassPropertyBase.pass(compilationUnit, symbolTable)
        resolveRepeat(compilationUnit, symbolTable)
        RsPassBulk.pass(compilationUnit, symbolTable)

        return symbolTable
    }

    private fun resolveRepeat(compilationUnit: RsCompilationUnit, symbolTable: RsSymbolTable) {
        val repeatTypeAlias = RsPassRepeatTypeAlias()
        val repeatFunction = RsPassRepeatFunction()
        val repeatProperty = RsPassRepeatProperty()
        repeat (REPEAT_COUNT) {
            val typeAliasIsResolved = repeatTypeAlias.attemptPass(compilationUnit, false, symbolTable)
            val functionIsResolved = repeatFunction.attemptPass(compilationUnit, false, symbolTable)
            val propertyIsResolved = repeatProperty.attemptPass(compilationUnit, false, symbolTable)
            if (typeAliasIsResolved && functionIsResolved && propertyIsResolved) return
        }
        repeatTypeAlias.attemptPass(compilationUnit, true, symbolTable)
        repeatFunction.attemptPass(compilationUnit, true, symbolTable)
        repeatProperty.attemptPass(compilationUnit, true, symbolTable)
    }
}