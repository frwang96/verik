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
import io.verik.compiler.constant.BitComponent
import io.verik.compiler.constant.BitConstant
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

object CoreVkSpecial : CoreScope(CorePackage.VK) {

    val F_imported = object : BasicCoreFunctionDeclaration(parent, "imported", "fun imported()", null) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }
    }

    val F_sv_String = BasicCoreFunctionDeclaration(parent, "sv", "fun sv(String)", null)

    val F_nc = object : TransformableCoreFunctionDeclaration(parent, "nc", "fun nc()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_imported.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return callExpression
        }
    }

    val F_b = object : TransformableCoreFunctionDeclaration(parent, "b", "fun b()") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            if (value !in 0..1)
                Messages.CARDINAL_NOT_BOOLEAN.on(callExpression, callExpression.typeArguments[0])
            return ConstantBuilder.buildBoolean(callExpression, value != 0)
        }
    }

    val F_i = object : TransformableCoreFunctionDeclaration(parent, "i", "fun i()") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return ConstantBuilder.buildInt(callExpression, value)
        }
    }

    val F_u = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.WIDTH_OUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val width = callExpression.type.asBitWidth(callExpression)
            return ConstantBuilder.buildBitConstant(callExpression.location, width, value)
        }
    }

    val F_u_Boolean = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u(Boolean)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_OUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofConstant(Cardinal.of(1).toType())
                )
            )
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
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0)
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
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            return ConstantBuilder.buildBitConstant(callExpression.location, width, 0)
        }
    }

    val F_u1 = object : TransformableCoreFunctionDeclaration(parent, "u1", "fun u1()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.zeroes(width), BitComponent.ones(width), false, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_ux = object : TransformableCoreFunctionDeclaration(parent, "ux", "fun ux()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.ones(width), BitComponent.zeroes(width), false, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_uz = object : TransformableCoreFunctionDeclaration(parent, "uz", "fun uz()") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.ones(width), BitComponent.ones(width), false, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_s_Int = BasicCoreFunctionDeclaration(parent, "s", "fun s(Int)", null)

    val F_s_String = BasicCoreFunctionDeclaration(parent, "s", "fun s(String)", null)

    val F_s_Ubit = object : TransformableCoreFunctionDeclaration(parent, "s", "fun s(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_u_Sbit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return CoreTransformUtil.callExpressionSigned(callExpression.valueArguments[0])
        }
    }
}
