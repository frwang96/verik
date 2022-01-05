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
import io.verik.compiler.constant.BitConstant
import io.verik.compiler.constant.ConstantFormatter
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.resolve.EqualsTypeConstraint
import io.verik.compiler.resolve.SpecialTypeConstraint
import io.verik.compiler.resolve.SpecialTypeConstraintKind
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.UnaryTypeConstraint
import io.verik.compiler.resolve.UnaryTypeConstraintKind

object CoreVkSpecial : CoreScope(CorePackage.VK) {

    val F_imported = object : BasicCoreFunctionDeclaration(parent, "imported", "fun imported()", null) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }
    }

    val F_sv_String = BasicCoreFunctionDeclaration(parent, "sv", "fun sv(String)", null)

    val F_nc = object : TransformableCoreFunctionDeclaration(parent, "nc", "fun nc()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
        }
    }

    val F_i = object : TransformableCoreFunctionDeclaration(parent, "i", "fun i()") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                value.toString()
            )
        }
    }

    val F_u = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                UnaryTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    true,
                    UnaryTypeConstraintKind.WIDTH
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(value, false, width)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                ConstantFormatter.formatBitConstant(bitConstant)
            )
        }
    }

    val F_u_Boolean = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u(Boolean)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(SpecialTypeConstraint(callExpression, SpecialTypeConstraintKind.CONSTANT_ONE))
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return callExpression.valueArguments[0]
        }
    }

    val F_u_Int = BasicCoreFunctionDeclaration(parent, "u", "fun u(Int)", null)

    val F_u_String = BasicCoreFunctionDeclaration(parent, "u", "fun u(String)", null)

    val F_u_Sbit = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u(Sbit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return CoreTransformUtil.callExpressionUnsigned(callExpression.valueArguments[0])
        }
    }

    val F_u0 = object : TransformableCoreFunctionDeclaration(parent, "u0", "fun u0()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(0, false, width)
            return EConstantExpression(
                callExpression.location,
                callExpression.type,
                ConstantFormatter.formatBitConstant(bitConstant)
            )
        }
    }

    val F_s_Int = BasicCoreFunctionDeclaration(parent, "s", "fun s(Int)", null)

    val F_s_String = BasicCoreFunctionDeclaration(parent, "s", "fun s(String)", null)

    val F_s_Ubit = object : TransformableCoreFunctionDeclaration(parent, "s", "fun s(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return CoreTransformUtil.callExpressionSigned(callExpression.valueArguments[0])
        }
    }
}
