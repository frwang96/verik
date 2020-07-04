package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

enum class KtTokenType {
    MULT,
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
    EXCL_EQ,
    EQEQ,
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
    INTERFACE,
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
    NULL_LITERAL,
    IDENTIFIER,
    LINE_STR_REF,
    LINE_STR_TEXT,
    LINE_STR_EXCAPED_CHAR;

    fun isSoftKeyword(): Boolean {
        return this in listOf(
            ABSTRACT,
            ANNOTATION,
            BY,
            CATCH,
            COMPANION,
            CONSTRUCTOR,
            CROSSINLINE,
            DATA,
            DYNAMIC,
            ENUM,
            EXTERNAL,
            FINAL,
            FINALLY,
            GET,
            IMPORT,
            INFIX,
            INIT,
            INLINE,
            INNER,
            INTERNAL,
            LATEINIT,
            NOINLINE,
            OPEN,
            OPERATOR,
            OUT,
            OVERRIDE,
            PRIVATE,
            PROTECTED,
            PUBLIC,
            REIFIED,
            SEALED,
            TAILREC,
            SET,
            VARARG,
            WHERE,
            FIELD,
            PROPERTY,
            RECEIVER,
            PARAM,
            SETPARAM,
            DELEGATE,
            FILE,
            EXPECT,
            ACTUAL,
            CONST,
            SUSPEND
        )
    }

    companion object {

        operator fun invoke(type: String, exception: Exception): KtTokenType {
            return when (type) {
                "MULT" -> MULT
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
                "EXCL_EQ" -> EXCL_EQ
                "EQEQ" -> EQEQ
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
                "INTERFACE" -> INTERFACE
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
                "nullLiteral" -> NULL_LITERAL
                "Identifier" -> IDENTIFIER
                "LineStrRef" -> LINE_STR_REF
                "LineStrText" -> LINE_STR_TEXT
                "LineStrExcapedChar" -> LINE_STR_EXCAPED_CHAR
                else -> throw exception
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
                "IN",
                "QUOTE_OPEN",
                "QUOTE_CLOSE",
                "LineStrExprStart"
            )
        }
    }
}