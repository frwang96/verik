package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

enum class KtTokenType {
    EOF,
    NL,
    DOT,                // .
    COMMA,              // ,
    LPAREN,             // (
    RPAREN,             // )
    LSQUARE,            // [
    RSQUARE,            // ]
    LCURL,              // {
    RCURL,              // }
    MULT,               // *
    ADD,                // +
    SUB,                // -
    INCR,               // ++
    DECR,               // --
    CONJ,               // &&
    DISJ,               // ||
    EXCL_WS,            // !
    EXCL_NO_WS,         // !
    COLON,              // :
    SEMICOLON,          // ;
    ASSIGNMENT,         // =
    ARROW,              // ->
    RANGE,              // ..
    AT_NO_WS,           // @
    AT_PRE_WS,          // @
    AT_POST_WS,         // @
    QUEST_NO_WS,        // ?
    LANGLE,             // <
    RANGLE,             // >
    LE,                 // <=
    GE,                 // >=
    EXCL_EQ,            // !=
    EQEQ,               // ==
    PACKAGE,            // package
    IMPORT,             // import
    CLASS,              // class
    FUN,                // fun
    OBJECT,             // object
    VAL,                // val
    CONSTRUCTOR,        // constructor
    COMPANION,          // companion
    THIS,               // this
    IF,                 // if
    ELSE,               // else
    WHEN,               // when
    FOR,                // for
    DO,                 // do
    WHILE,              // while
    RETURN,             // return
    IN,                 // in
    ENUM,               // enum
    INNER,              // inner
    OPERATOR,           // operator
    OVERRIDE,           // override
    ABSTRACT,           // abstract
    OPEN,               // open
    CONST,              // const
    INTEGER_LITERAL,
    BOOLEAN_LITERAL,    // true | false
    NULL_LITERAL,       // null
    IDENTIFIER,
    QUOTE_OPEN,         // "
    QUOTE_CLOSE,        // "
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
                "CONJ" -> CONJ
                "DISJ" -> DISJ
                "EXCL_WS" -> EXCL_WS
                "EXCL_NO_WS" -> EXCL_NO_WS
                "COLON" -> COLON
                "SEMICOLON" -> SEMICOLON
                "ASSIGNMENT" -> ASSIGNMENT
                "ARROW" -> ARROW
                "RANGE" -> RANGE
                "AT_NO_WS" -> AT_NO_WS
                "AT_PRE_WS" -> AT_PRE_WS
                "AT_POST_WS" -> AT_POST_WS
                "QUEST_NO_WS" -> QUEST_NO_WS
                "LANGLE" -> LANGLE
                "RANGLE" -> RANGLE
                "LE" -> LE
                "GE" -> GE
                "EXCL_EQ" -> EXCL_EQ
                "EQEQ" -> EQEQ
                "PACKAGE" -> PACKAGE
                "IMPORT" -> IMPORT
                "CLASS" -> CLASS
                "FUN" -> FUN
                "OBJECT" -> OBJECT
                "VAL" -> VAL
                "CONSTRUCTOR" -> CONSTRUCTOR
                "COMPANION" -> COMPANION
                "THIS" -> THIS
                "IF" -> IF
                "ELSE" -> ELSE
                "WHEN" -> WHEN
                "FOR" -> FOR
                "DO" -> DO
                "WHILE" -> WHILE
                "RETURN" -> RETURN
                "IN" -> IN
                "ENUM" -> ENUM
                "INNER" -> INNER
                "OPERATOR" -> OPERATOR
                "OVERRIDE" -> OVERRIDE
                "ABSTRACT" -> ABSTRACT
                "OPEN" -> OPEN
                "CONST" -> CONST
                "IntegerLiteral" -> INTEGER_LITERAL
                "BooleanLiteral" -> BOOLEAN_LITERAL
                "nullLiteral" -> NULL_LITERAL
                "Identifier" -> IDENTIFIER
                "QUOTE_OPEN" -> QUOTE_OPEN
                "QUOTE_CLOSE" -> QUOTE_CLOSE
                "LineStrText" -> LINE_STR_TEXT
                else -> null
            }
        }
    }
}