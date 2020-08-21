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

import verik.core.al.AlRule
import verik.core.base.LiteralValue
import verik.core.it.reify.ItReifier
import verik.core.it.reify.ItReifierExpression
import verik.core.it.reify.ItReifierProperty
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

    fun extractFile(rule: AlRule): SvFile {
        val file = ItFile(VkUtil.parseFile(rule))
        val symbolTable = ItSymbolTableBuilder.build(file)
        ItReifier.reify(file, symbolTable)
        return file.extract(symbolTable)
    }

    fun extractModule(rule: AlRule): SvModule {
        val module = ItModule(VkUtil.parseModule(rule))
        val symbolTable = ItSymbolTable()
        ItSymbolTableBuilder.buildDeclaration(module, symbolTable)
        reifyDeclaration(module, symbolTable)
        return ItModule(VkUtil.parseModule(rule)).extract(symbolTable)
    }

    fun extractPort(rule: AlRule): SvPort {
        val port = ItPort(VkUtil.parsePort(rule))
        reifyDeclaration(port, ItSymbolTable())
        return port.extract()
    }

    fun extractBaseProperty(rule: AlRule): SvBaseProperty {
        val baseProperty = ItBaseProperty(VkUtil.parseBaseProperty(rule))
        reifyDeclaration(baseProperty, ItSymbolTable())
        return baseProperty.extract()
    }

    fun extractActionBlock(rule: AlRule): SvActionBlock {
        val actionBlock = ItActionBlock(VkUtil.parseActionBlock(rule))
        reifyDeclaration(actionBlock, ItSymbolTable())
        return actionBlock.extract(ItSymbolTable())
    }

    fun extractExpression(rule: AlRule): SvExpression {
        val expression = ItExpression(VkUtil.parseExpression(rule))
        ItReifierExpression.reifyExpression(expression, ItSymbolTable())
        return expression.extractAsExpression(ItSymbolTable())
    }

    fun extractStatement(rule: AlRule): SvStatement {
        val expression = ItExpression(VkUtil.parseExpression(rule))
        ItReifierExpression.reifyExpression(expression, ItSymbolTable())
        return expression.extract(ItSymbolTable())
    }

    private fun reifyDeclaration(declaration: ItDeclaration, symbolTable: ItSymbolTable) {
        ItReifierProperty.reifyDeclaration(declaration, symbolTable)
        ItReifierExpression.reifyDeclaration(declaration, symbolTable)
    }
}