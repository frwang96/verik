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

package verikc.tx.build

import verikc.sv.ast.SvEnum
import verikc.sv.ast.SvEnumProperty

object TxBuilderEnum {

    fun build(enum: SvEnum, builder: TxSourceBuilder) {
        builder.label(enum.line)
        builder.appendln("typedef enum logic [${enum.width - 1}:0] {")
        indent(builder) {
            val alignedLines = enum.properties.map { buildEnumProperty(it) }
            val alignedBlock = TxAlignedBlock(alignedLines, ",", "")
            alignedBlock.build(builder)
        }
        builder.appendln("} ${enum.identifier};")
    }

    private fun buildEnumProperty(enumProperty: SvEnumProperty): TxAlignedLine {
        return TxAlignedLine(
            enumProperty.line,
            listOf(enumProperty.identifier, "=", enumProperty.expression.string)
        )
    }
}