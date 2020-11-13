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

package verikc.ps.ast

import verikc.base.ast.Line
import verikc.ps.symbol.PsSymbolTable
import verikc.rf.ast.RfBlock
import verikc.sv.ast.SvBlock

data class PsBlock(
        override val line: Int,
        val statements: ArrayList<PsStatement>
): Line {

    fun extract(symbolTable: PsSymbolTable): SvBlock {
        return SvBlock(
                line,
                statements.map { it.extract(symbolTable) }
        )
    }

    constructor(block: RfBlock): this(
            block.line,
            ArrayList(block.statements.map { PsStatement(it) })
    )
}
