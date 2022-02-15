/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.ast.property

import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType

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
