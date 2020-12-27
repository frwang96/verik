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

package verikc.sv.extract

import verikc.ps.ast.PsActionBlock
import verikc.sv.ast.SvActionBlock
import verikc.sv.symbol.SvSymbolTable

object SvActionBlockExtractor {

    fun extract(actionBlock: PsActionBlock, symbolTable: SvSymbolTable): SvActionBlock {
        return SvActionBlock(
            actionBlock.line,
            actionBlock.actionBlockType,
            actionBlock.eventExpressions.map { SvExpressionExtractor.extract(it, symbolTable) },
            SvBlockExtractor.extract(actionBlock.block, symbolTable)
        )
    }
}
