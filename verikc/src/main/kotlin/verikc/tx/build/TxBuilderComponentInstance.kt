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

import verikc.base.ast.ComponentType
import verikc.sv.ast.SvComponentInstance

object TxBuilderComponentInstance {

    fun build(componentInstance: SvComponentInstance, builder: TxSourceBuilder) {
        when (componentInstance.componentType) {
            ComponentType.MODULE, ComponentType.BUS -> buildModuleBus(componentInstance, builder)
            ComponentType.BUSPORT -> buildBusport(componentInstance, builder)
            ComponentType.CLOCKPORT -> TODO()
        }
    }

    private fun buildModuleBus(componentInstance: SvComponentInstance, builder: TxSourceBuilder) {
        builder.label(componentInstance.property.line)
        builder.append(TxBuilderTypeExtracted.buildWithoutPackedUnpacked(componentInstance.property))
        if (componentInstance.connections.isNotEmpty()) {
            builder.appendln(" (")
            indent(builder) {
                val alignedLines = componentInstance.connections.map { TxBuilderConnection.buildConnection(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }
            builder.appendln(");")
        } else {
            builder.appendln(" ();")
        }
    }

    private fun buildBusport(componentInstance: SvComponentInstance, builder: TxSourceBuilder) {
        builder.label(componentInstance.property.line)
        builder.append("modport ${componentInstance.property.identifier}")
        if (componentInstance.connections.isNotEmpty()) {
            builder.appendln(" (")
            indent(builder) {
                val alignedLines = componentInstance.connections.map { TxBuilderConnection.buildPort(it) }
                val alignedBlock = TxAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }
            builder.appendln(");")
        } else {
            builder.appendln(" ();")
        }
    }
}
