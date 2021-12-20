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
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeConstraint

object CoreVkSbit : CoreScope(Core.Vk.C_Sbit) {

    val F_unaryPlus = object : TransformableCoreFunctionDeclaration(parent, "unaryPlus", "fun unaryPlus()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return CoreVkUbit.F_unaryPlus.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return CoreVkUbit.F_unaryPlus.transform(callExpression)
        }
    }

    val F_unaryMinus = object : UnaryCoreFunctionDeclaration(
        parent,
        "unaryMinus",
        "fun unaryMinus()",
        SvUnaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_unaryPlus.getTypeConstraints(callExpression)
        }
    }
}
