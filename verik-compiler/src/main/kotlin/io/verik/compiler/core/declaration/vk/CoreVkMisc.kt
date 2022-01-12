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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EReplicationExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind
import io.verik.compiler.target.common.Target

object CoreVkMisc : CoreScope(CorePackage.VK) {

    val F_cat_Any_Any = object : TransformableCoreFunctionDeclaration(parent, "cat", "fun cat(Any, vararg Any)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            val typeAdapters = callExpression.valueArguments.map { TypeAdapter.ofElement(it) }
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.CAT_OUT,
                    listOf(TypeAdapter.ofElement(callExpression, 0)) + typeAdapters
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return EConcatenationExpression(callExpression.location, callExpression.type, callExpression.valueArguments)
        }
    }

    val F_rep_Any = object : TransformableCoreFunctionDeclaration(parent, "rep", "fun rep(Any)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.REP_OUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0])
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return EReplicationExpression(
                callExpression.location,
                callExpression.type,
                callExpression.valueArguments[0],
                callExpression.typeArguments[0].asCardinalValue(callExpression)
            )
        }
    }

    val F_max_Int_Int = object : TransformableCoreFunctionDeclaration(parent, "max", "fun max(Int, vararg Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return when (callExpression.valueArguments.size) {
                0 -> Messages.INTERNAL_ERROR.on(callExpression, "Value arguments expected for max")
                1 -> callExpression.valueArguments[0]
                else -> {
                    callExpression.valueArguments.reduce { accumulatedValueArgument, valueArgument ->
                        val binaryExpression = EKtBinaryExpression(
                            callExpression.location,
                            Core.Kt.C_Boolean.toType(),
                            accumulatedValueArgument,
                            valueArgument,
                            KtBinaryOperatorKind.GT
                        )
                        EInlineIfExpression(
                            callExpression.location,
                            callExpression.type.copy(),
                            binaryExpression,
                            ExpressionCopier.deepCopy(accumulatedValueArgument),
                            ExpressionCopier.deepCopy(valueArgument)
                        )
                    }
                }
            }
        }
    }

    val F_max_Ubit_Ubit = object : TransformableCoreFunctionDeclaration(parent, "max", "fun max(Ubit, vararg Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return callExpression.valueArguments.map {
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofElement(it, 0)
                )
            }
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_max_Int_Int.transform(callExpression)
        }
    }

    val F_max_Sbit_Sbit = object : TransformableCoreFunctionDeclaration(parent, "max", "fun max(Sbit, vararg Sbit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_max_Ubit_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_max_Int_Int.transform(callExpression)
        }
    }

    val F_min_Int_Int = object : TransformableCoreFunctionDeclaration(parent, "min", "fun min(Int, vararg Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return when (callExpression.valueArguments.size) {
                0 -> Messages.INTERNAL_ERROR.on(callExpression, "Value arguments expected for min")
                1 -> callExpression.valueArguments[0]
                else -> {
                    callExpression.valueArguments.reduce { accumulatedValueArgument, valueArgument ->
                        val binaryExpression = EKtBinaryExpression(
                            callExpression.location,
                            Core.Kt.C_Boolean.toType(),
                            accumulatedValueArgument,
                            valueArgument,
                            KtBinaryOperatorKind.LT
                        )
                        EInlineIfExpression(
                            callExpression.location,
                            callExpression.type.copy(),
                            binaryExpression,
                            ExpressionCopier.deepCopy(accumulatedValueArgument),
                            ExpressionCopier.deepCopy(valueArgument)
                        )
                    }
                }
            }
        }
    }

    val F_min_Ubit_Ubit = object : TransformableCoreFunctionDeclaration(parent, "min", "fun min(Ubit, vararg Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_max_Ubit_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_min_Int_Int.transform(callExpression)
        }
    }

    val F_min_Sbit_Sbit = object : TransformableCoreFunctionDeclaration(parent, "min", "fun min(Sbit, vararg Sbit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_max_Ubit_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_min_Int_Int.transform(callExpression)
        }
    }

    val F_log_Int = BasicCoreFunctionDeclaration(parent, "log", "fun log(Int)", Target.F_clog2)

    val F_exp_Int = object : TransformableCoreFunctionDeclaration(parent, "exp", "fun exp(Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val constantExpression = EConstantExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                "1"
            )
            return ECallExpression(
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
