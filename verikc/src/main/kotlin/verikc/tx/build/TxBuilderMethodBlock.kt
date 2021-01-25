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

import verikc.base.ast.MethodBlockType
import verikc.sv.ast.SvMethodBlock

object TxBuilderMethodBlock {

    fun build(methodBlock: SvMethodBlock, isAutomatic: Boolean, builder: TxSourceBuilder) {
        builder.label(methodBlock.line)

        when (methodBlock.methodBlockType) {
            MethodBlockType.FUNCTION -> {
                builder.append("function ")
                if (!isAutomatic) builder.append("automatic ")
                builder.append(
                    TxBuilderTypeExtracted.buildWithoutUnpacked(
                        methodBlock.returnTypeExtracted,
                        methodBlock.identifier,
                        methodBlock.line
                    )
                )
            }
            MethodBlockType.TASK -> {
                builder.append("task ")
                if (!isAutomatic) builder.append("automatic ")
                builder.append(methodBlock.identifier)
            }
            MethodBlockType.INSTANCE_CONSTRUCTOR -> {
                builder.append("function new")
            }
        }

        if (methodBlock.parameterProperties.isEmpty()) {
            builder.appendln(" ();")
        } else {
            builder.appendln(" (")
            indent(builder) {
                val alignedLines = methodBlock.parameterProperties.map { TxBuilderTypeExtracted.buildAlignedLine(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }
            builder.appendln(");")
        }

        TxBuilderBlock.buildBlockBare(methodBlock.block, true, builder)
        when (methodBlock.methodBlockType) {
            MethodBlockType.FUNCTION -> builder.appendln("endfunction")
            MethodBlockType.TASK -> builder.appendln("endtask")
            MethodBlockType.INSTANCE_CONSTRUCTOR -> builder.appendln("endfunction")
        }
    }
}
