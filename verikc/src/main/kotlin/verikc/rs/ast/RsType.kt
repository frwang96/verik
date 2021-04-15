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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtType

data class RsType(
    val line: Line,
    val identifier: String,
    val symbol: Symbol,
    val isStatic: Boolean,
    val parameterProperties: List<RsProperty>,
    val typeParent: RsTypeParent,
    val typeObject: RsProperty,
    val topObject: RsProperty?,
    val typeConstructor: RsFunction,
    val instanceConstructor: RsFunction?,
    val setvalFunction: RsFunction?,
    val enumProperties: List<RsProperty>,
    val functions: List<RsFunction>,
    val properties: List<RsProperty>
) {

    constructor(type: KtType): this(
        type.line,
        type.identifier,
        type.symbol,
        type.isStatic,
        type.parameterProperties.map { RsProperty(it) },
        RsTypeParent(type.typeParent),
        RsProperty(type.typeObject),
        type.topObject?.let { RsProperty(it) },
        RsFunction(type.typeConstructor),
        type.instanceConstructor?.let { RsFunction(it) },
        type.setvalFunction?.let { RsFunction(it) },
        type.enumProperties.map { RsProperty(it) },
        type.functions.map { RsFunction(it) },
        type.properties.map { RsProperty(it) }
    )

    fun getInstanceConstructorNotNull(): RsFunction {
        return instanceConstructor
            ?: throw LineException("instance constructor expected", line)
    }
}