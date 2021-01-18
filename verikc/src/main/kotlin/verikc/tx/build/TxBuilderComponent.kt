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

import verikc.sv.ast.SvComponent

object TxBuilderComponent {

    fun build(component: SvComponent, builder: TxSourceBuilder) {
        if (component.ports.isEmpty()) {
            builder.label(component.line)
            builder.appendln("module ${component.identifier};")
        } else {
            builder.label(component.line)
            builder.appendln("module ${component.identifier} (")

            indent(builder) {
                val alignedLines = component.ports.map { TxBuilderPort.build(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }

            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")

            if (component.properties.isNotEmpty()) {
                builder.appendln()
                val alignedLines = component.properties.map { TxBuilderTypeExtracted.buildAlignedLine(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ";", ";")
                alignedBlock.build(builder)
            }

            for (componentInstance in component.componentInstances) {
                builder.appendln()
                TxBuilderComponentInstance.build(componentInstance, builder)
            }

            for (actionBlock in component.actionBlocks) {
                builder.appendln()
                TxBuilderActionBlock.build(actionBlock, builder)
            }

            for (methodBlock in component.methodBlocks) {
                builder.appendln()
                TxBuilderMethodBlock.build(methodBlock, false, builder)
            }
        }
        builder.appendln()
        builder.appendln("endmodule: ${component.identifier}")
    }
}
