/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeConstraint

/**
 * Core binary functions from Sbit.
 */
object CoreVkSbitBinary : CoreScope(Core.Vk.C_Sbit) {

    val F_plus_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Ubit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbitBinary.F_plus_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_plus_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "plus",
        "fun plus(Sbit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_plus_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_add_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "add",
        "fun add(Ubit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbitBinary.F_add_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_add_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "add",
        "fun add(Sbit)",
        SvBinaryOperatorKind.PLUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_add_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_minus_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "minus",
        "fun minus(Ubit)",
        SvBinaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbitBinary.F_minus_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_minus_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "minus",
        "fun minus(Sbit)",
        SvBinaryOperatorKind.MINUS
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_minus_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_times_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "times",
        "fun times(Ubit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbitBinary.F_times_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_times_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "times",
        "fun times(Sbit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_times_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_mul_Ubit = object : BinaryCoreFunctionDeclaration(
        parent,
        "mul",
        "fun mul(Ubit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbitBinary.F_mul_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_mul_Sbit = object : BinaryCoreFunctionDeclaration(
        parent,
        "mul",
        "fun mul(Sbit)",
        SvBinaryOperatorKind.MUL
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_mul_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_and_Ubit = object : BinaryCoreFunctionDeclaration(parent, "and", "fun and(Ubit)", SvBinaryOperatorKind.AND) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbitBinary.F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_and_Sbit = object : BinaryCoreFunctionDeclaration(parent, "and", "fun and(Sbit)", SvBinaryOperatorKind.AND) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_or_Ubit = object : BinaryCoreFunctionDeclaration(parent, "or", "fun or(Ubit)", SvBinaryOperatorKind.OR) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_or_Sbit = object : BinaryCoreFunctionDeclaration(parent, "or", "fun or(Sbit)", SvBinaryOperatorKind.OR) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_xor_Ubit = object : BinaryCoreFunctionDeclaration(parent, "xor", "fun xor(Ubit)", SvBinaryOperatorKind.XOR) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_xor_Sbit = object : BinaryCoreFunctionDeclaration(parent, "xor", "fun xor(Sbit)", SvBinaryOperatorKind.XOR) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_and_Ubit.getTypeConstraints(callExpression)
        }
    }

    val F_shl_Int = object : TransformableCoreFunctionDeclaration(parent, "shl", "fun shl(Int)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkUbitBinary.F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkUbitBinary.F_shl_Int.transform(callExpression)
        }
    }

    val F_shl_Ubit = object : TransformableCoreFunctionDeclaration(parent, "shl", "fun shl(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_shl_Int.transform(callExpression)
        }
    }

    val F_shr_Int = object : TransformableCoreFunctionDeclaration(parent, "shr", "fun shr(Int)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.GTGTGT
            )
        }
    }

    val F_shr_Ubit = object : TransformableCoreFunctionDeclaration(parent, "shr", "fun shr(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_shr_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_shr_Int.transform(callExpression)
        }
    }

    val F_ushr_Int = object : TransformableCoreFunctionDeclaration(parent, "ushr", "fun ushr(Int)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_shl_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return ESvBinaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                SvBinaryOperatorKind.GTGT
            )
        }
    }

    val F_ushr_Ubit = object : TransformableCoreFunctionDeclaration(parent, "ushr", "fun ushr(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_ushr_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return F_ushr_Int.transform(callExpression)
        }
    }
}
