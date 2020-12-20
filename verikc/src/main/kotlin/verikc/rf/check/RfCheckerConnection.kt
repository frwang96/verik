/*
 * Copyright 2020 Francis Wang
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

package verikc.rf.check

import verikc.base.ast.ConnectionType
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.rf.ast.RfComponentInstance
import verikc.rf.ast.RfFile
import verikc.rf.ast.RfModule
import verikc.rf.symbol.RfSymbolTable

object RfCheckerConnection {

    fun check(file: RfFile, symbolTable: RfSymbolTable) {
        file.declarations.forEach {
            if (it is RfModule) {
                it.componentInstances.forEach { componentInstance ->
                    checkComponentInstance(componentInstance, symbolTable)
                }
            }
        }
    }

    fun checkComponentInstance(componentInstance: RfComponentInstance, symbolTable: RfSymbolTable) {
        val ports = symbolTable.getComponentPorts(componentInstance.typeSymbol, componentInstance.line)

        val portSymbols = HashSet<Symbol>()
        ports.forEach { portSymbols.add(it.symbol) }

        val connectionSymbols = HashSet<Symbol>()
        componentInstance.connections.forEach {
            if (connectionSymbols.contains(it.portSymbol)) {
                throw LineException("duplicate connection ${it.portSymbol}", componentInstance.line)
            }
            connectionSymbols.add(it.portSymbol)
        }

        val invalidConnections = connectionSymbols.subtract(portSymbols)
        if (invalidConnections.isNotEmpty()) {
            val connectionString = if (invalidConnections.size == 1) "connection" else "connections"
            throw LineException(
                "invalid $connectionString ${invalidConnections.joinToString()}",
                componentInstance.line
            )
        }

        val missingConnections = portSymbols.subtract(connectionSymbols)
        if (missingConnections.isNotEmpty()) {
            val connectionString = if (invalidConnections.size == 1) "connection" else "connections"
            throw LineException(
                "missing $connectionString ${missingConnections.joinToString()}",
                componentInstance.line
            )
        }

        componentInstance.connections.forEach {
            val port = ports.find { port -> port.symbol == it.portSymbol }!!
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
}
