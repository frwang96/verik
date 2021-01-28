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

package verikc.ps.pass

import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_NAME_ENUM
import verikc.lang.LangSymbol.FUNCTION_NATIVE_STRING
import verikc.lang.LangSymbol.FUNCTION_PRINTLN_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_PRINT_INSTANCE
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.ps.ast.*

class PsPassEnumName: PsPassBase() {

    private val indexer = Indexer()

    override fun pass(compilationUnit: PsCompilationUnit) {
        indexer.pass(compilationUnit)
        super.pass(compilationUnit)
    }

    override fun passBlock(block: PsBlock) {
        PsPassUtil.replaceBlock(block) {
            when (it.expression) {
                is PsExpressionFunction -> {
                    if (it.expression.functionSymbol
                        in listOf(FUNCTION_NATIVE_STRING, FUNCTION_PRINT_INSTANCE, FUNCTION_PRINTLN_INSTANCE)
                    ) {
                        replace(it.expression.args[0])?.let { arg -> it.expression.args[0] = arg }
                    }
                }
                else -> {}
            }
            null
        }
    }

    private fun replace(expression: PsExpression): PsExpression? {
        return if (expression.typeGenerified.typeSymbol in indexer.enumTypeSymbols) {
            return PsExpressionFunction(
                expression.line,
                TYPE_STRING.toTypeGenerified(),
                FUNCTION_INTERNAL_NAME_ENUM,
                expression,
                arrayListOf()
            )
        } else null
    }

    private class Indexer: PsPassBase() {

        val enumTypeSymbols = HashSet<Symbol>()

        override fun passEnum(enum: PsEnum) {
            enumTypeSymbols.add(enum.symbol)
        }
    }
}