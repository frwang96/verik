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

package verik.core.rf.check

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.rf.ast.*
import verik.core.rf.symbol.RfSymbolTable

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
        val ports = symbolTable.getComponentPorts(componentInstance.type, componentInstance.line)

        val portSymbols = HashSet<Symbol>()
        ports.forEach { portSymbols.add(it.symbol) }

        val connectionSymbols = HashSet<Symbol>()
        componentInstance.connections.forEach {
            if (connectionSymbols.contains(it.port)) {
                throw LineException("duplicate connection ${it.port}", componentInstance)
            }
            connectionSymbols.add(it.port)
        }

        val invalidConnections = connectionSymbols.subtract(portSymbols)
        if (invalidConnections.isNotEmpty()) {
            val connectionString = if (invalidConnections.size == 1) "connection" else "connections"
            throw LineException("invalid $connectionString ${invalidConnections.joinToString()}", componentInstance)
        }

        val missingConnections = portSymbols.subtract(connectionSymbols)
        if (missingConnections.isNotEmpty()) {
            val connectionString = if (invalidConnections.size == 1) "connection" else "connections"
            throw LineException("missing $connectionString ${missingConnections.joinToString()}", componentInstance)
        }

        componentInstance.connections.forEach {
            val port = ports.find { port -> port.symbol == it.port }!!
            when (port.portType) {
                RfPortType.INPUT -> if (it.connectionType != RfConnectionType.INPUT)
                    throw LineException("input assignment expected for ${it.port}", it)
                RfPortType.OUTPUT -> if (it.connectionType != RfConnectionType.OUTPUT)
                    throw LineException("output assignment expected for ${it.port}", it)
                else -> if (it.connectionType != RfConnectionType.INOUT)
                    throw LineException("con expression expected for ${it.port}", it)
            }
        }
    }
}