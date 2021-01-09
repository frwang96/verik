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
import verikc.base.symbol.Symbol
import verikc.ge.ast.GeProperty
import verikc.ge.ast.GeType
import verikc.ge.table.GeSymbolTable

object GeGenerifierProperty: GeGenerifierBase() {

    override fun generifyType(type: GeType, symbolTable: GeSymbolTable) {
        type.properties.forEach { generifyProperty(it, symbolTable) }
        type.enumProperties.forEach { generifyEnumProperty(it, type.symbol, symbolTable) }
    }

    override fun generifyProperty(property: GeProperty, symbolTable: GeSymbolTable) {
        if (property.expression == null)
            throw LineException("property expression expected", property.line)
        GeGenerifierExpression.generify(property.expression, symbolTable)
        property.typeGenerified = property.expression.getTypeGenerifiedNotNull()
        symbolTable.addProperty(property)
    }

    private fun generifyEnumProperty(property: GeProperty, typeSymbol: Symbol, symbolTable: GeSymbolTable) {
        if (property.expression != null) {
            GeGenerifierExpression.generify(property.expression, symbolTable)
        }
        property.typeGenerified = typeSymbol.toTypeGenerified()
        symbolTable.addProperty(property)
    }
}
