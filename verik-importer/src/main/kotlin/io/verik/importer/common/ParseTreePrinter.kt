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

package io.verik.importer.common

import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

object ParseTreePrinter {

    fun dump(parseTree: ParseTree): String {
        val builder = StringBuilder()
        dump(parseTree, builder)
        return builder.toString()
    }

    private fun dump(parseTree: ParseTree, builder: StringBuilder) {
        when (parseTree) {
            is RuleContext -> {
                val childCount = parseTree.childCount
                if (childCount == 1) {
                    dump(parseTree.getChild(0), builder)
                } else {
                    val ruleName = SystemVerilogPreprocessorParser.ruleNames[parseTree.ruleIndex]
                    builder.append("$ruleName(")
                    (0 until parseTree.childCount).forEach {
                        if (it != 0)
                            builder.append(", ")
                        dump(parseTree.getChild(it), builder)
                    }
                    builder.append(")")
                }
            }
            is TerminalNode -> {
                val tokenIndex = parseTree.symbol.tokenIndex
                builder.append(SystemVerilogPreprocessorParser.VOCABULARY.getSymbolicName(tokenIndex))
            }
        }
    }
}
