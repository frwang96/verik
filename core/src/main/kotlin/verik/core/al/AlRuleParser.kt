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

package verik.core.al

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import verik.antlr.KotlinLexer
import verik.antlr.KotlinParser
import verik.core.main.LineException
import java.util.*

object AlRuleParser {

    fun parseKotlinFile(input: String): AlRule {
        val parser = getParser(input)
        return build(parser.kotlinFile())
    }

    fun parseDeclaration(input: String): AlRule {
        val parser = getParser(input)
        return build(parser.declaration())
    }

    fun parseExpression(input: String): AlRule {
        val parser = getParser(input)
        return build(parser.expression())
    }

    private fun getParser(input: String): KotlinParser {
        val errorListener = object: BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                                     charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                throw LineException(msg ?: "antlr syntax error", line)
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

    private fun build(tree: ParseTree): AlRule {
        val (_, alTree) = buildRecursive(tree)
        if (alTree == null) {
            throw LineException("unable to parse root node of syntax tree", 0)
        } else {
            if (alTree is AlRule) {
                AlRuleReducer.reduce(alTree)
                return alTree
            } else {
                throw LineException("root node of syntax tree must be a rule", 0)
            }
        }
    }

    private fun buildRecursive(tree: ParseTree): Pair<Int, AlNode?> {
        return when (tree) {
            is TerminalNode -> {
                val line = tree.symbol.line
                if (tree.symbol.text.chars().anyMatch{ it >= 0x80 }) {
                    throw LineException("only ASCII characters are permitted", line)
                }
                val tokenName = KotlinLexer.VOCABULARY.getSymbolicName(tree.symbol.type)
                if (AlTokenType.isIgnored(tokenName)) {
                    Pair(line, null)
                } else {
                    val tokenType = AlTokenType(tokenName, line)
                    val alTree = AlToken(line, tokenType, tree.symbol.text)
                    Pair(line, alTree)
                }
            }
            is RuleContext -> {
                val children = ArrayList<AlNode>()
                var line = 0
                for (i in 0 until tree.childCount) {
                    val (childLine, child) = buildRecursive(tree.getChild(i))
                    if (line == 0) line = childLine
                    if (child != null) children.add(child)
                }
                val ruleName = KotlinParser.ruleNames[tree.ruleIndex]
                if (AlRuleType.isIgnored(ruleName)) {
                    Pair(line, null)
                } else {
                    val ruleType = AlRuleType(ruleName, line)
                    val alTree = AlRule(line, ruleType, children)
                    return Pair(line, alTree)
                }
            }
            else -> throw LineException("unable to parse node class \"${tree::class}\"", 0)
        }
    }
}