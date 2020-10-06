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

package verik.core.rf

import verik.core.base.LiteralValue
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.rf.reify.RfReifier
import verik.core.rf.reify.RfReifierExpression
import verik.core.rf.reify.RfReifierProperty
import verik.core.rf.reify.RfReifierStatement
import verik.core.rf.symbol.RfSymbolTable
import verik.core.rf.symbol.RfSymbolTableBuilder
import verik.core.sv.*
import verik.core.vk.VkUtil

object RfUtil {

    val EXPRESSION_NULL = RfExpressionLiteral(
            0,
            TYPE_UNIT,
            TYPE_REIFIED_UNIT,
            LiteralValue.fromBoolean(false)
    )

    fun extractFile(string: String): SvFile {
        val file = RfFile(VkUtil.parseFile(string))
        val symbolTable = RfSymbolTable()
        RfSymbolTableBuilder.buildFile(file, symbolTable)
        RfReifier.reify(file, symbolTable)
        return file.extract(symbolTable)
    }

    fun extractModule(string: String): SvModule {
        val module = RfModule(VkUtil.parseModule(string))
        val symbolTable = RfSymbolTable()
        RfSymbolTableBuilder.buildDeclaration(module, symbolTable)
        reifyDeclaration(module, symbolTable)
        return RfModule(VkUtil.parseModule(string)).extract(symbolTable)
    }

    fun extractPort(string: String): SvPort {
        val port = RfPort(VkUtil.parsePort(string))
        val symbolTable = RfSymbolTable()
        reifyDeclaration(port, symbolTable)
        return port.extract(symbolTable)
    }

    fun extractPrimaryProperty(string: String): SvPrimaryProperty {
        val primaryProperty = RfPrimaryProperty(VkUtil.parsePrimaryProperty(string))
        val symbolTable = RfSymbolTable()
        reifyDeclaration(primaryProperty, symbolTable)
        return primaryProperty.extract(symbolTable)
    }

    fun extractActionBlock(string: String): SvActionBlock {
        val actionBlock = RfActionBlock(VkUtil.parseActionBlock(string))
        reifyDeclaration(actionBlock, RfSymbolTable())
        return actionBlock.extract(RfSymbolTable())
    }

    fun extractExpression(string: String): SvExpression {
        val expression = RfExpression(VkUtil.parseExpression(string))
        RfReifierExpression.reify(expression, RfSymbolTable())
        return expression.extractAsExpression(RfSymbolTable())
    }

    fun extractStatement(string: String): SvStatement {
        val expression = RfExpression(VkUtil.parseExpression(string))
        RfReifierExpression.reify(expression, RfSymbolTable())
        return expression.extract(RfSymbolTable())
    }

    private fun reifyDeclaration(declaration: RfDeclaration, symbolTable: RfSymbolTable) {
        RfReifierProperty.reifyDeclaration(declaration, symbolTable)
        RfReifierStatement.reifyDeclaration(declaration, symbolTable)
    }
}