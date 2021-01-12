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

import verikc.base.ast.*
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtProperty
import verikc.rs.evaluate.RsEvaluateResult

data class RsProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val mutabilityType: MutabilityType,
    val annotations: List<AnnotationProperty>,
    val typeIdentifier: String?,
    val expression: RsExpression?,
    var typeGenerified: TypeGenerified?,
    var evaluateResult: RsEvaluateResult?
): RsDeclaration {

    constructor(property: KtProperty): this(
        property.line,
        property.identifier,
        property.symbol,
        property.mutabilityType,
        property.annotations,
        property.typeIdentifier,
        property.expression?.let { RsExpression(it) },
        null,
        null
    )

    fun getTypeIdentifierNotNull(): String {
        return typeIdentifier
            ?: throw LineException("property type identifier expected", line)
    }

    fun getTypeGenerifiedNotNull(): TypeGenerified {
        return typeGenerified
            ?: throw LineException("property has not been resolved", line)
    }
}
