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

package verikc.ge.generify

import verikc.base.ast.LineException
import verikc.ge.ast.GeComponentInstance
import verikc.ge.ast.GeModule
import verikc.ge.ast.GePort
import verikc.ge.ast.GePrimaryProperty
import verikc.ge.table.GeSymbolTable

object GeGenerifierProperty: GeGenerifierBase() {

    override fun generifyModule(module: GeModule, symbolTable: GeSymbolTable) {
        module.ports.forEach { generifyPort(it, symbolTable) }
        module.primaryProperties.forEach { generifyPrimaryProperty(it, symbolTable) }
        module.componentInstances.forEach { generifyComponentInstance(it) }
    }

    override fun generifyPrimaryProperty(primaryProperty: GePrimaryProperty, symbolTable: GeSymbolTable) {
        if (primaryProperty.expression == null)
            throw LineException("primary property expression expected", primaryProperty.line)
        GeGenerifierExpression.generify(primaryProperty.expression, symbolTable)
        val typeGenerified = primaryProperty.expression.getTypeGenerifiedNotNull()
        primaryProperty.typeGenerified = typeGenerified.toInstance(primaryProperty.expression.line)
        symbolTable.addProperty(primaryProperty)
    }

    private fun generifyPort(port: GePort, symbolTable: GeSymbolTable) {
        GeGenerifierExpression.generify(port.expression, symbolTable)
        val typeGenerified = port.expression.getTypeGenerifiedNotNull()
        port.typeGenerified = typeGenerified.toInstance(port.expression.line)
        symbolTable.addProperty(port)
    }

    private fun generifyComponentInstance(componentInstance: GeComponentInstance) {
        componentInstance.typeGenerified = componentInstance.typeSymbol.toTypeGenerifiedInstance()
    }
}
