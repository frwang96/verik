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

package verikc.rs.pass

import verikc.rs.ast.RsCompilationUnit
import verikc.rs.table.RsSymbolTable

object RsPassRepeat {

    private const val REPEAT_COUNT = 3

    fun pass(compilationUnit: RsCompilationUnit, symbolTable: RsSymbolTable) {
        val passFunction = RsPassFunction()
        val passProperty = RsPassProperty()
        repeat (REPEAT_COUNT) {
            val functionIsResolved = passFunction.attemptPass(compilationUnit, false, symbolTable)
            val propertyIsResolved = passProperty.attemptPass(compilationUnit, false, symbolTable)
            if (functionIsResolved && propertyIsResolved) return
        }
        passFunction.attemptPass(compilationUnit, true, symbolTable)
        passProperty.attemptPass(compilationUnit, true, symbolTable)
    }
}