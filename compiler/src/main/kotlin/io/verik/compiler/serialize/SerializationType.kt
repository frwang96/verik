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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.common.*
import io.verik.compiler.ast.element.sv.*

enum class SerializationType {
    EXPRESSION,
    STATEMENT,
    OTHER;

    companion object {

        fun getType(element: EElement): SerializationType {
            return when (element) {
                is EParenthesizedExpression -> EXPRESSION
                is ESvUnaryExpression -> EXPRESSION
                is ESvBinaryExpression -> EXPRESSION
                is EReferenceExpression -> EXPRESSION
                is ECallExpression -> EXPRESSION
                is EDotQualifiedExpression -> EXPRESSION
                is EConstantExpression -> EXPRESSION
                is EStringExpression -> EXPRESSION
                is EEventExpression -> EXPRESSION
                is EEventControlExpression -> EXPRESSION
                is EDelayExpression -> EXPRESSION

                is ESvBlockExpression -> STATEMENT
                is EIfExpression -> STATEMENT
                is ELoopStatement -> STATEMENT

                else -> OTHER
            }
        }
    }
}