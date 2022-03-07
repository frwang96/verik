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

import io.verik.compiler.ast.element.declaration.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.element.expression.sv.EScopeExpression
import io.verik.compiler.constant.BitComponent
import io.verik.compiler.constant.BitConstant
import io.verik.compiler.constant.BooleanConstantKind
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CorePropertyDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

object CoreVkSpecial : CoreScope(CorePackage.VK) {

    val F_imp = object : TransformableCoreFunctionDeclaration(parent, "imp", "fun imp()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            if (!callExpression.isImported()) {
                Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            }
            return ENothingExpression(callExpression.location)
        }
    }

    val F_inj_String = BasicCoreFunctionDeclaration(parent, "inj", "fun inj(String)", null)

    val F_inji_String = object : BasicCoreFunctionDeclaration(parent, "inji", "fun inji(String)", null) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_imp.getTypeConstraints(callExpression)
        }
    }

    val F_t = object : TransformableCoreFunctionDeclaration(parent, "t", "fun t()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val parent = callExpression.parent
            if (parent !is EInjectedExpression) {
                Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            }
            return EScopeExpression(callExpression.location, callExpression.typeArguments[0], listOf())
        }
    }

    val F_nc = object : TransformableCoreFunctionDeclaration(parent, "nc", "fun nc()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_imp.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            if (callExpression.parent !is EAbstractComponentInstantiation) {
                Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            }
            return ENothingExpression(callExpression.location)
        }
    }

    val F_b = object : TransformableCoreFunctionDeclaration(parent, "b", "fun b()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return evaluate(callExpression)
        }

        override fun evaluate(callExpression: ECallExpression): EConstantExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            if (value !in 0..1)
                Messages.CARDINAL_NOT_BOOLEAN.on(callExpression, callExpression.typeArguments[0])
            return ConstantBuilder.buildBoolean(callExpression, BooleanConstantKind.fromBoolean(value != 0))
        }
    }

    val F_i = object : TransformableCoreFunctionDeclaration(parent, "i", "fun i()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return evaluate(callExpression)
        }

        override fun evaluate(callExpression: ECallExpression): EConstantExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            return ConstantBuilder.buildInt(callExpression, value)
        }
    }

    val F_u = object : TransformableCoreFunctionDeclaration(parent, "u", "fun u()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.WIDTH_OUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val value = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val width = callExpression.type.asBitWidth(callExpression)
            return ConstantBuilder.buildBitConstant(callExpression.location, width, value)
        }
    }

    val F_u_Int = BasicCoreFunctionDeclaration(parent, "u", "fun u(Int)", null)

    val F_u_String = BasicCoreFunctionDeclaration(parent, "u", "fun u(String)", null)

    val F_u0 = object : TransformableCoreFunctionDeclaration(parent, "u0", "fun u0()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            return ConstantBuilder.buildBitConstant(callExpression.location, width, 0)
        }
    }

    val F_u1 = object : TransformableCoreFunctionDeclaration(parent, "u1", "fun u1()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.zeroes(width), BitComponent.ones(width), false, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_ux = object : TransformableCoreFunctionDeclaration(parent, "ux", "fun ux()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.ones(width), BitComponent.zeroes(width), false, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_uz = object : TransformableCoreFunctionDeclaration(parent, "uz", "fun uz()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.ones(width), BitComponent.ones(width), false, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_s_Int = BasicCoreFunctionDeclaration(parent, "s", "fun s(Int)", null)

    val F_s_String = BasicCoreFunctionDeclaration(parent, "s", "fun s(String)", null)

    val F_s0 = object : TransformableCoreFunctionDeclaration(parent, "s0", "fun s0()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.zeroes(width), BitComponent.zeroes(width), true, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_s1 = object : TransformableCoreFunctionDeclaration(parent, "s1", "fun s1()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_u0.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val width = callExpression.type.asBitWidth(callExpression)
            val bitConstant = BitConstant(BitComponent.zeroes(width), BitComponent.ones(width), true, width)
            return ConstantBuilder.buildBitConstant(callExpression.location, bitConstant)
        }
    }

    val F_c = object : TransformableCoreFunctionDeclaration(parent, "c", "fun c(vararg Boolean)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cp_Any = object : TransformableCoreFunctionDeclaration(parent, "cp", "fun cp(Any)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cp_Any_Function = object : TransformableCoreFunctionDeclaration(parent, "cp", "fun cp(Any, Function)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cc_Any = object : TransformableCoreFunctionDeclaration(parent, "cc", "fun cc(vararg CoverPoint)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val P_unknown = object : CorePropertyDeclaration(parent, "unknown") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            return ConstantBuilder.buildBoolean(referenceExpression, BooleanConstantKind.UNKNOWN)
        }
    }

    val P_floating = object : CorePropertyDeclaration(parent, "floating") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            return ConstantBuilder.buildBoolean(referenceExpression, BooleanConstantKind.FLOATING)
        }
    }
}
