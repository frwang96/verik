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

import verikc.base.ast.LineException
import verikc.base.ast.MethodBlockType
import verikc.sv.ast.SvMethodBlock

object TxBuilderMethodBlock {

    fun build(methodBlock: SvMethodBlock, builder: TxSourceBuilder) {
        builder.label(methodBlock.line)

        if (methodBlock.methodBlockType != MethodBlockType.FUNCTION)
            throw LineException("only function method blocks are supported", methodBlock.line)

        builder.append("function automatic ")
        builder.append(
            TxBuilderTypeExtracted.buildWithoutUnpacked(
                methodBlock.returnTypeExtracted,
                methodBlock.identifier,
                methodBlock.line
            )
        )

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

        TxBuilderBlock.buildBlockBare(methodBlock.block, builder)
        builder.appendln("endfunction")
    }
}
