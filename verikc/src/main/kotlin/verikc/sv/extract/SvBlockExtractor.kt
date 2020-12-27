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

import verikc.ps.ast.PsBlock
import verikc.sv.ast.SvBlock
import verikc.sv.symbol.SvSymbolTable

object SvBlockExtractor {

    fun extract(block: PsBlock, symbolTable: SvSymbolTable): SvBlock {
        return SvBlock(
            block.line,
            block.statements.map { SvStatementExtractor.extract(it, symbolTable) }
        )
    }
}
