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
import verikc.vk.ast.VkCompilationUnit
import verikc.vk.ast.VkComponent
import verikc.vk.ast.VkComponentInstance
import verikc.vk.ast.VkConnectionType

object VkCheckerComponentInstance {

    fun check(compilationUnit: VkCompilationUnit, componentTable: VkComponentTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.components.forEach { checkComponent(it, componentTable) }
            }
        }
    }

    private fun checkComponent(component: VkComponent, componentTable: VkComponentTable) {
        component.componentInstances.forEach { checkComponentInstance(it, component.componentType, componentTable) }
    }

    private fun checkComponentInstance(
        componentInstance: VkComponentInstance,
        parentComponentType: ComponentType,
        componentTable: VkComponentTable
    ) {
        val componentSymbol = componentInstance.property.typeGenerified.typeSymbol
        val componentType = componentTable.getComponentType(componentSymbol)
            ?: throw LineException("unable to recognize component $componentSymbol", componentInstance.property.line)
        componentInstance.componentType = componentType

        if (parentComponentType == ComponentType.MODULE && componentType == ComponentType.BUSPORT)
            throw LineException("bus port not allowed in module", componentInstance.property.line)
        if (parentComponentType == ComponentType.BUS && componentType == ComponentType.MODULE)
            throw LineException("module not allowed in bus", componentInstance.property.line)

        if (componentType == ComponentType.CLOCKPORT) {
            if (componentInstance.eventExpression == null)
                throw LineException(
                    "on expression expected for clock port instantiation",
                    componentInstance.property.line
                )
        } else {
            if (componentInstance.eventExpression != null)
                throw LineException(
                    "on expression not permitted for component instantiation",
                    componentInstance.property.line
                )
        }

        checkComponentInstanceConnections(componentInstance, componentTable)
    }

    private fun checkComponentInstanceConnections(
        componentInstance: VkComponentInstance,
        componentTable: VkComponentTable
    ) {
        val ports = componentTable.getPorts(
            componentInstance.property.typeGenerified.typeSymbol,
            componentInstance.property.line
        )

        val portSymbols = HashSet<Symbol>()
        ports.forEach { portSymbols.add(it.property.symbol) }

        val connectionSymbols = HashSet<Symbol>()
        componentInstance.connections.forEach {
            if (connectionSymbols.contains(it.portSymbol)) {
                throw LineException("duplicate connection ${it.portSymbol}", componentInstance.property.line)
            }
            connectionSymbols.add(it.portSymbol)
        }

        val invalidConnections = connectionSymbols.subtract(portSymbols)
        if (invalidConnections.isNotEmpty()) {
            val connectionString = if (invalidConnections.size == 1) "connection" else "connections"
            throw LineException(
                "invalid $connectionString ${invalidConnections.joinToString()}",
                componentInstance.property.line
            )
        }

        val missingConnections = portSymbols.subtract(connectionSymbols)
        if (missingConnections.isNotEmpty()) {
            val connectionString = if (missingConnections.size == 1) "connection" else "connections"
            throw LineException(
                "missing $connectionString ${missingConnections.joinToString()}",
                componentInstance.property.line
            )
        }

        componentInstance.connections.forEach {
            if (componentInstance.componentType == ComponentType.BUSPORT) {
                if (!it.identifiersMatch)
                    throw LineException("bus port connection identifiers must match", it.line)
            }

            val port = ports.find { port -> port.property.symbol == it.portSymbol }!!
            when (port.portType) {
                PortType.INPUT -> if (it.connectionType != VkConnectionType.INPUT)
                    throw LineException("input assignment expected for ${it.portSymbol}", it.line)
                PortType.OUTPUT -> if (it.connectionType != VkConnectionType.OUTPUT)
                    throw LineException("output assignment expected for ${it.portSymbol}", it.line)
                else -> if (it.connectionType != VkConnectionType.INOUT)
                    throw LineException("con expression expected for ${it.portSymbol}", it.line)
            }
            it.portType = port.portType
        }
    }
}