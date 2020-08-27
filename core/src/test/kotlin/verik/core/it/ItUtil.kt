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

package verik.core.it

import verik.core.base.LiteralValue
import verik.core.it.reify.ItReifier
import verik.core.it.reify.ItReifierExpression
import verik.core.it.reify.ItReifierProperty
import verik.core.it.reify.ItReifierStatement
import verik.core.it.symbol.ItSymbolTable
import verik.core.it.symbol.ItSymbolTableBuilder
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.sv.*
import verik.core.vk.VkUtil

object ItUtil {

    val EXPRESSION_NULL = ItExpressionLiteral(
            0,
            TYPE_UNIT,
            TYPE_REIFIED_UNIT,
            LiteralValue.fromBoolean(false)
    )

    fun extractFile(string: String): SvFile {
        val file = ItFile(VkUtil.parseFile(string))
        val symbolTable = ItSymbolTable()
        ItSymbolTableBuilder.buildFile(file, symbolTable)
        ItReifier.reify(file, symbolTable)
        return file.extract(symbolTable)
    }

    fun extractModule(string: String): SvModule {
        val module = ItModule(VkUtil.parseModule(string))
        val symbolTable = ItSymbolTable()
        ItSymbolTableBuilder.buildDeclaration(module, symbolTable)
        reifyDeclaration(module, symbolTable)
        return ItModule(VkUtil.parseModule(string)).extract(symbolTable)
    }

    fun extractPort(string: String): SvPort {
        val port = ItPort(VkUtil.parsePort(string))
        val symbolTable = ItSymbolTable()
        reifyDeclaration(port, symbolTable)
        return port.extract(symbolTable)
    }

    fun extractPrimaryProperty(string: String): SvPrimaryProperty {
        val primaryProperty = ItPrimaryProperty(VkUtil.parsePrimaryProperty(string))
        val symbolTable = ItSymbolTable()
        reifyDeclaration(primaryProperty, symbolTable)
        return primaryProperty.extract(symbolTable)
    }

    fun extractActionBlock(string: String): SvActionBlock {
        val actionBlock = ItActionBlock(VkUtil.parseActionBlock(string))
        reifyDeclaration(actionBlock, ItSymbolTable())
        return actionBlock.extract(ItSymbolTable())
    }

    fun extractExpression(string: String): SvExpression {
        val expression = ItExpression(VkUtil.parseExpression(string))
        ItReifierExpression.reify(expression, ItSymbolTable())
        return expression.extractAsExpression(ItSymbolTable())
    }

    fun extractStatement(string: String): SvStatement {
        val expression = ItExpression(VkUtil.parseExpression(string))
        ItReifierExpression.reify(expression, ItSymbolTable())
        return expression.extract(ItSymbolTable())
    }

    private fun reifyDeclaration(declaration: ItDeclaration, symbolTable: ItSymbolTable) {
        ItReifierProperty.reifyDeclaration(declaration, symbolTable)
        ItReifierStatement.reifyDeclaration(declaration, symbolTable)
    }
}