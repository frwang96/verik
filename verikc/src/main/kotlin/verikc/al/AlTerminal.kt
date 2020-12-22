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

package verikc.al

import verikc.antlr.KotlinParser

object AlTerminal {

    val MULT: Int
    val MOD: Int
    val DIV: Int
    val ADD: Int
    val SUB: Int
    val INCR: Int
    val DECR: Int
    val EXCL_WS: Int
    val EXCL_NO_WS: Int
    val ADD_ASSIGNMENT: Int
    val SUB_ASSIGNMENT: Int
    val MULT_ASSIGNMENT: Int
    val DIV_ASSIGNMENT: Int
    val MOD_ASSIGNMENT: Int
    val AT_NO_WS: Int
    val AT_PRE_WS: Int
    val LANGLE: Int
    val RANGLE: Int
    val LE: Int
    val GE: Int
    val EXCL_EQ: Int
    val EQEQ: Int
    val FILE: Int
    val FIELD: Int
    val PROPERTY: Int
    val GET: Int
    val SET: Int
    val RECEIVER: Int
    val PARAM: Int
    val SETPARAM: Int
    val DELEGATE: Int
    val PACKAGE: Int
    val IMPORT: Int
    val CLASS: Int
    val FUN: Int
    val OBJECT: Int
    val VAL: Int
    val VAR: Int
    val CONSTRUCTOR: Int
    val BY: Int
    val COMPANION: Int
    val INIT: Int
    val THIS: Int
    val SUPER: Int
    val WHERE: Int
    val ELSE: Int
    val CATCH: Int
    val FINALLY: Int
    val RETURN: Int
    val CONTINUE: Int
    val BREAK: Int
    val AS: Int
    val IS: Int
    val IN: Int
    val NOT_IS: Int
    val NOT_IN: Int
    val OUT: Int
    val DYNAMIC: Int
    val PUBLIC: Int
    val PRIVATE: Int
    val PROTECTED: Int
    val INTERNAL: Int
    val ENUM: Int
    val SEALED: Int
    val ANNOTATION: Int
    val DATA: Int
    val INNER: Int
    val TAILREC: Int
    val OPERATOR: Int
    val INLINE: Int
    val INFIX: Int
    val EXTERNAL: Int
    val SUSPEND: Int
    val OVERRIDE: Int
    val ABSTRACT: Int
    val FINAL: Int
    val OPEN: Int
    val CONST: Int
    val LATEINIT: Int
    val VARARG: Int
    val NOINLINE: Int
    val CROSSINLINE: Int
    val REIFIED: Int
    val EXPECT: Int
    val ACTUAL: Int
    val INTEGER_LITERAL: Int
    val HEX_LITERAL: Int
    val BIN_LITERAL: Int
    val BOOLEAN_LITERAL: Int
    val IDENTIFIER: Int
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
        EXCL_WS = terminalMap.index("EXCL_WS")
        EXCL_NO_WS = terminalMap.index("EXCL_NO_WS")
        ADD_ASSIGNMENT = terminalMap.index("ADD_ASSIGNMENT")
        SUB_ASSIGNMENT = terminalMap.index("SUB_ASSIGNMENT")
        MULT_ASSIGNMENT = terminalMap.index("MULT_ASSIGNMENT")
        DIV_ASSIGNMENT = terminalMap.index("DIV_ASSIGNMENT")
        MOD_ASSIGNMENT = terminalMap.index("MOD_ASSIGNMENT")
        AT_NO_WS = terminalMap.index("AT_NO_WS")
        AT_PRE_WS = terminalMap.index("AT_PRE_WS")
        LANGLE = terminalMap.index("LANGLE")
        RANGLE = terminalMap.index("RANGLE")
        LE = terminalMap.index("LE")
        GE = terminalMap.index("GE")
        EXCL_EQ = terminalMap.index("EXCL_EQ")
        EQEQ = terminalMap.index("EQEQ")
        FILE = terminalMap.index("FILE")
        FIELD = terminalMap.index("FIELD")
        PROPERTY = terminalMap.index("PROPERTY")
        GET = terminalMap.index("GET")
        SET = terminalMap.index("SET")
        RECEIVER = terminalMap.index("RECEIVER")
        PARAM = terminalMap.index("PARAM")
        SETPARAM = terminalMap.index("SETPARAM")
        DELEGATE = terminalMap.index("DELEGATE")
        PACKAGE = terminalMap.index("PACKAGE")
        IMPORT = terminalMap.index("IMPORT")
        CLASS = terminalMap.index("CLASS")
        FUN = terminalMap.index("FUN")
        OBJECT = terminalMap.index("OBJECT")
        VAL = terminalMap.index("VAL")
        VAR = terminalMap.index("VAR")
        CONSTRUCTOR = terminalMap.index("CONSTRUCTOR")
        BY = terminalMap.index("BY")
        COMPANION = terminalMap.index("COMPANION")
        INIT = terminalMap.index("INIT")
        THIS = terminalMap.index("THIS")
        SUPER = terminalMap.index("SUPER")
        WHERE = terminalMap.index("WHERE")
        ELSE = terminalMap.index("ELSE")
        CATCH = terminalMap.index("CATCH")
        FINALLY = terminalMap.index("FINALLY")
        RETURN = terminalMap.index("RETURN")
        CONTINUE = terminalMap.index("CONTINUE")
        BREAK = terminalMap.index("BREAK")
        AS = terminalMap.index("AS")
        IS = terminalMap.index("IS")
        IN = terminalMap.index("IN")
        NOT_IS = terminalMap.index("NOT_IS")
        NOT_IN = terminalMap.index("NOT_IN")
        OUT = terminalMap.index("OUT")
        DYNAMIC = terminalMap.index("DYNAMIC")
        PUBLIC = terminalMap.index("PUBLIC")
        PRIVATE = terminalMap.index("PRIVATE")
        PROTECTED = terminalMap.index("PROTECTED")
        INTERNAL = terminalMap.index("INTERNAL")
        ENUM = terminalMap.index("ENUM")
        SEALED = terminalMap.index("SEALED")
        ANNOTATION = terminalMap.index("ANNOTATION")
        DATA = terminalMap.index("DATA")
        INNER = terminalMap.index("INNER")
        TAILREC = terminalMap.index("TAILREC")
        OPERATOR = terminalMap.index("OPERATOR")
        INLINE = terminalMap.index("INLINE")
        INFIX = terminalMap.index("INFIX")
        EXTERNAL = terminalMap.index("EXTERNAL")
        SUSPEND = terminalMap.index("SUSPEND")
        OVERRIDE = terminalMap.index("OVERRIDE")
        ABSTRACT = terminalMap.index("ABSTRACT")
        FINAL = terminalMap.index("FINAL")
        OPEN = terminalMap.index("OPEN")
        CONST = terminalMap.index("CONST")
        LATEINIT = terminalMap.index("LATEINIT")
        VARARG = terminalMap.index("VARARG")
        NOINLINE = terminalMap.index("NOINLINE")
        CROSSINLINE = terminalMap.index("CROSSINLINE")
        REIFIED = terminalMap.index("REIFIED")
        EXPECT = terminalMap.index("EXPECT")
        ACTUAL = terminalMap.index("ACTUAL")
        INTEGER_LITERAL = terminalMap.index("IntegerLiteral")
        HEX_LITERAL = terminalMap.index("HexLiteral")
        BIN_LITERAL = terminalMap.index("BinLiteral")
        BOOLEAN_LITERAL = terminalMap.index("BooleanLiteral")
        IDENTIFIER = terminalMap.index("Identifier")
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