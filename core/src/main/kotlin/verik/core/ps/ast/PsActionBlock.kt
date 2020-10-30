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

import verik.core.base.ast.Symbol
import verik.core.rf.ast.RfActionBlock
import verik.core.rf.ast.RfActionBlockType

enum class PsActionBlockType {
    COM,
    SEQ,
    RUN;

    companion object {

        operator fun invoke(type: RfActionBlockType): PsActionBlockType {
            return when (type) {
                RfActionBlockType.COM -> COM
                RfActionBlockType.SEQ -> SEQ
                RfActionBlockType.RUN -> RUN
            }
        }
    }
}

data class PsActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: PsActionBlockType,
        val eventExpressions: List<PsExpression>,
        val block: PsBlock
): PsDeclaration {

    constructor(actionBlock: RfActionBlock): this(
            actionBlock.line,
            actionBlock.identifier,
            actionBlock.symbol,
            PsActionBlockType(actionBlock.actionBlockType),
            actionBlock.eventExpressions.map { PsExpression(it) },
            PsBlock(actionBlock.block)
    )
}