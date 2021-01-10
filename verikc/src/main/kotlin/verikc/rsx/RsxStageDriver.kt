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

package verikc.rsx

import verikc.kt.ast.KtCompilationUnit
import verikc.rsx.ast.RsxCompilationUnit
import verikc.rsx.resolve.*
import verikc.rsx.table.RsxSymbolTable

object RsxStageDriver {

    fun build(compilationUnit: KtCompilationUnit): RsxCompilationUnit {
        return RsxCompilationUnit(compilationUnit)
    }

    fun resolve(compilationUnit: RsxCompilationUnit): RsxSymbolTable {
       RsxResolverImport.resolve(compilationUnit)

        val symbolTable = RsxSymbolTable()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                symbolTable.addFile(file)
            }
        }

        RsxResolverPassType.resolve(compilationUnit, symbolTable)
        RsxResolverPassFunction.resolve(compilationUnit, symbolTable)
        RsxResolverPassProperty.resolve(compilationUnit, symbolTable)
        RsxResolverPassBulk.resolve(compilationUnit, symbolTable)

        return symbolTable
    }
}