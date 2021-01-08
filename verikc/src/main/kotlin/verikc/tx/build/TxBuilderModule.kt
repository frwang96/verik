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

import verikc.sv.ast.SvModule

object TxBuilderModule {

    fun build(module: SvModule, builder: TxSourceBuilder) {
        if (module.ports.isEmpty()) {
            builder.label(module.line)
            builder.appendln("module ${module.identifier};")
        } else {
            builder.label(module.line)
            builder.appendln("module ${module.identifier} (")

            indent(builder) {
                val alignedLines = module.ports.map { TxBuilderPort.build(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }

            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")

            if (module.properties.isNotEmpty()) {
                builder.appendln()
                val alignedLines = module.properties.map { TxBuilderProperty.build(it, false) }
                val alignedBlock = TxAlignedBlock(alignedLines, ";", ";")
                alignedBlock.build(builder)
            }

            for (componentInstance in module.componentInstances) {
                builder.appendln()
                TxBuilderComponentInstance.build(componentInstance, builder)
            }

            for (actionBlock in module.actionBlocks) {
                builder.appendln()
                TxBuilderActionBlock.build(actionBlock, builder)
            }

            for (methodBlock in module.methodBlocks) {
                builder.appendln()
                TxBuilderMethodBlock.build(methodBlock, builder)
            }
        }
        builder.appendln()
        builder.appendln("endmodule: ${module.identifier}")
    }
}
