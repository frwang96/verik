/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.lexer.KtSingleValueToken

enum class KtBinaryOperatorKind {
    EQ,
    MUL,
    DIV,
    PLUS,
    MINUS,
    RANGE,
    LT,
    LTEQ,
    GT,
    GTEQ,
    EQEQ,
    EXCL_EQ,
    PLUS_EQ,
    MINUS_EQ,
    DIV_EQ,
    ANDAND,
    OROR;

    fun isReducible(): Boolean {
        return this !in listOf(EQ, LT, LTEQ, GT, GTEQ, EQEQ, EXCL_EQ, ANDAND, OROR)
    }

    companion object {

        operator fun invoke(token: KtSingleValueToken, location: SourceLocation): KtBinaryOperatorKind {
            return when (token.toString()) {
                "EQ" -> EQ
                "MUL" -> MUL
                "DIV" -> DIV
                "PLUS" -> PLUS
                "MINUS" -> MINUS
                "RANGE" -> RANGE
                "LT" -> LT
                "LTEQ" -> LTEQ
                "GT" -> GT
                "GTEQ" -> GTEQ
                "EQEQ" -> EQEQ
                "EXCLEQ" -> EXCL_EQ
                "PLUSEQ" -> PLUS_EQ
                "MINUSEQ" -> MINUS_EQ
                "DIVEQ" -> DIV_EQ
                "ANDAND" -> ANDAND
                "OROR" -> OROR
                else -> Messages.INTERNAL_ERROR.on(location, "Unrecognised binary operator kind: $token")
            }
        }
    }
}
