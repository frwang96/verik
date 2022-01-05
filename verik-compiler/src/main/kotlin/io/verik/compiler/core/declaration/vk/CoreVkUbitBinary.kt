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
import io.verik.compiler.constant.ConstantFormatter
import io.verik.compiler.constant.ConstantParser
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreTransformUtil
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.resolve.BinaryTypeConstraint
import io.verik.compiler.resolve.BinaryTypeConstraintKind
import io.verik.compiler.resolve.EqualsTypeConstraint
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint

object CoreVkUbitBinary : CoreScope(Core.Vk.C_Ubit) {

    val F_plus_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Ubit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryTypeConstraintKind.MAX
                )
            )
        }

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantParser.getBitConstant(callExpression.receiver!!)
            val right = ConstantParser.getBitConstant(callExpression.valueArguments[0])
            return if (left != null && right != null) {
                ConstantFormatter.formatBitConstant(left.add(right, callExpression))
            } else null
        }
    }

    val F_plus_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Sbit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_plus_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_add_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "add",
        "fun add(Ubit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryTypeConstraintKind.MAX_INC
                )
            )
        }
    }

    val F_add_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "add",
        "fun add(Sbit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_add_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_minus_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "minus",
        "fun minus(Ubit)",
        SvBinaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryTypeConstraintKind.MAX
                )
            )
        }

        override fun evaluate(callExpression: EKtCallExpression): String? {
            val left = ConstantParser.getBitConstant(callExpression.receiver!!)
            val right = ConstantParser.getBitConstant(callExpression.valueArguments[0])
            return if (left != null && right != null) {
                ConstantFormatter.formatBitConstant(left.sub(right, callExpression))
            } else null
        }
    }

    val F_minus_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "minus",
        "fun minus(Sbit)",
        SvBinaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_minus_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_times_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "times",
        "fun times(Ubit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryTypeConstraintKind.MAX
                )
            )
        }
    }

    val F_times_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "times",
        "fun times(Sbit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_times_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_mul_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "mul",
        "fun mul(Ubit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                BinaryTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0),
                    BinaryTypeConstraintKind.ADD
                )
            )
        }
    }

    val F_mul_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "mul",
        "fun mul(Sbit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_mul_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_div_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "div",
        "fun div(Ubit)",
        SvBinaryOperatorKind.DIV
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

    val F_and_Ubit = object : BinaryCoreFunctionDeclaration(parent, "and", "fun and(Ubit)", SvBinaryOperatorKind.AND) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                ),
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.valueArguments[0], 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }
    }

    val F_and_Sbit = object : BinaryCoreFunctionDeclaration(parent, "and", "fun and(Sbit)", SvBinaryOperatorKind.AND) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_or_Ubit = object : BinaryCoreFunctionDeclaration(parent, "or", "fun or(Ubit)", SvBinaryOperatorKind.OR) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_or_Sbit = object : BinaryCoreFunctionDeclaration(parent, "or", "fun or(Sbit)", SvBinaryOperatorKind.OR) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_xor_Ubit = object : BinaryCoreFunctionDeclaration(parent, "xor", "fun xor(Ubit)", SvBinaryOperatorKind.XOR) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_xor_Sbit = object : BinaryCoreFunctionDeclaration(parent, "xor", "fun xor(Sbit)", SvBinaryOperatorKind.XOR) {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_shl_Int = object : TransformableCoreFunctionDeclaration(parent, "shl", "fun shl(Int)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return listOf(
                EqualsTypeConstraint(
                    TypeAdapter.ofElement(callExpression.receiver!!, 0),
                    TypeAdapter.ofElement(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.LTLT
            )
        }
    }

    val F_shl_Ubit = object : TransformableCoreFunctionDeclaration(parent, "shl", "fun shl(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_shl_Int.transform(callExpression)
        }
    }

    val F_shr_Int = object : TransformableCoreFunctionDeclaration(parent, "shr", "fun shr(Int)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.GTGT
            )
        }
    }

    val F_shr_Ubit = object : TransformableCoreFunctionDeclaration(parent, "shr", "fun shr(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_shr_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_shr_Int.transform(callExpression)
        }
    }

    val F_sshr_Int = object : TransformableCoreFunctionDeclaration(parent, "sshr", "fun sshr(Int)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val callExpressionSigned = CoreTransformUtil.callExpressionSigned(callExpression.receiver!!)
            val binaryExpression = ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpressionSigned,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.GTGTGT
            )
            return CoreTransformUtil.callExpressionUnsigned(binaryExpression)
        }
    }

    val F_sshr_Ubit = object : TransformableCoreFunctionDeclaration(parent, "sshr", "fun sshr(Ubit)") {

        override fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
            return F_sshr_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: EKtCallExpression): EExpression {
            return F_sshr_Int.transform(callExpression)
        }
    }
}
