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
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration
import io.verik.compiler.resolve.ComparisonTypeConstraint
import io.verik.compiler.resolve.ComparisonTypeConstraintKind
import io.verik.compiler.resolve.EqualsTypeConstraint
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint

object CoreVkUbit : CoreScope(Core.Vk.C_Ubit) {

    val F_unaryMinus = object : UnaryCoreFunctionDeclaration(
        parent,
        "unaryMinus",
        "fun unaryMinus()",
        SvUnaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }
    }

    val F_get_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvArrayAccessExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_set_Int_Boolean = object : TransformableCoreFunctionDeclaration(
        parent,
        "set",
        "fun set(Int, Boolean)"
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val receiver = ESvArrayAccessExpression(
                callExpression.location,
                callExpression.valueArguments[1].type.copy(),
                callExpression.receiver!!,
                callExpression.valueArguments[0]
            )
            return EKtBinaryExpression(
                callExpression.location,
                callExpression.type,
                receiver,
                callExpression.valueArguments[1],
                KtBinaryOperatorKind.EQ
            )
        }
    }

    val F_set_Int_Ubit = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Int, Ubit)") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.valueArguments[1].type.asBitWidth(callExpression)
            val msbIndex = EKtCallExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Kt.Int.F_plus_Int,
                ExpressionCopier.copy(callExpression.valueArguments[0]),
                arrayListOf(EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "${width - 1}")),
                arrayListOf()
            )
            val receiver = EConstantPartSelectExpression(
                callExpression.location,
                callExpression.valueArguments[1].type.copy(),
                callExpression.receiver!!,
                msbIndex,
                callExpression.valueArguments[0]
            )
            return EKtBinaryExpression(
                callExpression.location,
                callExpression.type,
                receiver,
                callExpression.valueArguments[1],
                KtBinaryOperatorKind.EQ
            )
        }
    }

    val F_invert = object : UnaryCoreFunctionDeclaration(
        parent,
        "invert",
        "fun invert()",
        SvUnaryOperatorKind.BITWISE_NEG
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }
    }

    val F_reverse = object : TransformableCoreFunctionDeclaration(parent, "reverse", "fun reverse()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return EStreamingExpression(callExpression.location, callExpression.type, callExpression.receiver!!)
        }
    }

    val F_slice_Int = object : TransformableCoreFunctionDeclaration(parent, "slice", "fun slice(Int)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val msbIndex = EKtCallExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Kt.Int.F_plus_Int,
                ExpressionCopier.copy(callExpression.valueArguments[0]),
                arrayListOf(EConstantExpression(callExpression.location, Core.Kt.C_Int.toType(), "${value - 1}")),
                arrayListOf()
            )
            return EConstantPartSelectExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                msbIndex,
                callExpression.valueArguments[0]
            )
        }
    }

    val F_ext = object : TransformableCoreFunctionDeclaration(parent, "ext", "fun ext()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                ComparisonTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    ComparisonTypeConstraintKind.EXT
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                value
            )
        }
    }

    val F_sext = object : TransformableCoreFunctionDeclaration(parent, "sext", "fun sext()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_ext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = CoreTransformUtil.callExpressionSigned(callExpression.receiver!!)
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpressionSigned,
                value
            )
        }
    }

    val F_tru = object : TransformableCoreFunctionDeclaration(parent, "tru", "fun tru()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                ComparisonTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    ComparisonTypeConstraintKind.TRU
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                value
            )
        }
    }
}
