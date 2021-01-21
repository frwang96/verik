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

package verikc.ps.pass

import verikc.base.ast.ActionBlockType
import verikc.base.ast.ComponentType
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_ASSIGN_NONBLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.ps.ast.*

class PsPassConvertAssignment: PsPassBase() {

    private val indexer = Indexer()

    override fun pass(compilationUnit: PsCompilationUnit) {
        indexer.pass(compilationUnit)
        super.pass(compilationUnit)
    }

    override fun passComponent(component: PsComponent) {
        val componentPropertySymbols = HashSet<Symbol>()
        component.ports.forEach { componentPropertySymbols.add(it.property.symbol) }
        component.properties.forEach { componentPropertySymbols.add(it.symbol) }

        component.actionBlocks.forEach {
            passBlock(it.block, if (it.actionBlockType == ActionBlockType.SEQ) componentPropertySymbols else null)
        }
        component.methodBlocks.forEach { passBlock(it.block, null) }
    }

    override fun passCls(cls: PsCls) {
        cls.methodBlocks.forEach { passBlock(it.block, null) }
    }

    private fun passBlock(block: PsBlock, componentPropertySymbols: Set<Symbol>?) {
        PsPassUtil.replaceBlock(block) {
            if (!it.isSubexpression) replace(it.expression, componentPropertySymbols)
            else null
        }
    }

    private fun replace(expression: PsExpression, componentPropertySymbols: Set<Symbol>?): PsExpression? {
        return if (expression is PsExpressionFunction
            && expression.functionSymbol == FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
        ) {
            var receiver = expression.receiver!!
            while (true) {
                receiver = if (receiver is PsExpressionFunction && receiver.receiver != null) receiver.receiver!!
                else break
            }
            val receiverSymbol = if (receiver is PsExpressionProperty) {
                receiver.propertySymbol
            } else throw LineException("property expression expected", expression.line)

            val functionSymbol = if (receiverSymbol in indexer.clockportPropertySymbols
                || (componentPropertySymbols != null && receiverSymbol in componentPropertySymbols)
            ) {
                FUNCTION_INTERNAL_ASSIGN_NONBLOCKING
            } else {
                FUNCTION_INTERNAL_ASSIGN_BLOCKING
            }
            PsExpressionFunction(
                expression.line,
                expression.typeGenerified,
                functionSymbol,
                expression.receiver,
                expression.args
            )
        } else null
    }

    private class Indexer: PsPassBase() {

        val clockportPropertySymbols = HashSet<Symbol>()

        override fun passComponent(component: PsComponent) {
            if (component.componentType == ComponentType.CLOCK_PORT) {
                component.ports.forEach { clockportPropertySymbols.add(it.property.symbol) }
            }
        }
    }
}
