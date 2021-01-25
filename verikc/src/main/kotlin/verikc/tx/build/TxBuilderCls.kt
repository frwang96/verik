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

package verikc.tx.build

import verikc.sv.ast.SvCls

object TxBuilderCls {

    fun build(cls: SvCls, builder: TxSourceBuilder) {
        builder.appendln("class ${cls.identifier};")
        indent(builder) {
            if (cls.properties.isNotEmpty()) {
                builder.appendln()
                TxAlignedBlock(
                    cls.properties.map { TxBuilderTypeExtracted.buildAlignedLine(it) },
                    ";",
                    ";"
                ).build(builder)
            }

            for (methodBlock in cls.methodBlocks) {
                builder.appendln()
                TxBuilderMethodBlock.build(methodBlock, true, builder)
            }
        }
        builder.appendln()
        builder.appendln("endclass: ${cls.identifier}")
    }
}