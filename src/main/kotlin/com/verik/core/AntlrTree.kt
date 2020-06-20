package com.verik.core

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

class AntlrTree private constructor(val tree: ParseTree) {

    fun countRuleNodes(): Int {
        fun countRuleNodesRecursive(tree: ParseTree): Int {
            var count = 0
            if (tree is RuleContext) count++
            for (i in 0 until tree.childCount) {
                count += countRuleNodesRecursive(tree.getChild(i))
            }
            return count
        }
        return countRuleNodesRecursive(tree)
    }

    fun countTerminalNodes(): Int {
        fun countTerminalNodesRecursive(tree: ParseTree): Int {
            var count = 0
            if (tree is TerminalNode) count++
            for (i in 0 until tree.childCount) {
                count += countTerminalNodesRecursive(tree.getChild(i))
            }
            return count
        }
        return countTerminalNodesRecursive(tree)
    }

    override fun toString(): String {
        val lexerVocabulary = KotlinLexer.VOCABULARY
        val parserRuleNames = KotlinParser.ruleNames
        fun toStringRecursive(tree: ParseTree, stem: Stack<Boolean>, builder: StringBuilder) {
            if (stem.isNotEmpty()) {
                for (s in stem.dropLast(1)) {
                    builder.append(if (s) "│  " else "   ")
                }
                builder.append(if (stem.peek()) "├─ " else "└─ ")
            }
            builder.append(when (tree) {
                is TerminalNode -> lexerVocabulary.getSymbolicName(tree.symbol.type) + " " + tree.symbol.text.replace("\n", "\\n")
                is RuleContext -> parserRuleNames[tree.ruleIndex]
                else -> ""
            })
            builder.append("\n")
            for (i in 0 until tree.childCount) {
                stem.push( i != tree.childCount - 1)
                toStringRecursive(tree.getChild(i), stem, builder)
                stem.pop()
            }
        }
        val builder = StringBuilder()
        toStringRecursive(tree, Stack(), builder)
        return builder.toString()
    }

    companion object {
        private fun parse(input: InputStream): KotlinParser {
            val errorListener = object: BaseErrorListener() {
                override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                                         charPositionInLine: Int, msg: String?, e: RecognitionException?) {
                    throw AntlrTreeParseException(line, charPositionInLine, msg?:"")
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

        fun parseKotlinFile(input: InputStream): AntlrTree {
            val parser = parse(input)
            return AntlrTree(parser.kotlinFile())
        }

        fun parseKotlinFile(input: String): AntlrTree {
            return parseKotlinFile(ByteArrayInputStream(input.toByteArray()))
        }
    }
}