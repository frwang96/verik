package com.verik.core.kt

import com.verik.antlr.KotlinLexer
import com.verik.antlr.KotlinParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.StringBuilder
import java.util.*

// Copyright (c) 2020 Francis Wang

data class KtTree(val node: KtNode, val line: Int, val pos: Int, val children: List<KtTree>) {

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
        operator fun invoke(tree: ParseTree): KtTree {
            return when (tree) {
                is TerminalNode -> {
                    val tokenName = KotlinLexer.VOCABULARY.getSymbolicName(tree.symbol.type)
                    val tokenType = KtTokenType.getType(tokenName)
                    if (tokenType == null) {
                        throw KtParseException(tree.symbol.line, tree.symbol.charPositionInLine, "antlr lexer token type \"$tokenName\" is not supported")
                    } else {
                        KtTree(KtToken(tokenType, tree.symbol.text), tree.symbol.line, tree.symbol.charPositionInLine, listOf())
                    }
                }
                is RuleContext -> {
                    val ruleName = KotlinParser.ruleNames[tree.ruleIndex]
                    val ruleType = KtRuleType.getType(ruleName)
                    if (ruleType == null) {
                        var child = tree
                        while (child.childCount > 0) {
                            child = child.getChild(0)
                        }
                        val (line, pos) = when (child) {
                            is TerminalNode -> Pair(child.symbol.line, child.symbol.charPositionInLine)
                            else -> Pair(0, 0)
                        }
                        throw KtParseException(line, pos, "antlr parser rule type \"$ruleName\" is not supported")
                    } else {
                        val children = (0 until tree.childCount).map { KtTree(tree.getChild(it)) }
                        val (line, pos) = if (children.isNotEmpty()) {
                            Pair(children[0].line, children[0].pos)
                        } else {
                            Pair(0, 0)
                        }
                        KtTree(KtRule(ruleType), line, pos, children)
                    }
                }
                else -> throw KtParseException(0, 0, "unable to parse antlr node class \"${tree::class}\"")
            }
        }

        private fun parse(input: InputStream): KotlinParser {
            val errorListener = object: BaseErrorListener() {
                override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                                         charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                    throw KtAntlrException(line, charPositionInLine, msg ?: "")
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
            val parser = parse(input)
            return KtTree(parser.kotlinFile())
        }

        fun parseKotlinFile(input: String): KtTree {
            return parseKotlinFile(ByteArrayInputStream(input.toByteArray()))
        }
    }
}