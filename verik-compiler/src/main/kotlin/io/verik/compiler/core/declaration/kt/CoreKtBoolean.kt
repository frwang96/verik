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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.constant.BooleanConstantEvaluator
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration

object CoreKtBoolean : CoreScope(Core.Kt.C_Boolean) {

    val F_not = object : UnaryCoreFunctionDeclaration(parent, "not", "fun not()", SvUnaryOperatorKind.LOGICAL_NEG) {

        override fun evaluate(callExpression: ECallExpression): EConstantExpression? {
            val expression = callExpression.receiver!!
            return if (expression is EConstantExpression) {
                BooleanConstantEvaluator.not(callExpression, expression)
            } else null
        }
    }

    val F_and_Boolean = BinaryCoreFunctionDeclaration(parent, "and", "fun and(Boolean)", SvBinaryOperatorKind.ANDAND)

    val F_or_Boolean = BinaryCoreFunctionDeclaration(parent, "or", "fun or(Boolean)", SvBinaryOperatorKind.OROR)

    val F_xor_Boolean = BinaryCoreFunctionDeclaration(parent, "xor", "fun xor(Boolean)", SvBinaryOperatorKind.XOR)
}
