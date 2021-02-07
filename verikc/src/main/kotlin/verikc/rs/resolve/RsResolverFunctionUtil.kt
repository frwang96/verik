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

package verikc.rs.resolve

import verikc.base.ast.ExpressionClass
import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_WITH_COMPONENT
import verikc.lang.resolve.LangResolverCommon
import verikc.rs.ast.RsExpressionFunction
import verikc.rs.table.RsFunctionEntry
import verikc.rs.table.RsFunctionEntryRegular
import verikc.rs.table.RsResolveException
import verikc.rs.table.RsSymbolTable

object RsResolverFunctionUtil {

    fun matches(argsParentTypeSymbols: List<List<Symbol>>, functionEntry: RsFunctionEntry): Boolean {
        if (functionEntry.isVararg) {
            val varargCount = argsParentTypeSymbols.size - functionEntry.argTypeSymbols.size + 1
            if (varargCount < 0) return false
            val noVarargCount = functionEntry.argTypeSymbols.size - 1
            val varargTypeSymbol = functionEntry.argTypeSymbols.last()

            for(i in 0 until noVarargCount) {
                if (functionEntry.argTypeSymbols[i] !in argsParentTypeSymbols[i]) return false
            }
            for (i in 0 until varargCount) {
                if (varargTypeSymbol !in argsParentTypeSymbols[i + noVarargCount]) return false
            }
            return true
        } else {
            if (argsParentTypeSymbols.size != functionEntry.argTypeSymbols.size) return false
            for (i in argsParentTypeSymbols.indices) {
                if (functionEntry.argTypeSymbols[i] !in argsParentTypeSymbols[i]) return false
            }
            return true
        }
    }

    fun dominatingEntry(
        functionEntries: List<RsFunctionEntry>,
        symbolTable: RsSymbolTable,
        line: Line
    ): RsFunctionEntry {
        val dominatingFunctionEntries = ArrayList<RsFunctionEntry>()
        for (i in functionEntries.indices) {
            val functionArgsParentTypeSymbols = functionEntries[i].argTypeSymbols
                .map { symbolTable.getParentTypeSymbols(it, line) }
            var dominating = true
            for (j in functionEntries.indices) {
                if (j != i) {
                    val dominates = dominates(functionEntries[i], functionArgsParentTypeSymbols, functionEntries[j])
                    if (!dominates) {
                        dominating = false
                        break
                    }
                }
            }
            if (dominating) dominatingFunctionEntries.add(functionEntries[i])
        }

        if (dominatingFunctionEntries.size != 1)
            throw LineException("unable to resolve function overload ambiguity", line)
        return dominatingFunctionEntries[0]
    }

    fun validate(expression: RsExpressionFunction, functionEntry: RsFunctionEntry) {
        expression.receiver?.let {
            compareExpressionClass(
                if (functionEntry.symbol != FUNCTION_WITH_COMPONENT) VALUE else TYPE,
                it.getExpressionClassNotNull(),
                "receiver",
                functionEntry.symbol,
                expression.line
            )
        }
        for (i in expression.args.indices) {
            compareExpressionClass(
                functionEntry.getArgExpressionClass(i),
                expression.args[i].getExpressionClassNotNull(),
                "argument ${i + 1}",
                functionEntry.symbol,
                expression.line
            )
        }
        if (functionEntry is RsFunctionEntryRegular) {
            expression.args.forEachIndexed { index, arg ->
                val argTypesGenerified = functionEntry.argTypesGenerified
                    ?: throw RsResolveException(functionEntry.symbol, expression.line)
                val expectedTypeGenerified = argTypesGenerified[index]
                LangResolverCommon.inferWidthIfBit(arg, expectedTypeGenerified)
                LangResolverCommon.matchTypes(arg, expectedTypeGenerified)
            }
        }
    }

    private fun dominates(
        functionEntry: RsFunctionEntry,
        functionArgsParentTypeSymbols: List<List<Symbol>>,
        comparisonFunctionEntry: RsFunctionEntry
    ): Boolean {
        if (functionEntry.isVararg) return false
        if (comparisonFunctionEntry.isVararg) return true
        if (functionArgsParentTypeSymbols.size != comparisonFunctionEntry.argTypeSymbols.size) return false

        for (i in functionArgsParentTypeSymbols.indices) {
            if (comparisonFunctionEntry.argTypeSymbols[i] !in functionArgsParentTypeSymbols[i]) return false
        }
        return true
    }

    private fun compareExpressionClass(
        expectedExpressionClass: ExpressionClass,
        actualExpressionClass: ExpressionClass,
        string: String,
        functionSymbol: Symbol,
        line: Line
    ) {
        if (expectedExpressionClass != actualExpressionClass) {
            when (expectedExpressionClass) {
                TYPE ->
                    throw LineException("type expression expected in $string of function $functionSymbol", line)
                VALUE ->
                    throw LineException("type expression not permitted in $string of function $functionSymbol", line)
            }
        }
    }
}