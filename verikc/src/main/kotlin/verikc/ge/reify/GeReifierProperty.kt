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

package verikc.ge.reify

import verikc.ge.ast.GeComponentInstance
import verikc.ge.ast.GeModule
import verikc.ge.ast.GePort
import verikc.ge.ast.GePrimaryProperty
import verikc.ge.symbol.GeSymbolTable

object GeReifierProperty: GeReifierBase() {

    override fun reifyModule(module: GeModule, symbolTable: GeSymbolTable) {
        module.ports.forEach { reifyPort(it, symbolTable) }
        module.primaryProperties.forEach { reifyPrimaryProperty(it, symbolTable) }
        module.componentInstances.forEach { reifyComponentInstance(it) }
    }

    override fun reifyPrimaryProperty(primaryProperty: GePrimaryProperty, symbolTable: GeSymbolTable) {
        GeReifierExpression.reify(primaryProperty.expression, symbolTable)
        val typeReified = primaryProperty.expression.getTypeReifiedNotNull()
        primaryProperty.typeReified = typeReified.toInstance(primaryProperty.expression.line)
        symbolTable.addProperty(primaryProperty)
    }

    private fun reifyPort(port: GePort, symbolTable: GeSymbolTable) {
        GeReifierExpression.reify(port.expression, symbolTable)
        val typeReified = port.expression.getTypeReifiedNotNull()
        port.typeReified = typeReified.toInstance(port.expression.line)
        symbolTable.addProperty(port)
    }

    private fun reifyComponentInstance(componentInstance: GeComponentInstance) {
        componentInstance.typeReified = componentInstance.typeSymbol.toTypeReifiedInstance()
    }
}
