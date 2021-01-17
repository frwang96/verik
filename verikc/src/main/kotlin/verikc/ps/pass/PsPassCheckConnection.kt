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

package verikc.ps.pass

import verikc.base.ast.ConnectionType
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry
import verikc.base.symbol.SymbolEntryMap
import verikc.ps.ast.*

class PsPassCheckConnection: PsPassBase() {

    private val indexer = Indexer()

    override fun pass(compilationUnit: PsCompilationUnit) {
        indexer.pass(compilationUnit)
        super.pass(compilationUnit)
    }

    override fun passModule(module: PsModule) {
        module.componentInstances.forEach { passComponentInstance(it) }
    }

    private fun passComponentInstance(componentInstance: PsComponentInstance) {
        val ports = indexer
            .componentEntryMap
            .get(componentInstance.property.typeGenerified.typeSymbol, componentInstance.property.line)
            .ports

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
            val port = ports.find { port -> port.property.symbol == it.portSymbol }!!
            when (port.portType) {
                PortType.INPUT -> if (it.connectionType != ConnectionType.INPUT)
                    throw LineException("input assignment expected for ${it.portSymbol}", it.line)
                PortType.OUTPUT -> if (it.connectionType != ConnectionType.OUTPUT)
                    throw LineException("output assignment expected for ${it.portSymbol}", it.line)
                else -> if (it.connectionType != ConnectionType.INOUT)
                    throw LineException("con expression expected for ${it.portSymbol}", it.line)
            }
        }
    }

    private class Indexer: PsPassBase() {

        val componentEntryMap = SymbolEntryMap<ComponentEntry>("component")

        override fun passModule(module: PsModule) {
            componentEntryMap.add(ComponentEntry(module.symbol, module.ports), module.line)
        }

        override fun passBus(bus: PsBus) {
            componentEntryMap.add(ComponentEntry(bus.symbol, bus.ports), bus.line)
        }

        data class ComponentEntry(
            override val symbol: Symbol,
            val ports: List<PsPort>
        ): SymbolEntry
    }
}