/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

enum class SvUnaryOperatorKind {
    LOGICAL_NEG,
    BITWISE_NEG,
    MINUS,
    PRE_INC,
    PRE_DEC,
    POST_INC,
    POST_DEC,
    AND,
    OR,
    XOR,
    DELAY,
    TRIGGER;

    fun isStatement(): Boolean {
        return this in listOf(
            PRE_INC,
            PRE_DEC,
            POST_INC,
            POST_DEC,
            DELAY,
            TRIGGER
        )
    }

    fun serializePrefix(): String? {
        return when (this) {
            LOGICAL_NEG -> "!"
            BITWISE_NEG -> "~"
            MINUS -> "-"
            PRE_INC -> "++"
            PRE_DEC -> "--"
            AND -> "&"
            OR -> "|"
            XOR -> "^"
            DELAY -> "#"
            TRIGGER -> "->"
            else -> null
        }
    }

    fun serializePostfix(): String? {
        return when (this) {
            POST_INC -> "++"
            POST_DEC -> "--"
            else -> null
        }
    }
}
