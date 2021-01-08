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

package verikc.ge.ast

import verikc.base.ast.AnnotationFunction
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsFunction

data class GeFunction(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val annotations: List<AnnotationFunction>,
    val parameterProperties: List<GeProperty>,
    val returnTypeSymbol: Symbol,
    val block: GeBlock,
    var returnTypeGenerified: TypeGenerified?
): GeDeclaration {

    constructor(function: RsFunction): this(
        function.line,
        function.identifier,
        function.symbol,
        function.annotations,
        function.parameterProperties.map { GeProperty(it) },
        function.getReturnTypeSymbolNotNull(),
        GeBlock(function.block),
        null
    )

    fun getReturnTypeGenerifiedNotNull(): TypeGenerified {
        return returnTypeGenerified
            ?: throw LineException("function has not been generified", line)
    }
}