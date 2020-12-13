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

package verikc.kt.ast

import verikc.base.ast.Line
import verikc.base.ast.Symbol

data class KtType(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val annotations: List<KtAnnotationType>,
    val parameters: List<KtParameterProperty>,
    val typeParent: KtTypeParent,
    val declarations: List<KtDeclaration>
): KtDeclaration