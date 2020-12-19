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

package verikc.rf

import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rf.ast.*
import verikc.rf.reify.RfReifier
import verikc.rf.reify.RfReifierExpression
import verikc.rf.symbol.RfSymbolTable
import verikc.vk.VkUtil

object RfUtil {

    val EXPRESSION_NULL = RfExpressionLiteral(
        Line(0),
        TYPE_UNIT,
        TYPE_REIFIED_UNIT,
        LiteralValue.fromBoolean(false)
    )

    fun parseFile(string: String): RfFile {
        val file = RfFile(VkUtil.parseFile(string))
        val symbolTable = RfSymbolTable()
        RfReifier.reifyFile(file, symbolTable)
        return file
    }

    fun parseModule(string: String): RfModule {
        val module = RfModule(VkUtil.parseModule(string))
        val symbolTable = RfSymbolTable()
        RfReifier.reifyDeclaration(module, symbolTable)
        return module
    }

    fun parseEnum(string: String): RfEnum {
        val enum = RfEnum(VkUtil.parseEnum(string))
        val symbolTable = RfSymbolTable()
        RfReifier.reifyDeclaration(enum, symbolTable)
        return enum
    }

    fun parsePort(string: String): RfPort {
        val port = RfPort(VkUtil.parsePort(string))
        val symbolTable = RfSymbolTable()
        RfReifier.reifyDeclaration(port, symbolTable)
        return port
    }

    fun parsePrimaryProperty(string: String): RfPrimaryProperty {
        val primaryProperty = RfPrimaryProperty(VkUtil.parsePrimaryProperty(string))
        val symbolTable = RfSymbolTable()
        RfReifier.reifyDeclaration(primaryProperty, symbolTable)
        return primaryProperty
    }

    fun parseActionBlock(string: String): RfActionBlock {
        val actionBlock = RfActionBlock(VkUtil.parseActionBlock(string))
        RfReifier.reifyDeclaration(actionBlock, RfSymbolTable())
        return actionBlock
    }

    fun parseExpression(string: String): RfExpression {
        val expression = RfExpression(VkUtil.parseExpression(string))
        RfReifierExpression.reify(expression, RfSymbolTable())
        return expression
    }
}
