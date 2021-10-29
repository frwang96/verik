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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

object CoreVkSbit : CoreScope(Core.Vk.C_Sbit) {

    val F_lt_Sbit = object : TransformableCoreFunctionDeclaration(parent, "lt", null) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val left = callExpression.receiver!!
            val right = callExpression.valueArguments[0]
            val leftSigned = EKtCallExpression(
                left.location,
                left.type.copy(),
                Target.F_signed,
                null,
                arrayListOf(left),
                arrayListOf()
            )
            val rightSigned = EKtCallExpression(
                right.location,
                right.type.copy(),
                Target.F_signed,
                null,
                arrayListOf(right),
                arrayListOf()
            )
            return ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                leftSigned,
                rightSigned,
                SvBinaryOperatorKind.LT
            )
        }
    }
}
