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

package io.verik.core.kt

import io.verik.antlr.KotlinLexer
import io.verik.antlr.KotlinParser
import io.verik.core.FileLine
import io.verik.core.FileLineException
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.*

class KtRuleParser {

    companion object {

        fun parseKotlinFile(file: String, input: String): KtRule {
            val parser = getParser(file, input)
            return build(file, parser.kotlinFile())
        }

        fun parseKotlinFile(input: String): KtRule {
            return parseKotlinFile("", input)
        }

        fun parseDeclaration(input: String): KtRule {
            val parser = getParser("", input)
            return build("", parser.declaration())
        }

        fun parseExpression(input: String): KtRule {
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

        private fun build(file: String, tree: ParseTree): KtRule {
            val (_, ktTree) = buildRecursive(file, tree)
            if (ktTree == null) {
                throw FileLineException("unable to parse root node of syntax tree", FileLine(file))
            } else {
                if (ktTree is KtRule) {
                    KtRuleReducer.reduce(ktTree)
                    return ktTree
                } else {
                    throw FileLineException("root node of syntax tree must be a rule", FileLine(file))
                }
            }
        }

        private fun buildRecursive(file:String, tree: ParseTree): Pair<FileLine, KtNode?> {
            return when (tree) {
                is TerminalNode -> {
                    val fileLine = FileLine(file, tree.symbol.line)
                    if (tree.symbol.text.chars().anyMatch{ it >= 0x80 }) {
                        throw FileLineException("only ASCII characters are permitted", fileLine)
                    }
                    val tokenName = KotlinLexer.VOCABULARY.getSymbolicName(tree.symbol.type)
                    if (KtTokenType.isIgnored(tokenName)) {
                        Pair(fileLine, null)
                    } else {
                        val tokenType = KtTokenType(tokenName, fileLine)
                        val ktTree = KtToken(fileLine, tokenType, tree.symbol.text)
                        Pair(fileLine, ktTree)
                    }
                }
                is RuleContext -> {
                    val children = ArrayList<KtNode>()
                    var fileLine = FileLine()
                    for (i in 0 until tree.childCount) {
                        val (childFileLine, child) = buildRecursive(file, tree.getChild(i))
                        if (fileLine == FileLine()) fileLine = childFileLine
                        if (child != null) children.add(child)
                    }
                    val ruleName = KotlinParser.ruleNames[tree.ruleIndex]
                    if (KtRuleType.isIgnored(ruleName)) {
                        Pair(fileLine, null)
                    } else {
                        val ruleType = KtRuleType(ruleName, fileLine)
                        val ktTree = KtRule(fileLine, ruleType, children)
                        return Pair(fileLine, ktTree)
                    }
                }
                else -> throw FileLineException("unable to parse node class \"${tree::class}\"", FileLine(file))
            }
        }
    }
}