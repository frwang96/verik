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

package verikc.ps.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.MethodBlockType
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.ge.ast.GeMethodBlock

data class PsMethodBlock(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val methodBlockType: MethodBlockType,
    val primaryProperties: List<PsPrimaryProperty>,
    val returnTypeGenerified: TypeGenerified,
    val block: PsBlock
): PsDeclaration {

    companion object {

        operator fun invoke(methodBlock: GeMethodBlock): PsMethodBlock {
            val returnTypeGenerified = methodBlock.returnTypeGenerified
                ?: throw LineException(
                    "function ${methodBlock.symbol} return type has not been generified",
                    methodBlock.line
                )

            return PsMethodBlock(
                methodBlock.line,
                methodBlock.identifier,
                methodBlock.symbol,
                methodBlock.methodBlockType,
                methodBlock.parameterProperties.map { PsPrimaryProperty(it) },
                returnTypeGenerified,
                PsBlock(methodBlock.block)
            )
        }
    }
}
