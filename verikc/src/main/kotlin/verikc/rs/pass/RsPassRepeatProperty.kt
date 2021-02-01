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

package verikc.rs.pass

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.resolve.RsEvaluatorExpression
import verikc.rs.table.RsResolveException
import verikc.rs.table.RsSymbolTable

class RsPassRepeatProperty: RsPassRepeatBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        type.properties.forEach { passProperty(it, type.symbol, symbolTable) }
    }

    override fun passProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (property.typeGenerified == null) {
            if (property.expression == null)
                throw LineException("property expression expected", property.line)
            try {
                RsPassExpression.pass(property.expression, scopeSymbol, symbolTable)
                property.typeGenerified = property.expression.getTypeGenerifiedNotNull()
                property.evaluateResult = RsEvaluatorExpression.evaluate(property.expression, symbolTable)
                symbolTable.setProperty(property)
            } catch (exception: RsResolveException) {
                isResolved = false
                if (throwException) throw exception
            }
        }
    }
}
