/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.target.common.TargetFunctionDeclaration

/**
 * A core declaration that represents a function.
 */
sealed class CoreFunctionDeclaration : CoreDeclaration {

    open fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
        return listOf()
    }

    open fun evaluate(callExpression: ECallExpression): EConstantExpression? {
        return null
    }
}

/**
 * A core function declaration that transforms to [targetFunctionDeclaration].
 */
open class BasicCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String,
    val targetFunctionDeclaration: TargetFunctionDeclaration?
) : CoreFunctionDeclaration()

/**
 * A core function declaration that transforms according to [transform].
 */
abstract class TransformableCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String
) : CoreFunctionDeclaration() {

    abstract fun transform(callExpression: ECallExpression): EExpression
}

/**
 * A core function declaration that transforms to a unary operator of [kind].
 */
open class UnaryCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String,
    val kind: SvUnaryOperatorKind
) : CoreFunctionDeclaration()

/**
 * A core function declaration that transforms to a binary operator of [kind].
 */
open class BinaryCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String,
    val kind: SvBinaryOperatorKind
) : CoreFunctionDeclaration()
