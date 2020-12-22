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

package verikc.alx

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import verikc.antlr.KotlinLexer
import verikc.antlr.KotlinParser
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol

object AlxTreeParser {

    fun parseKotlinFile(file: Symbol, string: String): AlxTree {
        val parser = getParser(file, string)
        val parseTree = parser.kotlinFile()
        return build(file, parseTree)
    }

    private fun getParser(file: Symbol, string: String): KotlinParser {
        val lexerErrorListener = object: BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String?,
                e: RecognitionException?
            ) {
                throw LineException(msg ?: "antlr lexer error", Line(file, line))
            }
        }
        val parserErrorListener = object: BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String?,
                e: RecognitionException?
            ) {
                throw LineException(msg ?: "antlr parser error", Line(file, line))
            }
        }
        val lexer = KotlinLexer(CharStreams.fromString(string))
        lexer.removeErrorListeners()
        lexer.addErrorListener(lexerErrorListener)
        val parser = KotlinParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(parserErrorListener)
        return parser
    }

    private fun build(file: Symbol, parseTree: ParseTree): AlxTree {
        if (parseTree is ParserRuleContext) {
            val line = Line(file, parseTree.getStart().line)
            val children = ArrayList<AlxTree>()
            for (i in 0 until parseTree.childCount) {
                children.add(build(file, parseTree.getChild(i)))
            }
            return AlxTree(
                line,
                AlxRuleIndex.index(parseTree.ruleIndex),
                null,
                children
            )
        }
        if (parseTree is TerminalNode) {
            val line = Line(file, parseTree.symbol.line)
            val text = parseTree.symbol.text
            if (text.chars().anyMatch { it >= 0x80 }) {
                throw LineException("only ASCII characters are permitted", line)
            }
            return AlxTree(
                line,
                AlxTerminalIndex.index(parseTree.symbol.type),
                text,
                listOf()
            )
        }
        throw LineException("unable to build syntax tree", Line(file, 0))
    }
}