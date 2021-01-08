/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.sv.ast

import verikc.base.ast.ActionBlockType
import verikc.base.ast.Line
import verikc.ps.ast.PsActionBlock
import verikc.sv.extract.SvExtractorExpressionBase
import verikc.sv.table.SvSymbolTable

data class SvActionBlock(
    val line: Line,
    val identifier: String,
    val actionBlockType: ActionBlockType,
    val eventExpressions: List<SvExpression>,
    val block: SvBlock
) {

    constructor(actionBlock: PsActionBlock, symbolTable: SvSymbolTable): this(
        actionBlock.line,
        actionBlock.identifier,
        actionBlock.actionBlockType,
        actionBlock.eventExpressions.map { SvExtractorExpressionBase.extract(it, symbolTable) },
        SvBlock(actionBlock.block, symbolTable)
    )
}
