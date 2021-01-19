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
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.vk.ast.VkCompilationUnit
import verikc.vk.ast.VkComponent
import verikc.vk.ast.VkPort

object VkCheckerComponent {

    fun check(compilationUnit: VkCompilationUnit, componentTable: VkComponentTable) {
        val parentIdentifiersMap = buildParentIdentifiersMap(compilationUnit)
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.components.forEach { checkComponent(it, parentIdentifiersMap, componentTable) }
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

    private fun checkComponent(
        component: VkComponent,
        parentIdentifiersMap: Map<Symbol, List<String>>,
        componentTable: VkComponentTable
    ) {
        if (component.componentType == ComponentType.BUSPORT) {
            val parentIdentifiers = parentIdentifiersMap[component.symbol]
            component.busportParentIdentifier = if (parentIdentifiers != null) {
                if (parentIdentifiers.size == 1) parentIdentifiers[0]
                else throw LineException("bus port can only be instantiated once", component.line)
            } else null
        }
        component.ports.forEach { checkPort(it, componentTable) }
    }

    private fun checkPort(port: VkPort, componentTable: VkComponentTable) {
        val componentType = componentTable.getComponentType(port.property.typeGenerified.typeSymbol)
        when (port.portType) {
            PortType.INPUT, PortType.OUTPUT, PortType.INOUT -> {
                if (port.property.typeGenerified.typeSymbol !in listOf(TYPE_BOOL, TYPE_UBIT, TYPE_SBIT)) {
                    throw LineException(
                        "port of type ${port.property.typeGenerified} not supported",
                        port.property.line
                    )
                }
            }
            PortType.BUS -> if (componentType != ComponentType.BUS)
                throw LineException("bus type expected", port.property.line)
            PortType.BUSPORT -> if (componentType != ComponentType.BUSPORT)
                throw LineException("bus port type expected", port.property.line)
            PortType.CLOCKPORT -> if (componentType != ComponentType.CLOCKPORT)
                throw LineException("clock port type expected", port.property.line)
        }
    }
}