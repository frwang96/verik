/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType

/**
 * Enum for Kotlin unary operator kind.
 */
enum class KtUnaryOperatorKind {
    EXCL,
    EXCL_EXCL,
    PLUS,
    MINUS,
    PRE_INC,
    PRE_DEC,
    POST_INC,
    POST_DEC;

    fun isReducible(): Boolean {
        return this !in listOf(PRE_INC, PRE_DEC, POST_INC, POST_DEC)
    }

    companion object {

        fun getKindPrefix(token: IElementType, location: SourceLocation): KtUnaryOperatorKind {
            return when (token.toString()) {
                "EXCL" -> EXCL
                "PLUS" -> PLUS
                "MINUS" -> MINUS
                "PLUSPLUS" -> PRE_INC
                "MINUSMINUS" -> PRE_DEC
                else -> Messages.INTERNAL_ERROR.on(location, "Unrecognised unary operator kind: $token")
            }
        }

        fun getKindPostfix(token: IElementType, location: SourceLocation): KtUnaryOperatorKind {
            return when (token.toString()) {
                "EXCLEXCL" -> EXCL_EXCL
                "PLUSPLUS" -> POST_INC
                "MINUSMINUS" -> POST_DEC
                else -> Messages.INTERNAL_ERROR.on(location, "Unrecognised unary operator kind: $token")
            }
        }
    }
}
