/*
 * Copyright (c) 2021 Francis Wang
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
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtTypeAlias

data class RsTypeAlias(
    val line: Line,
    val identifier: String,
    val symbol: Symbol,
    val typeConstructor: RsFunction,
    val expression: RsExpressionFunction,
    val aliasedTypeIdentifier: String,
    var typeGenerified: TypeGenerified?
) {

    constructor(typeAlias: KtTypeAlias): this(
        typeAlias.line,
        typeAlias.identifier,
        typeAlias.symbol,
        RsFunction(typeAlias.typeConstructor),
        RsExpressionFunction(typeAlias.expression),
        typeAlias.aliasedTypeIdentifier,
        null
    )

    fun getTypeGenerifiedNotNull(): TypeGenerified {
        return typeGenerified
            ?: throw LineException("type alias has not been resolved", line)
    }
}