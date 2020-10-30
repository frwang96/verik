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

package verik.core.rf.ast

import verik.core.base.ast.Symbol
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.SvActionBlock
import verik.core.sv.ast.SvActionBlockType
import verik.core.vk.ast.VkActionBlock
import verik.core.vk.ast.VkActionBlockType

enum class RfActionBlockType {
    COM,
    SEQ,
    RUN;

    fun extract(): SvActionBlockType {
        return when (this) {
            COM -> SvActionBlockType.ALWAYS_COMB
            SEQ -> SvActionBlockType.ALWAYS_FF
            RUN -> SvActionBlockType.INITIAL
        }
    }

    companion object {

        operator fun invoke(type: VkActionBlockType): RfActionBlockType {
            return when (type) {
                VkActionBlockType.COM -> COM
                VkActionBlockType.SEQ -> SEQ
                VkActionBlockType.RUN -> RUN
            }
        }
    }
}

data class RfActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: RfActionBlockType,
        val eventExpressions: List<RfExpression>,
        val block: RfBlock
): RfDeclaration {

    fun extract(symbolTable: RfSymbolTable): SvActionBlock {
        return SvActionBlock(
                line,
                actionBlockType.extract(),
                eventExpressions.map { it.extractAsExpression(symbolTable) },
                block.extract(symbolTable)
        )
    }

    constructor(actionBlock: VkActionBlock): this(
            actionBlock.line,
            actionBlock.identifier,
            actionBlock.symbol,
            RfActionBlockType(actionBlock.actionBlockType),
            actionBlock.eventExpressions.map { RfExpression(it) },
            RfBlock(actionBlock.block)
    )
}