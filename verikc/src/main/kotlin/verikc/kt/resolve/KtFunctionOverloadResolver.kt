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

package verikc.kt.resolve

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.kt.symbol.KtFunctionEntry

object KtFunctionOverloadResolver {

    fun matches(argsParentTypeSymbols: List<List<Symbol>>, functionEntry: KtFunctionEntry): Boolean {
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

    fun dominatingFunctionEntry(
        functionEntries: List<KtFunctionEntry>,
        functionsArgsParentTypeSymbols: List<List<List<Symbol>>>,
        line: Line
    ): KtFunctionEntry {
        val dominatingFunctionEntries = ArrayList<KtFunctionEntry>()
        for (i in functionEntries.indices) {
            var dominating = true
            for (j in functionEntries.indices) {
                if (j != i) {
                    val dominates = dominates(functionEntries[i], functionsArgsParentTypeSymbols[i], functionEntries[j])
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

    fun dominates(
        functionEntry: KtFunctionEntry,
        functionArgsParentTypeSymbols: List<List<Symbol>>,
        comparisonFunctionEntry: KtFunctionEntry
    ): Boolean {
        if (functionEntry.isVararg) return false
        if (comparisonFunctionEntry.isVararg) return true
        if (functionArgsParentTypeSymbols.size != comparisonFunctionEntry.argTypeSymbols.size) return false

        for (i in functionArgsParentTypeSymbols.indices) {
            if (comparisonFunctionEntry.argTypeSymbols[i] !in functionArgsParentTypeSymbols[i]) return false
        }
        return true
    }
}