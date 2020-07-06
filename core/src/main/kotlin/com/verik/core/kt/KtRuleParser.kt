package com.verik.core.kt

import com.verik.antlr.KotlinLexer
import com.verik.antlr.KotlinParser
import com.verik.core.LinePos
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.*

// Copyright (c) 2020 Francis Wang

class KtRuleParser {

    companion object {

        fun parseKotlinFile(input: String): KtRule {
            val parser = getParser(input)
            return build(parser.kotlinFile())
        }

        fun parseDeclaration(input: String): KtRule {
            val parser = getParser(input)
            return build(parser.declaration())
        }

        fun parseExpression(input: String): KtRule {
            val parser = getParser(input)
            return build(parser.expression())
        }

        private fun getParser(input: String): KotlinParser {
            val errorListener = object: BaseErrorListener() {
                override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                                         charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                    throw KtAntlrException(msg ?: "", LinePos(line, charPositionInLine))
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

        private fun build(tree: ParseTree): KtRule {
            val (_, ktTree) = buildRecursive(tree)
            if (ktTree == null) {
                throw KtParseException("unable to parse root node of syntax tree", LinePos.ZERO)
            } else {
                if (ktTree is KtRule) {
                    KtRuleReducer.reduce(ktTree)
                    return ktTree
                } else {
                    throw KtParseException("root node of syntax tree must be a rule", LinePos.ZERO)
                }
            }
        }

        private fun buildRecursive(tree: ParseTree): Pair<LinePos, KtNode?> {
            return when (tree) {
                is TerminalNode -> {
                    val linePos = LinePos(tree.symbol.line, tree.symbol.charPositionInLine + 1)
                    if (tree.symbol.text.chars().anyMatch{ it >= 0x80 }) {
                        throw KtParseException("only ASCII characters are permitted", linePos)
                    }
                    val tokenName = KotlinLexer.VOCABULARY.getSymbolicName(tree.symbol.type)
                    if (KtTokenType.isIgnored(tokenName)) {
                        Pair(linePos, null)
                    } else {
                        val tokenType = KtTokenType(tokenName, KtParseException("lexer token type \"$tokenName\" is not supported", linePos))
                        val ktTree = KtToken(linePos, tokenType, tree.symbol.text)
                        Pair(linePos, ktTree)
                    }
                }
                is RuleContext -> {
                    val children = ArrayList<KtNode>()
                    var linePos: LinePos = LinePos.ZERO
                    for (i in 0 until tree.childCount) {
                        val (childLinePos, child) = buildRecursive(tree.getChild(i))
                        if (linePos == LinePos.ZERO) linePos = childLinePos
                        if (child != null) children.add(child)
                    }
                    val ruleName = KotlinParser.ruleNames[tree.ruleIndex]
                    if (KtRuleType.isIgnored(ruleName)) {
                        Pair(linePos, null)
                    } else {
                        val ruleType = KtRuleType(ruleName, KtParseException("parser rule type \"$ruleName\" is not supported", linePos))
                        val ktTree = KtRule(linePos, ruleType, children)
                        return Pair(linePos, ktTree)
                    }
                }
                else -> throw KtParseException("unable to parse node class \"${tree::class}\"", LinePos.ZERO)
            }
        }
    }
}