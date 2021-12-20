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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EReplicationExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.resolve.SpecialTypeConstraint
import io.verik.compiler.resolve.SpecialTypeConstraintKind
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.target.common.Target

object CoreVkMisc : CoreScope(CorePackage.VK) {

    val F_cat_Any_Any = object : TransformableCoreFunctionDeclaration(parent, "cat", "fun cat(Any, vararg Any)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(SpecialTypeConstraint(callExpression, SpecialTypeConstraintKind.CAT))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EConcatenationExpression(callExpression.location, callExpression.type, callExpression.valueArguments)
        }
    }

    val F_rep_Any = object : TransformableCoreFunctionDeclaration(parent, "rep", "fun rep(Any)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(SpecialTypeConstraint(callExpression, SpecialTypeConstraintKind.REP))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EReplicationExpression(
                callExpression.location,
                callExpression.type,
                callExpression.valueArguments[0],
                callExpression.typeArguments[0].asCardinalValue(callExpression)
            )
        }
    }

    val F_log_Int = BasicCoreFunctionDeclaration(parent, "log", "fun log(Int)", Target.F_clog2)

    val F_exp_Int = object : TransformableCoreFunctionDeclaration(parent, "exp", "fun exp(Int)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val constantExpression = EConstantExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                "1"
            )
            return EKtCallExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Kt.Int.F_shl_Int,
                constantExpression,
                callExpression.valueArguments,
                ArrayList()
            )
        }
    }
}
