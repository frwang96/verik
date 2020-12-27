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

package verikc.al.ast

import verikc.antlr.KotlinParser

object AlTerminal {

    val MULT: Int
    val MOD: Int
    val DIV: Int
    val ADD: Int
    val SUB: Int
    val INCR: Int
    val DECR: Int
    val CONJ: Int
    val DISJ: Int
    val ADD_ASSIGNMENT: Int
    val SUB_ASSIGNMENT: Int
    val MULT_ASSIGNMENT: Int
    val DIV_ASSIGNMENT: Int
    val MOD_ASSIGNMENT: Int
    val RANGE: Int
    val LANGLE: Int
    val RANGLE: Int
    val LE: Int
    val GE: Int
    val EXCL_EQ: Int
    val EQEQ: Int
    val CLASS: Int
    val FUN: Int
    val OBJECT: Int
    val VAL: Int
    val VAR: Int
    val ELSE: Int
    val RETURN: Int
    val CONTINUE: Int
    val BREAK: Int
    val IS: Int
    val IN: Int
    val NOT_IS: Int
    val NOT_IN: Int
    val INTEGER_LITERAL: Int
    val HEX_LITERAL: Int
    val BIN_LITERAL: Int
    val BOOLEAN_LITERAL: Int
    val QUOTE_OPEN: Int
    val QUOTE_CLOSE: Int
    val LINE_STR_REF: Int
    val LINE_STR_TEXT: Int
    val LINE_STR_ESCAPED_CHAR: Int

    private val offset: Int

    fun index(index: Int): Int {
        return index + offset
    }

    init {
        val terminalMap = TerminalMap()

        MULT = terminalMap.index("MULT")
        MOD = terminalMap.index("MOD")
        DIV = terminalMap.index("DIV")
        ADD = terminalMap.index("ADD")
        SUB = terminalMap.index("SUB")
        INCR = terminalMap.index("INCR")
        DECR = terminalMap.index("DECR")
        CONJ = terminalMap.index("CONJ")
        DISJ = terminalMap.index("DISJ")
        ADD_ASSIGNMENT = terminalMap.index("ADD_ASSIGNMENT")
        SUB_ASSIGNMENT = terminalMap.index("SUB_ASSIGNMENT")
        MULT_ASSIGNMENT = terminalMap.index("MULT_ASSIGNMENT")
        DIV_ASSIGNMENT = terminalMap.index("DIV_ASSIGNMENT")
        MOD_ASSIGNMENT = terminalMap.index("MOD_ASSIGNMENT")
        RANGE = terminalMap.index("RANGE")
        LANGLE = terminalMap.index("LANGLE")
        RANGLE = terminalMap.index("RANGLE")
        LE = terminalMap.index("LE")
        GE = terminalMap.index("GE")
        EXCL_EQ = terminalMap.index("EXCL_EQ")
        EQEQ = terminalMap.index("EQEQ")
        CLASS = terminalMap.index("CLASS")
        FUN = terminalMap.index("FUN")
        OBJECT = terminalMap.index("OBJECT")
        VAL = terminalMap.index("VAL")
        VAR = terminalMap.index("VAR")
        ELSE = terminalMap.index("ELSE")
        RETURN = terminalMap.index("RETURN")
        CONTINUE = terminalMap.index("CONTINUE")
        BREAK = terminalMap.index("BREAK")
        IS = terminalMap.index("IS")
        IN = terminalMap.index("IN")
        NOT_IS = terminalMap.index("NOT_IS")
        NOT_IN = terminalMap.index("NOT_IN")
        INTEGER_LITERAL = terminalMap.index("IntegerLiteral")
        HEX_LITERAL = terminalMap.index("HexLiteral")
        BIN_LITERAL = terminalMap.index("BinLiteral")
        BOOLEAN_LITERAL = terminalMap.index("BooleanLiteral")
        QUOTE_OPEN = terminalMap.index("QUOTE_OPEN")
        QUOTE_CLOSE = terminalMap.index("QUOTE_CLOSE")
        LINE_STR_REF = terminalMap.index("LineStrRef")
        LINE_STR_TEXT = terminalMap.index("LineStrText")
        LINE_STR_ESCAPED_CHAR = terminalMap.index("LineStrEscapedChar")

        offset = terminalMap.offset
    }

    private class TerminalMap {

        val parser = KotlinParser(null)
        val offset = parser.ruleNames.size

        fun index(string: String): Int {
            return parser.tokenTypeMap[string]!! + offset
        }
    }
}