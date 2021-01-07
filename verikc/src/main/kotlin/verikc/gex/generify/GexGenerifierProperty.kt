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

package verikc.gex.generify

import verikc.base.ast.LineException
import verikc.gex.ast.GexProperty
import verikc.gex.ast.GexType
import verikc.gex.table.GexSymbolTable

object GexGenerifierProperty: GexGenerifierBase() {

    override fun generifyType(type: GexType, symbolTable: GexSymbolTable) {
        type.properties.forEach { generifyProperty(it, symbolTable) }
        type.enumProperties.forEach { generifyProperty(it, symbolTable) }
    }

    override fun generifyProperty(property: GexProperty, symbolTable: GexSymbolTable) {
        if (property.expression == null)
            throw LineException("property expression expected", property.line)
        GexGenerifierExpression.generify(property.expression, symbolTable)
        val typeGenerified = property.expression.getTypeGenerifiedNotNull()
        property.typeGenerified = typeGenerified.toInstance(property.line)
        symbolTable.addProperty(property)
    }
}
