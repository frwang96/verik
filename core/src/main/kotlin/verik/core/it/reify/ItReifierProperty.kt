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

package verik.core.it.reify

import verik.core.base.LineException
import verik.core.it.ItBaseProperty
import verik.core.it.ItModule
import verik.core.it.ItPort
import verik.core.it.symbol.ItSymbolTable

object ItReifierProperty: ItReifierBase() {

    override fun reifyModule(module: ItModule, symbolTable: ItSymbolTable) {
        module.ports.map { reifyPort(it, symbolTable) }
        module.baseProperties.map { reifyBaseProperty(it, symbolTable) }
    }

    override fun reifyPort(port: ItPort, symbolTable: ItSymbolTable) {
        ItReifierExpression.reifyExpression(port.expression, symbolTable)
        val typeReified = port.expression.typeReified
                ?: throw LineException("port expression has not been reified", port.expression)
        port.typeReified = typeReified.toInstance(port.expression)
    }

    override fun reifyBaseProperty(baseProperty: ItBaseProperty, symbolTable: ItSymbolTable) {
        ItReifierExpression.reifyExpression(baseProperty.expression, symbolTable)
        val typeReified = baseProperty.expression.typeReified
                ?: throw LineException("base property expression has not been reified", baseProperty.expression)
        baseProperty.typeReified = typeReified.toInstance(baseProperty.expression)
    }
}