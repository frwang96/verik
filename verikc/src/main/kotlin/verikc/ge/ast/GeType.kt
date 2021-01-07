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

import verikc.base.ast.AnnotationType
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsType

data class GeType(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val isStatic: Boolean,
    val annotations: List<AnnotationType>,
    val parameterProperties: List<GeProperty>,
    val typeParent: GeTypeParent,
    val typeConstructorFunction: GeFunction,
    val enumProperties: List<GeProperty>,
    val functions: List<GeFunction>,
    val properties: List<GeProperty>
): GeDeclaration {

    constructor(type: RsType): this(
        type.line,
        type.identifier,
        type.symbol,
        type.isStatic,
        type.annotations,
        type.parameterProperties.map { GeProperty(it) },
        GeTypeParent(type.typeParent),
        GeFunction(type.typeConstructorFunction),
        type.enumProperties.map { GeProperty(it) },
        type.functions.map { GeFunction(it) },
        type.properties.map { GeProperty(it) }
    )
}