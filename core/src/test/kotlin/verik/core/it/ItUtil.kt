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
import verik.core.it.reify.ItExpressionReifier
import verik.core.it.reify.ItPropertyReifier
import verik.core.it.reify.ItReifier
import verik.core.it.symbol.ItSymbolTable
import verik.core.it.symbol.ItSymbolTableBuilder
import verik.core.sv.*
import verik.core.vk.VkUtil

object ItUtil {

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
        ItPropertyReifier.reifyDeclaration(module, symbolTable)
        ItExpressionReifier.reifyDeclaration(module, symbolTable)
        return ItModule(VkUtil.parseModule(rule)).extract(symbolTable)
    }

    fun extractPort(rule: AlRule): SvPort {
        val port = ItPort(VkUtil.parsePort(rule))
        ItPropertyReifier.reifyDeclaration(port, ItSymbolTable())
        return port.extract()
    }

    fun extractActionBlock(rule: AlRule): SvActionBlock {
        val actionBlock = ItActionBlock(VkUtil.parseActionBlock(rule))
        ItExpressionReifier.reifyDeclaration(actionBlock, ItSymbolTable())
        return actionBlock.extract(ItSymbolTable())
    }

    fun extractExpression(rule: AlRule): SvExpression {
        val expression = ItExpression(VkUtil.parseExpression(rule))
        ItExpressionReifier.reifyExpression(expression, ItSymbolTable())
        return expression.extract(ItSymbolTable())
    }
}