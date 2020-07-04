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
                    throw KtAntlrException(LinePos(line, charPositionInLine), msg ?: "")
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
            val (_, ktTree) = buildRecursive(LinePos(0, 0), tree)
            if (ktTree == null) {
                throw KtParseException(LinePos(0, 0), "unable to parse root node of syntax tree")
            } else {
                if (ktTree is KtRule) {
                    KtRuleReducer.reduce(ktTree)
                    return ktTree
                } else {
                    throw KtParseException(LinePos(0, 0), "root node of syntax tree must be a rule")
                }
            }
        }

        private fun buildRecursive(linePos: LinePos, tree: ParseTree): Pair<LinePos, KtNode?> {
            return when (tree) {
                is TerminalNode -> {
                    if (tree.symbol.text.chars().anyMatch{ it >= 0x80 }) {
                        throw KtParseException(linePos, "only ASCII characters are permitted")
                    }
                    val tokenName = KotlinLexer.VOCABULARY.getSymbolicName(tree.symbol.type)
                    if (KtTokenType.isIgnored(tokenName)) {
                        Pair(linePos.advance(tree.symbol.text), null)
                    } else {
                        val tokenType = KtTokenType(tokenName, KtParseException(linePos, "lexer token type \"$tokenName\" is not supported"))
                        val ktTree = KtToken(linePos, tokenType, tree.symbol.text)
                        Pair(linePos.advance(tree.symbol.text), ktTree)
                    }
                }
                is RuleContext -> {
                    val children = ArrayList<KtNode>()
                    var currentLinePos = linePos
                    for (i in 0 until tree.childCount) {
                        val (newLinePos, child) = buildRecursive(currentLinePos, tree.getChild(i))
                        currentLinePos = newLinePos
                        if (child != null) children.add(child)
                    }
                    val ruleName = KotlinParser.ruleNames[tree.ruleIndex]
                    if (KtRuleType.isIgnored(ruleName)) {
                        Pair(currentLinePos, null)
                    } else {
                        val ruleType = KtRuleType(ruleName, KtParseException(linePos, "parser rule type \"$ruleName\" is not supported"))
                        val ktTree = KtRule(linePos, ruleType, children)
                        return Pair(currentLinePos, ktTree)
                    }
                }
                else -> throw KtParseException(linePos, "unable to parse node class \"${tree::class}\"")
            }
        }
    }
}