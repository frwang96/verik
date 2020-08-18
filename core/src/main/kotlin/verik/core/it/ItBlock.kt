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

import verik.core.it.symbol.ItSymbolTable
import verik.core.main.Line
import verik.core.sv.SvBlock
import verik.core.vk.VkBlock

data class ItBlock(
        override val line: Int,
        val statements: List<ItStatement>
): Line {

    fun extract(symbolTable: ItSymbolTable): SvBlock {
        return SvBlock(
                line,
                statements.map { it.extract(symbolTable) }
        )
    }

    constructor(block: VkBlock): this(
            block.line,
            block.statements.map { ItStatement(it) }
    )
}