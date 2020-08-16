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

package verik.core.itx

import verik.core.it.ItActionBlock
import verik.core.it.ItActionBlockType
import verik.core.it.ItDeclaration
import verik.core.main.symbol.Symbol

data class ItxActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: ItActionBlockType,
        val edges: List<ItxEdge>?,
        val block: ItxBlock
): ItDeclaration {

    companion object {

        operator fun invoke(actionBlock: ItActionBlock): ItxActionBlock {
            return ItxActionBlock(
                    actionBlock.line,
                    actionBlock.identifier,
                    actionBlock.symbol,
                    actionBlock.actionBlockType,
                    actionBlock.edges?.map { ItxEdge(it) },
                    ItxBlock(actionBlock.block)
            )
        }
    }
}