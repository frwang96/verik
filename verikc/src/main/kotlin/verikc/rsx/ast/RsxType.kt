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

package verikc.rsx.ast

import verikc.base.ast.AnnotationType
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtType

data class RsxType(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val isStatic: Boolean,
    val annotations: List<AnnotationType>,
    val parameterProperties: List<RsxProperty>,
    val typeParent: RsxTypeParent,
    val typeConstructorFunction: RsxFunction,
    val enumProperties: List<RsxProperty>,
    val functions: List<RsxFunction>,
    val properties: List<RsxProperty>
): RsxDeclaration {

    constructor(type: KtType): this(
        type.line,
        type.identifier,
        type.symbol,
        type.isStatic,
        type.annotations,
        type.parameterProperties.map { RsxProperty(it) },
        RsxTypeParent(type.typeParent),
        RsxFunction(type.typeConstructorFunction),
        type.enumProperties.map { RsxProperty(it) },
        type.functions.map { RsxFunction(it) },
        type.properties.map { RsxProperty(it) }
    )
}