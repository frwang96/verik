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

package verik.core.ps.symbol

import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.ps.ast.PsExpressionFunction
import verik.core.ps.ast.PsExpressionOperator
import verik.core.ps.ast.PsExpressionProperty
import verik.core.sv.ast.SvBlock
import verik.core.sv.ast.SvExpression
import verik.core.sv.ast.SvExtractedType
import verik.core.sv.ast.SvStatement

data class PsFunctionExtractorRequest(
        val function: PsExpressionFunction,
        val receiver: SvExpression?,
        val args: List<SvExpression>
)

data class PsOperatorExtractorRequest(
        val operator: PsExpressionOperator,
        val receiver: SvExpression?,
        val args: List<SvExpression>,
        val blocks: List<SvBlock>
)

// TODO remove annotation
@Suppress("UNUSED_PARAMETER")
class PsSymbolTable {

    fun extractType(reifiedType: ReifiedType, line: Int): SvExtractedType {
        TODO()
    }

    fun extractFunction(request: PsFunctionExtractorRequest): SvStatement {
        TODO()
    }

    fun extractOperator(request: PsOperatorExtractorRequest): SvStatement {
        TODO()
    }

    fun extractProperty(expression: PsExpressionProperty): SvStatement {
        TODO()
    }

    fun extractPropertyIdentifier(property: Symbol, line: Int): String {
        TODO()
    }

    fun extractComponentIdentifier(type: Symbol, line: Int): String {
        TODO()
    }
}