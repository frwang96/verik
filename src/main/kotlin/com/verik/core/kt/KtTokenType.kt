package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

enum class KtTokenType {
    EOF,
    NL,
    DOT,
    COMMA,
    LPAREN,
    RPAREN,
    LSQUARE,
    RSQUARE,
    LCURL,
    RCURL,
    MULT,
    ADD,
    SUB,
    INCR,
    DECR,
    COLON,
    SEMICOLON,
    ASSIGNMENT,
    PACKAGE,
    IMPORT,
    CLASS,
    FUN,
    VAL,
    VAR,
    IF,
    ELSE,
    WHEN,
    FOR,
    RETURN,
    CONST,
    INTEGER_LITERAL,
    IDENTIFIER,
    QUOTE_OPEN,
    QUOTE_CLOSE,
    LINE_STR_TEXT;

    companion object {
        fun getType(type: String): KtTokenType? {
            return when (type) {
                "EOF" -> EOF
                "NL" -> NL
                "DOT" -> DOT
                "COMMA" -> COMMA
                "LPAREN" -> LPAREN
                "RPAREN" -> RPAREN
                "LSQUARE" -> LSQUARE
                "RSQUARE" -> RSQUARE
                "LCURL" -> LCURL
                "RCURL" -> RCURL
                "MULT" -> MULT
                "ADD" -> ADD
                "SUB" -> SUB
                "INCR" -> INCR
                "DECR" -> DECR
                "COLON" -> COLON
                "SEMICOLON" -> SEMICOLON
                "ASSIGNMENT" -> ASSIGNMENT
                "PACKAGE" -> PACKAGE
                "IMPORT" -> IMPORT
                "CLASS" -> CLASS
                "FUN" -> FUN
                "VAL" -> VAL
                "VAR" -> VAR
                "IF" -> IF
                "ELSE" -> ELSE
                "WHEN" -> WHEN
                "FOR" -> FOR
                "RETURN" -> RETURN
                "CONST" -> CONST
                "IntegerLiteral" -> INTEGER_LITERAL
                "Identifier" -> IDENTIFIER
                "QUOTE_OPEN" -> QUOTE_OPEN
                "QUOTE_CLOSE" -> QUOTE_CLOSE
                "LineStrText" -> LINE_STR_TEXT
                else -> null
            }
        }
    }
}