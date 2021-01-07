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

import verikc.base.ast.Line
import verikc.rs.ast.RsBlock

data class GeBlock(
    val line: Line,
    val lambdaProperties: List<GeProperty>,
    val statements: List<GeStatement>
) {

    constructor(block: RsBlock): this(
        block.line,
        block.lambdaProperties.map { GeProperty(it) },
        block.statements.map { GeStatement(it) }
    )
}
