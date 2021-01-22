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
import verikc.vk.ast.*

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

        if (parentComponentType == ComponentType.MODULE && componentType == ComponentType.BUS_PORT)
            throw LineException("bus port not allowed in module", componentInstance.property.line)
        if (parentComponentType == ComponentType.BUS && componentType == ComponentType.MODULE)
            throw LineException("module not allowed in bus", componentInstance.property.line)

        if (componentType == ComponentType.CLOCK_PORT) {
            if (componentInstance.clockPortEventExpression == null)
                throw LineException(
                    "event argument expected for clock port instantiation",
                    componentInstance.property.line
                )
        } else {
            if (componentInstance.clockPortEventExpression != null)
                throw LineException(
                    "event argument not permitted for component instantiation",
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

        if (ports.size != componentInstance.connections.size)
            throw LineException(
                "expected ${ports.size} connections but found ${componentInstance.connections.size}",
                componentInstance.property.line
            )

        if (componentInstance.connectionIdentifiers != null) {
            componentInstance.connections.forEachIndexed { index, connection ->
                val portIdentifier = componentInstance.connectionIdentifiers[index]
                val port = ports.find { it.property.identifier == portIdentifier }
                    ?: throw LineException("could not identify port $portIdentifier", connection.line)
                checkComponentInstanceConnection(componentInstance, connection, port)
            }
        } else {
            componentInstance.connections.forEachIndexed { index, connection ->
                checkComponentInstanceConnection(componentInstance, connection, ports[index])
            }
        }
    }

    private fun checkComponentInstanceConnection(
        componentInstance: VkComponentInstance,
        connection: VkConnection,
        port: VkPort
    ) {
        if (componentInstance.componentType in listOf(ComponentType.BUS_PORT, ComponentType.CLOCK_PORT)) {
            if (connection.expressionPropertyIdentifier != port.property.identifier)
                throw LineException("connection identifiers must match", connection.line)
        }

        val expectedTypeGenerified = port.property.typeGenerified
        val actualTypeGenerified = connection.expression.typeGenerified
        if (expectedTypeGenerified != actualTypeGenerified)
            throw LineException(
                "connection type mismatch expected $expectedTypeGenerified but got $actualTypeGenerified",
                connection.line
            )

        connection.portSymbol = port.property.symbol
        connection.portType = port.portType
    }
}