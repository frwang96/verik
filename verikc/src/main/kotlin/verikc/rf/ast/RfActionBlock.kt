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

package verikc.rf.ast

import verikc.base.ast.ActionBlockType
import verikc.base.ast.Line
import verikc.base.ast.Symbol
import verikc.vk.ast.VkActionBlock

data class RfActionBlock(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val actionBlockType: ActionBlockType,
    val eventExpressions: List<RfExpression>,
    val block: RfBlock
): RfDeclaration {

    constructor(actionBlock: VkActionBlock): this(
        actionBlock.line,
        actionBlock.identifier,
        actionBlock.symbol,
        actionBlock.actionBlockType,
        actionBlock.eventExpressions.map { RfExpression(it) },
        RfBlock(actionBlock.block)
    )
}
