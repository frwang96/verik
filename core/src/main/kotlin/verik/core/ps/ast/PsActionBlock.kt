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

package verik.core.ps.ast

import verik.core.base.ast.ActionBlockType
import verik.core.base.ast.Symbol
import verik.core.ps.symbol.PsSymbolTable
import verik.core.rf.ast.RfActionBlock
import verik.core.sv.ast.SvActionBlock

data class PsActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: ActionBlockType,
        val eventExpressions: List<PsExpression>,
        val block: PsBlock
): PsDeclaration {

    fun extract(symbolTable: PsSymbolTable): SvActionBlock {
        return SvActionBlock(
                line,
                actionBlockType,
                eventExpressions.map { it.extractAsExpression(symbolTable) },
                block.extract(symbolTable)
        )
    }

    constructor(actionBlock: RfActionBlock): this(
            actionBlock.line,
            actionBlock.identifier,
            actionBlock.symbol,
            actionBlock.actionBlockType,
            actionBlock.eventExpressions.map { PsExpression(it) },
            PsBlock(actionBlock.block)
    )
}