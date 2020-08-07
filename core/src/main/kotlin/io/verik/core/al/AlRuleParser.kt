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

package io.verik.core.al

import io.verik.antlr.KotlinLexer
import io.verik.antlr.KotlinParser
import io.verik.core.FileLine
import io.verik.core.FileLineException
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.*

class AlRuleParser {

    companion object {

        fun parseKotlinFile(file: String, input: String): AlRule {
            val parser = getParser(file, input)
            return build(file, parser.kotlinFile())
        }

        fun parseKotlinFile(input: String): AlRule {
            return parseKotlinFile("", input)
        }

        fun parseDeclaration(input: String): AlRule {
            val parser = getParser("", input)
            return build("", parser.declaration())
        }

        fun parseExpression(input: String): AlRule {
            val parser = getParser("", input)
            return build("", parser.expression())
        }

        private fun getParser(file: String, input: String): KotlinParser {
            val errorListener = object: BaseErrorListener() {
                override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                                         charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                    throw FileLineException(msg ?: "antlr syntax error", FileLine(file, line))
                }
            }
            val lexer = KotlinLexer(CharStreams.fromString(input))
            lexer.removeErrorListeners()
            lexer.addErrorListener(errorListener)
            val parser = KotlinParser(CommonTokenStream(lexer))
            parser.removeErrorListeners()
            parser.addErrorListener(errorListener)
            return parser
        }

        private fun build(file: String, tree: ParseTree): AlRule {
            val (_, alTree) = buildRecursive(file, tree)
            if (alTree == null) {
                throw FileLineException("unable to parse root node of syntax tree", FileLine(file))
            } else {
                if (alTree is AlRule) {
                    AlRuleReducer.reduce(alTree)
                    return alTree
                } else {
                    throw FileLineException("root node of syntax tree must be a rule", FileLine(file))
                }
            }
        }

        private fun buildRecursive(file:String, tree: ParseTree): Pair<FileLine, AlNode?> {
            return when (tree) {
                is TerminalNode -> {
                    val fileLine = FileLine(file, tree.symbol.line)
                    if (tree.symbol.text.chars().anyMatch{ it >= 0x80 }) {
                        throw FileLineException("only ASCII characters are permitted", fileLine)
                    }
                    val tokenName = KotlinLexer.VOCABULARY.getSymbolicName(tree.symbol.type)
                    if (AlTokenType.isIgnored(tokenName)) {
                        Pair(fileLine, null)
                    } else {
                        val tokenType = AlTokenType(tokenName, fileLine)
                        val alTree = AlToken(fileLine, tokenType, tree.symbol.text)
                        Pair(fileLine, alTree)
                    }
                }
                is RuleContext -> {
                    val children = ArrayList<AlNode>()
                    var fileLine = FileLine()
                    for (i in 0 until tree.childCount) {
                        val (childFileLine, child) = buildRecursive(file, tree.getChild(i))
                        if (fileLine == FileLine()) fileLine = childFileLine
                        if (child != null) children.add(child)
                    }
                    val ruleName = KotlinParser.ruleNames[tree.ruleIndex]
                    if (AlRuleType.isIgnored(ruleName)) {
                        Pair(fileLine, null)
                    } else {
                        val ruleType = AlRuleType(ruleName, fileLine)
                        val alTree = AlRule(fileLine, ruleType, children)
                        return Pair(fileLine, alTree)
                    }
                }
                else -> throw FileLineException("unable to parse node class \"${tree::class}\"", FileLine(file))
            }
        }
    }
}