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

import verikc.base.ast.Line
import verikc.base.ast.TypeGenerified
import verikc.kt.ast.KtTypeParent

data class RsxTypeParent(
    val line: Line,
    val typeIdentifier: String,
    val args: List<RsxExpression>,
    var typeGenerified: TypeGenerified?
) {

    constructor(typeParent: KtTypeParent): this(
        typeParent.line,
        typeParent.typeIdentifier,
        typeParent.args.map { RsxExpression(it) },
        null
    )
}
