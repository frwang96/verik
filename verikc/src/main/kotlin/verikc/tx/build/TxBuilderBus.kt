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

import verikc.sv.ast.SvBus

object TxBuilderBus {

    fun build(bus: SvBus, builder: TxSourceBuilder) {
        if (bus.ports.isEmpty()) {
            builder.label(bus.line)
            builder.appendln("interface ${bus.identifier};")
        } else {
            builder.label(bus.line)
            builder.appendln("interface ${bus.identifier} (")

            indent(builder) {
                val alignedLines = bus.ports.map { TxBuilderPort.build(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }

            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")

            if (bus.properties.isNotEmpty()) {
                builder.appendln()
                val alignedLines = bus.properties.map { TxBuilderTypeExtracted.buildAlignedLine(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ";", ";")
                alignedBlock.build(builder)
            }
        }
        builder.appendln()
        builder.appendln("endinterface: ${bus.identifier}")
    }
}
