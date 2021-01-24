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

package verikc.vk.check

import verikc.base.ast.ComponentType
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.vk.ast.VkCompilationUnit
import verikc.vk.ast.VkComponent
import verikc.vk.ast.VkConnectionType
import verikc.vk.ast.VkPort

object VkCheckerComponent {

    fun check(compilationUnit: VkCompilationUnit, componentTable: VkComponentTable) {
        val parentIdentifiersMap = buildParentIdentifiersMap(compilationUnit)
        val portTypeSet = buildPortTypeSet(compilationUnit)
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.components.forEach { checkComponent(it, parentIdentifiersMap, portTypeSet, componentTable) }
            }
        }
    }

    private fun buildParentIdentifiersMap(compilationUnit: VkCompilationUnit): Map<Symbol, List<String>> {
        val parentIdentifiersMap = HashMap<Symbol, ArrayList<String>>()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                for (component in file.components) {
                    component.componentInstances.forEach {
                        val componentInstanceSymbol = it.property.typeGenerified.typeSymbol
                        val parentIdentifiers = parentIdentifiersMap[componentInstanceSymbol]
                        if (parentIdentifiers != null) {
                            parentIdentifiers.add(component.identifier)
                        } else {
                            parentIdentifiersMap[componentInstanceSymbol] = arrayListOf(component.identifier)
                        }
                    }
                }
            }
        }
        return parentIdentifiersMap
    }

    private fun buildPortTypeSet(compilationUnit: VkCompilationUnit): Set<Symbol> {
        val portTypeSet = HashSet<Symbol>()
        portTypeSet.add(TYPE_BOOLEAN)
        portTypeSet.add(TYPE_UBIT)
        portTypeSet.add(TYPE_SBIT)
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.enums.forEach { portTypeSet.add(it.symbol) }
                file.structs.forEach { portTypeSet.add(it.symbol) }
            }
        }
        return portTypeSet
    }

    private fun checkComponent(
        component: VkComponent,
        parentIdentifiersMap: Map<Symbol, List<String>>,
        portTypeSet: Set<Symbol>,
        componentTable: VkComponentTable
    ) {
        if (component.componentType == ComponentType.BUS_PORT) {
            val parentIdentifiers = parentIdentifiersMap[component.symbol]
            component.busPortParentIdentifier = if (parentIdentifiers != null) {
                if (parentIdentifiers.size == 1) parentIdentifiers[0]
                else throw LineException("bus port can only be instantiated once", component.line)
            } else null
        }
        component.ports.forEach { checkPort(it, portTypeSet, componentTable) }
    }

    private fun checkPort(port: VkPort, portTypeSet: Set<Symbol>, componentTable: VkComponentTable) {
        val componentType = componentTable.getComponentType(port.property.typeGenerified.typeSymbol)
        port.portType = when (port.connectionType) {
            VkConnectionType.INPUT -> {
                checkPortType(port, portTypeSet)
                PortType.INPUT
            }
            VkConnectionType.OUTPUT -> {
                checkPortType(port, portTypeSet)
                PortType.OUTPUT
            }
            VkConnectionType.INOUT -> {
                when (componentType) {
                    ComponentType.BUS -> PortType.BUS
                    ComponentType.BUS_PORT -> PortType.BUS_PORT
                    ComponentType.CLOCK_PORT -> PortType.CLOCK_PORT
                    else -> {
                        checkPortType(port, portTypeSet)
                        PortType.INOUT
                    }
                }
            }
        }
    }

    private fun checkPortType(port: VkPort, portTypeSet: Set<Symbol>) {
        if (port.property.typeGenerified.typeSymbol !in portTypeSet)
            throw LineException("port of type ${port.property.typeGenerified} not supported", port.property.line)
    }
}