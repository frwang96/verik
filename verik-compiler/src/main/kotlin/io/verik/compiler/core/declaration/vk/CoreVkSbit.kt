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
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeConstraint

object CoreVkSbit : CoreScope(Core.Vk.C_Sbit) {

    val F_unaryPlus = object : TransformableCoreFunctionDeclaration(parent, "unaryPlus", "fun unaryPlus()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbit.F_unaryPlus.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkUbit.F_unaryPlus.transform(callExpression)
        }
    }

    val F_unaryMinus = object : UnaryCoreFunctionDeclaration(
        parent,
        "unaryMinus",
        "fun unaryMinus()",
        SvUnaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_unaryPlus.getTypeConstraints(callExpression)
        }
    }

    val F_get_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkUbit.F_get_Int.transform(callExpression)
        }
    }

    val F_get_Ubit = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbit.F_get_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_get_Int.transform(callExpression)
        }
    }

    val F_set_Int_Boolean = object : TransformableCoreFunctionDeclaration(
        parent,
        "set",
        "fun set(Int, Boolean)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkUbit.F_set_Int_Boolean.transform(callExpression)
        }
    }

    val F_set_Ubit_Boolean = object : TransformableCoreFunctionDeclaration(
        parent,
        "set",
        "fun set(Ubit, Boolean)"
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_get_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_set_Int_Boolean.transform(callExpression)
        }
    }

    val F_not = UnaryCoreFunctionDeclaration(
        parent,
        "not",
        "fun not()",
        SvUnaryOperatorKind.LOGICAL_NEG
    )

    val F_ext = object : TransformableCoreFunctionDeclaration(parent, "ext", "fun ext()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbit.F_ext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkUbit.F_ext.transform(callExpression)
        }
    }

    val F_uext = object : TransformableCoreFunctionDeclaration(parent, "uext", "fun uext()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_ext.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            val callExpressionUnsigned = CoreTransformUtil.callExpressionUnsigned(callExpression.receiver!!)
            val width = callExpression.typeArguments[0].asCardinalValue(callExpression)
            val widthCastExpression = EWidthCastExpression(
                callExpression.location,
                callExpression.type,
                callExpressionUnsigned,
                width
            )
            return CoreTransformUtil.callExpressionSigned(widthCastExpression)
        }
    }

    val F_tru = object : TransformableCoreFunctionDeclaration(parent, "tru", "fun tru()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbit.F_tru.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_ext.transform(callExpression)
        }
    }

    val F_toUbit = object : TransformableCoreFunctionDeclaration(parent, "toUbit", "fun toUbit()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbit.F_toSbit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreTransformUtil.callExpressionUnsigned(callExpression.receiver!!)
        }
    }
}
