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

package verikc.rf.reify

import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.rf.ast.RfComponentInstance
import verikc.rf.ast.RfModule
import verikc.rf.ast.RfPort
import verikc.rf.ast.RfPrimaryProperty
import verikc.rf.symbol.RfSymbolTable

object RfReifierProperty: RfReifierBase() {

    override fun reifyModule(module: RfModule, symbolTable: RfSymbolTable) {
        module.ports.map { reifyPort(it, symbolTable) }
        module.primaryProperties.map { reifyPrimaryProperty(it, symbolTable) }
        module.componentInstances.map { reifyComponentInstance(it, symbolTable) }
    }

    override fun reifyPort(port: RfPort, symbolTable: RfSymbolTable) {
        RfReifierExpression.reify(port.expression, symbolTable)
        val typeReified = port.expression.getTypeReifiedNotNull()
        port.typeReified = typeReified.toInstance(port.expression.line)
        symbolTable.addProperty(port)
    }

    override fun reifyPrimaryProperty(primaryProperty: RfPrimaryProperty, symbolTable: RfSymbolTable) {
        RfReifierExpression.reify(primaryProperty.expression, symbolTable)
        val typeReified = primaryProperty.expression.getTypeReifiedNotNull()
        primaryProperty.typeReified = typeReified.toInstance(primaryProperty.expression.line)
        symbolTable.addProperty(primaryProperty)
    }

    override fun reifyComponentInstance(componentInstance: RfComponentInstance, symbolTable: RfSymbolTable) {
        componentInstance.typeReified = TypeReified(componentInstance.typeSymbol, INSTANCE, listOf())
    }
}
