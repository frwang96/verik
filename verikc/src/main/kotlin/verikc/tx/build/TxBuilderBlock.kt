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

import verikc.sv.ast.SvBlock

object TxBuilderBlock {

    fun buildBlock(block: SvBlock, identifier: String?, isAutomatic: Boolean, builder: TxSourceBuilder) {
        if (identifier != null) {
            builder.appendln("begin: $identifier")
        } else {
            builder.appendln("begin")
        }
        buildBlockBare(block, isAutomatic, builder)
        if (identifier != null) {
            builder.appendln("end: $identifier")
        } else {
            builder.appendln("end")
        }
    }

    fun buildBlockBare(block: SvBlock, isAutomatic: Boolean, builder: TxSourceBuilder) {
        indent(builder) {
            if (block.properties.isNotEmpty()) {
                val alignedLines = block.properties.map {
                    if (isAutomatic) TxBuilderTypeExtracted.buildAlignedLine(it)
                    else TxBuilderTypeExtracted.buildAlignedLine(it, "automatic")
                }
                val alignedBlock = TxAlignedBlock(alignedLines, ";", ";")
                alignedBlock.build(builder)
            }
            for (expression in block.expressions) {
                builder.label(expression.line)
                TxBuilderExpressionBase.build(expression, isAutomatic, builder)
            }
        }
    }
}
