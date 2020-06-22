package com.verik.core.kt

import com.verik.antlr.KotlinLexer
import com.verik.antlr.KotlinParser
import com.verik.core.LinePos
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.StringBuilder
import java.util.*

// Copyright (c) 2020 Francis Wang

data class KtTree(val node: KtNode, val linePos: LinePos, val children: List<KtTree>) {

    fun childrenContains(token: KtTokenType): Boolean {
        return children.any { it.node is KtToken && it.node.type == token }
    }

    fun childrenContains(rule: KtRuleType): Boolean {
        return children.any { it.node is KtRule && it.node.type == rule }
    }

    fun countRuleNodes(): Int {
        var count = if (node is KtRule) 1 else 0
        for (child in children) {
            count += child.countRuleNodes()
        }
        return count
    }

    fun countTokenNodes(): Int {
        var count = if (node is KtToken) 1 else 0
        for (child in children) {
            count += child.countTokenNodes()
        }
        return count
    }

    override fun toString(): String {
        fun toStringRecursive(tree: KtTree, stem: Stack<Boolean>, builder: StringBuilder) {
            if (stem.isNotEmpty()) {
                for (s in stem.dropLast(1)) {
                    builder.append(if (s) "│  " else "   ")
                }
                builder.append(if (stem.peek()) "├─ " else "└─ ")
            }
            builder.append(when (tree.node) {
                is KtToken -> "" + tree.node.type + " " + tree.node.text.replace("\n", "\\n")
                is KtRule -> "" + tree.node.type
            })
            builder.append("\n")
            for (child in tree.children.dropLast(1)) {
                stem.push(true)
                toStringRecursive(child, stem, builder)
                stem.pop()
            }
            if (tree.children.isNotEmpty()) {
                stem.push(false)
                toStringRecursive(tree.children.last(), stem, builder)
                stem.pop()
            }
        }
        val builder = StringBuilder()
        toStringRecursive(this, Stack(), builder)
        return builder.toString()
    }

    companion object {
        private operator fun invoke(tree: ParseTree): KtTree {
            val (_, ktTree) = build(LinePos(0, 0), tree)
            if (ktTree == null) {
                throw KtParseException(LinePos(0, 0), "unable to parse root node of syntax tree")
            } else {
                KtTreeReducer.reduce(ktTree)
                return ktTree
            }
        }

        private fun build(linePos: LinePos, tree: ParseTree): Pair<LinePos, KtTree?> {
            return when (tree) {
                is TerminalNode -> {
                    if (tree.symbol.text.chars().anyMatch{ it >= 0x80 }) {
                        throw KtParseException(linePos, "only ASCII characters are permitted")
                    }
                    val tokenName = KotlinLexer.VOCABULARY.getSymbolicName(tree.symbol.type)
                    if (KtTokenType.isIgnored(tokenName)) {
                        Pair(linePos.advance(tree.symbol.text), null)
                    } else {
                        val tokenType = KtTokenType.getType(tokenName)
                        if (tokenType == null) {
                            throw KtParseException(linePos, "lexer token type \"$tokenName\" is not supported")
                        } else {
                            val ktTree = KtTree(KtToken(tokenType, tree.symbol.text), linePos, listOf())
                            Pair(linePos.advance(tree.symbol.text), ktTree)
                        }
                    }
                }
                is RuleContext -> {
                    val children = ArrayList<KtTree>()
                    var currentLinePos = linePos
                    for (i in 0 until tree.childCount) {
                        val (newLinePos, child) = build(currentLinePos, tree.getChild(i))
                        currentLinePos = newLinePos
                        if (child != null) children.add(child)
                    }
                    val ruleName = KotlinParser.ruleNames[tree.ruleIndex]
                    if (KtRuleType.isIgnored(ruleName)) {
                        Pair(currentLinePos, null)
                    } else {
                        val ruleType = KtRuleType.getType(ruleName)
                        if (ruleType == null) {
                            throw KtParseException(linePos, "parser rule type \"$ruleName\" is not supported")
                        } else {
                            val ktTree = KtTree(KtRule(ruleType), linePos, children)
                            return Pair(currentLinePos, ktTree)
                        }
                    }
                }
                else -> throw KtParseException(linePos, "unable to parse node class \"${tree::class}\"")
            }
        }

        private fun getParser(input: InputStream): KotlinParser {
            val errorListener = object: BaseErrorListener() {
                override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                                         charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                    throw KtAntlrException(LinePos(line, charPositionInLine), msg ?: "")
                }
            }
            val lexer = KotlinLexer(CharStreams.fromStream(input))
            lexer.removeErrorListeners()
            lexer.addErrorListener(errorListener)
            val parser = KotlinParser(CommonTokenStream(lexer))
            parser.removeErrorListeners()
            parser.addErrorListener(errorListener)
            return parser
        }

        fun parseKotlinFile(input: InputStream): KtTree {
            val parser = getParser(input)
            return KtTree(parser.kotlinFile())
        }

        fun parseKotlinFile(input: String): KtTree {
            return parseKotlinFile(ByteArrayInputStream(input.toByteArray()))
        }
    }
}