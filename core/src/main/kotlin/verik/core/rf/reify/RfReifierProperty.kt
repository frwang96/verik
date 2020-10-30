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

package verik.core.rf.reify

import verik.core.base.LineException
import verik.core.rf.ast.*
import verik.core.rf.symbol.RfSymbolTable

object RfReifierProperty: RfReifierBase() {

    override fun reifyModule(module: RfModule, symbolTable: RfSymbolTable) {
        module.ports.map { reifyPort(it, symbolTable) }
        module.primaryProperties.map { reifyPrimaryProperty(it, symbolTable) }
        module.componentInstances.map { reifyComponentInstance(it, symbolTable) }
    }

    override fun reifyPort(port: RfPort, symbolTable: RfSymbolTable) {
        RfReifierExpression.reify(port.expression, symbolTable)
        val reifiedType = port.expression.reifiedType
                ?: throw LineException("port expression has not been reified", port.expression)
        port.reifiedType = reifiedType.toInstance(port.expression)
    }

    override fun reifyPrimaryProperty(primaryProperty: RfPrimaryProperty, symbolTable: RfSymbolTable) {
        RfReifierExpression.reify(primaryProperty.expression, symbolTable)
        val reifiedType = primaryProperty.expression.reifiedType
                ?: throw LineException("primary property expression has not been reified", primaryProperty.expression)
        primaryProperty.reifiedType = reifiedType.toInstance(primaryProperty.expression)
    }

    override fun reifyComponentInstance(componentInstance: RfComponentInstance, symbolTable: RfSymbolTable) {
        componentInstance.reifiedType = RfReifiedType(componentInstance.type, RfTypeClass.INSTANCE, listOf())
    }
}