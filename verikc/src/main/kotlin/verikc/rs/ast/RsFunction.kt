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

package verikc.rs.ast

import verikc.base.ast.AnnotationFunction
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtFunction

data class RsFunction(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val annotations: List<AnnotationFunction>,
    val parameterProperties: List<RsProperty>,
    val returnTypeIdentifier: String,
    val block: RsBlock,
    var returnTypeGenerified: TypeGenerified?
): RsDeclaration {

    constructor(function: KtFunction): this(
        function.line,
        function.identifier,
        function.symbol,
        function.annotations,
        function.parameterProperties.map { RsProperty(it) },
        function.returnTypeIdentifier,
        RsBlock(function.block),
        null
    )

    fun getReturnTypeGenerifiedNotNull(): TypeGenerified {
        return returnTypeGenerified
            ?: throw LineException("function has not been resolved", line)
    }
}
