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

package verik.core.sv

import verik.core.base.Line
import verik.core.sv.build.*

data class SvEnumEntry(
        override val line: Int,
        val identifier: String,
        val expression: SvExpressionLiteral
): Line {

    fun build(): SvAlignedLine {
        return SvAlignedLine(line, listOf(identifier, "=", expression.string))
    }
}

data class SvEnum(
        override val line: Int,
        val identifier: String,
        val entries: List<SvEnumEntry>,
        val width: Int
): Line, SvBuildable {

    override fun build(builder: SvSourceBuilder) {
        builder.label(this)
        builder.appendln("typedef enum logic [${width-1}:0] {")
        indent(builder) {
            val alignedLines = entries.map { it.build() }
            val alignedBlock = SvAlignedBlock(alignedLines, ",", "")
            alignedBlock.build(builder)
        }
        builder.appendln("} $identifier;")
    }
}