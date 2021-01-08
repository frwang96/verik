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

package verikc.ge

import verikc.ge.ast.GeCompilationUnit
import verikc.ge.generify.GeGenerifierBulk
import verikc.ge.generify.GeGenerifierFunction
import verikc.ge.generify.GeGenerifierProperty
import verikc.ge.table.GeSymbolTable
import verikc.rs.ast.RsCompilationUnit

object GeStageDriver {

    fun build(compilationUnit: RsCompilationUnit): GeCompilationUnit {
        return GeCompilationUnit(compilationUnit)
    }

    fun generify(compilationUnit: GeCompilationUnit): GeSymbolTable {
        val symbolTable = GeSymbolTable()
        GeGenerifierFunction.generify(compilationUnit, symbolTable)
        GeGenerifierProperty.generify(compilationUnit, symbolTable)
        GeGenerifierBulk.generify(compilationUnit, symbolTable)
        return symbolTable
    }
}
