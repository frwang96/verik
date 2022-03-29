/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.sv.EConcatenationExpression
import io.verik.compiler.ast.element.expression.sv.EInlineIfExpression
import io.verik.compiler.ast.element.expression.sv.EReplicationExpression
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

    val F_cat_Any = object : TransformableCoreFunctionDeclaration(parent, "cat", "fun cat(vararg Any)") {

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
            if (callExpression.valueArguments.isEmpty()) {
                Messages.CALL_EXPRESSION_INSUFFICIENT_ARGUMENTS.on(callExpression, name)
            }
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

    val F_max_Int = object : TransformableCoreFunctionDeclaration(parent, "max", "fun max(vararg Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            if (callExpression.valueArguments.size < 2) {
                Messages.CALL_EXPRESSION_INSUFFICIENT_ARGUMENTS.on(callExpression, name)
                return ENothingExpression(callExpression.location)
            }
            return callExpression.valueArguments.reduce { accumulatedValueArgument, valueArgument ->
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

    val F_max_Ubit = object : TransformableCoreFunctionDeclaration(parent, "max", "fun max(vararg Ubit)") {

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
            return F_max_Int.transform(callExpression)
        }
    }

    val F_max_Sbit = object : TransformableCoreFunctionDeclaration(parent, "max", "fun max(vararg Sbit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_max_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_max_Int.transform(callExpression)
        }
    }

    val F_min_Int = object : TransformableCoreFunctionDeclaration(parent, "min", "fun min(vararg Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            if (callExpression.valueArguments.size < 2) {
                Messages.CALL_EXPRESSION_INSUFFICIENT_ARGUMENTS.on(callExpression, name)
                return ENothingExpression(callExpression.location)
            }
            return callExpression.valueArguments.reduce { accumulatedValueArgument, valueArgument ->
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

    val F_min_Ubit = object : TransformableCoreFunctionDeclaration(parent, "min", "fun min(vararg Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_max_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_min_Int.transform(callExpression)
        }
    }

    val F_min_Sbit = object : TransformableCoreFunctionDeclaration(parent, "min", "fun min(vararg Sbit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_max_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_min_Int.transform(callExpression)
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
                false,
                callExpression.valueArguments,
                ArrayList()
            )
        }
    }
}
