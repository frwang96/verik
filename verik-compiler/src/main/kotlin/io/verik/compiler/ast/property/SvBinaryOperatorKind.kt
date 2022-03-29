/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

/**
 * Enum for SystemVerilog binary operator kind.
 */
enum class SvBinaryOperatorKind {
    ASSIGN,
    ARROW_ASSIGN,
    MUL,
    DIV,
    PLUS,
    MINUS,
    AND,
    OR,
    XOR,
    LT,
    LTEQ,
    GT,
    GTEQ,
    EQEQ,
    EXCL_EQ,
    ANDAND,
    OROR,
    LTLT,
    GTGT,
    GTGTGT;

    fun serialize(): String {
        return when (this) {
            ASSIGN -> "="
            ARROW_ASSIGN -> "<="
            MUL -> "*"
            DIV -> "/"
            PLUS -> "+"
            MINUS -> "-"
            AND -> "&"
            OR -> "|"
            XOR -> "^"
            LT -> "<"
            LTEQ -> "<="
            GT -> ">"
            GTEQ -> ">="
            EQEQ -> "=="
            EXCL_EQ -> "!="
            ANDAND -> "&&"
            OROR -> "||"
            LTLT -> "<<"
            GTGT -> ">>"
            GTGTGT -> ">>>"
        }
    }
}
