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

package verikc.sv.ast

import verikc.base.ast.Line
import verikc.sv.build.*

data class SvEnumProperty(
    val line: Line,
    val identifier: String,
    val expression: SvExpressionLiteral
) {

    fun build(): SvAlignedLine {
        return SvAlignedLine(line, listOf(identifier, "=", expression.string))
    }
}

data class SvEnum(
    val line: Line,
    val identifier: String,
    val properties: List<SvEnumProperty>,
    val width: Int
): SvBuildable {

    override fun build(builder: SvSourceBuilder) {
        builder.label(line)
        builder.appendln("typedef enum logic [${width - 1}:0] {")
        indent(builder) {
            val alignedLines = properties.map { it.build() }
            val alignedBlock = SvAlignedBlock(alignedLines, ",", "")
            alignedBlock.build(builder)
        }
        builder.appendln("} $identifier;")
    }
}
