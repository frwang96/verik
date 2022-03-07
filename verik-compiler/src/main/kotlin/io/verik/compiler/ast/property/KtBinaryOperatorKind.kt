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
import org.jetbrains.kotlin.lexer.KtSingleValueToken

enum class KtBinaryOperatorKind {
    EQ,
    MUL,
    DIV,
    PLUS,
    MINUS,
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
