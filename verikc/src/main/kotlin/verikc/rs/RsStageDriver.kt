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
import verikc.rs.resolve.*
import verikc.rs.table.RsSymbolTable

object RsStageDriver {

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

        RsResolverType.resolve(compilationUnit, symbolTable)
        RsResolverFunction.resolve(compilationUnit, symbolTable)
        RsResolverProperty.resolve(compilationUnit, symbolTable)
        RsResolverBulk.resolve(compilationUnit, symbolTable)

        return symbolTable
    }
}