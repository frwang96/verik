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

package verik.core.it

import verik.core.base.Symbol
import verik.core.it.symbol.ItSymbolTable
import verik.core.sv.SvActionBlock
import verik.core.sv.SvActionBlockType
import verik.core.vk.VkActionBlock
import verik.core.vk.VkActionBlockType

enum class ItActionBlockType {
    COMB,
    SEQ,
    RUN;

    fun extract(): SvActionBlockType {
        return when (this) {
            COMB -> SvActionBlockType.ALWAYS_COMB
            SEQ -> SvActionBlockType.ALWAYS_FF
            RUN -> SvActionBlockType.INITIAL
        }
    }

    companion object {

        operator fun invoke(type: VkActionBlockType): ItActionBlockType {
            return when (type) {
                VkActionBlockType.COMB -> COMB
                VkActionBlockType.SEQ -> SEQ
                VkActionBlockType.RUN -> RUN
            }
        }
    }
}

data class ItActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: ItActionBlockType,
        val eventExpressions: List<ItExpression>,
        val block: ItBlock
): ItDeclaration {

    fun extract(symbolTable: ItSymbolTable): SvActionBlock {
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
            ItActionBlockType(actionBlock.actionBlockType),
            actionBlock.eventExpressions.map { ItExpression(it) },
            ItBlock(actionBlock.block)
    )
}