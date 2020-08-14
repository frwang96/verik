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

import verik.core.main.LineException

enum class AlTokenType {
    MULT,
    MOD,
    DIV,
    ADD,
    SUB,
    EXCL_WS,
    EXCL_NO_WS,
    AT_NO_WS,
    AT_PRE_WS,
    LANGLE,
    RANGLE,
    LE,
    GE,
    FILE,
    FIELD,
    PROPERTY,
    GET,
    SET,
    RECEIVER,
    PARAM,
    SETPARAM,
    DELEGATE,
    PACKAGE,
    IMPORT,
    CLASS,
    FUN,
    VAL,
    CONSTRUCTOR,
    BY,
    COMPANION,
    INIT,
    THIS,
    SUPER,
    WHERE,
    ELSE,
    CATCH,
    FINALLY,
    RETURN,
    CONTINUE,
    BREAK,
    IN,
    NOT_IN,
    OUT,
    DYNAMIC,
    PUBLIC,
    PRIVATE,
    PROTECTED,
    INTERNAL,
    ENUM,
    SEALED,
    ANNOTATION,
    DATA,
    INNER,
    TAILREC,
    OPERATOR,
    INLINE,
    INFIX,
    EXTERNAL,
    SUSPEND,
    OVERRIDE,
    ABSTRACT,
    FINAL,
    OPEN,
    CONST,
    LATEINIT,
    VARARG,
    NOINLINE,
    CROSSINLINE,
    REIFIED,
    EXPECT,
    ACTUAL,
    INTEGER_LITERAL,
    HEX_LITERAL,
    BIN_LITERAL,
    BOOLEAN_LITERAL,
    IDENTIFIER,
    LINE_STR_REF,
    LINE_STR_TEXT,
    LINE_STR_ESCAPED_CHAR;

    companion object {

        operator fun invoke(type: String, line: Int): AlTokenType {
            return when (type) {
                "MULT" -> MULT
                "MOD" -> MOD
                "DIV" -> DIV
                "ADD" -> ADD
                "SUB" -> SUB
                "EXCL_WS" -> EXCL_WS
                "EXCL_NO_WS" -> EXCL_NO_WS
                "AT_NO_WS" -> AT_NO_WS
                "AT_PRE_WS" -> AT_PRE_WS
                "LANGLE" -> LANGLE
                "RANGLE" -> RANGLE
                "LE" -> LE
                "GE" -> GE
                "FILE" -> FILE
                "FIELD" -> FIELD
                "PROPERTY" -> PROPERTY
                "GET" -> GET
                "SET" -> SET
                "RECEIVER" -> RECEIVER
                "PARAM" -> PARAM
                "SETPARAM" -> SETPARAM
                "DELEGATE" -> DELEGATE
                "PACKAGE" -> PACKAGE
                "IMPORT" -> IMPORT
                "CLASS" -> CLASS
                "FUN" -> FUN
                "VAL" -> VAL
                "CONSTRUCTOR" -> CONSTRUCTOR
                "BY" -> BY
                "COMPANION" -> COMPANION
                "INIT" -> INIT
                "THIS" -> THIS
                "SUPER" -> SUPER
                "WHERE" -> WHERE
                "ELSE" -> ELSE
                "CATCH" -> CATCH
                "FINALLY" -> FINALLY
                "RETURN" -> RETURN
                "CONTINUE" -> CONTINUE
                "BREAK" -> BREAK
                "IN" -> IN
                "NOT_IN" -> NOT_IN
                "OUT" -> OUT
                "DYNAIC" -> DYNAMIC
                "PUBLIC" -> PUBLIC
                "PRIVATE" -> PRIVATE
                "PROTECTED" -> PROTECTED
                "INTERNAL" -> INTERNAL
                "ENUM" -> ENUM
                "SEALED" -> SEALED
                "ANNOTATION" -> ANNOTATION
                "DATA" -> DATA
                "INNER" -> INNER
                "TAILREC" -> TAILREC
                "OPERATOR" -> OPERATOR
                "INLINE" -> INLINE
                "INFIX" -> INFIX
                "EXTERNAL" -> EXTERNAL
                "SUSPEND" -> SUSPEND
                "OVERRIDE" -> OVERRIDE
                "ABSTRACT" -> ABSTRACT
                "FINAL" -> FINAL
                "OPEN" -> OPEN
                "CONST" -> CONST
                "LATEINIT" -> LATEINIT
                "VARARG" -> VARARG
                "NOINLINE" -> NOINLINE
                "CROSSINLINE" -> CROSSINLINE
                "REIFIED" -> REIFIED
                "EXPECT" -> EXPECT
                "ACTUAL" -> ACTUAL
                "IntegerLiteral" -> INTEGER_LITERAL
                "HexLiteral" -> HEX_LITERAL
                "BinLiteral" -> BIN_LITERAL
                "BooleanLiteral" -> BOOLEAN_LITERAL
                "Identifier" -> IDENTIFIER
                "LineStrRef" -> LINE_STR_REF
                "LineStrText" -> LINE_STR_TEXT
                "LineStrEscapedChar" -> LINE_STR_ESCAPED_CHAR
                else -> throw LineException("lexer token type \"$type\" not supported", line)
            }
        }

        fun isIgnored(type: String): Boolean {
            return type in listOf(
                "EOF",
                "NL",
                "DOT",
                "COMMA",
                "LPAREN",
                "RPAREN",
                "LSQUARE",
                "RSQUARE",
                "LCURL",
                "RCURL",
                "CONJ",
                "DISJ",
                "COLON",
                "SEMICOLON",
                "ASSIGNMENT",
                "ARROW",
                "RANGE",
                "IF",
                "WHEN",
                "FOR",
                "DO",
                "WHILE",
                "QUOTE_OPEN",
                "QUOTE_CLOSE",
                "LineStrExprStart"
            )
        }
    }
}