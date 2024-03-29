/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that reduces unary expressions to call expressions if possible.
 */
object UnaryExpressionReducerStage : ProjectStage() {

    private val referenceMap = HashMap<ReducerEntry, CoreFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(Core.Kt.C_Boolean, KtUnaryOperatorKind.EXCL)] = Core.Kt.Boolean.F_not
        referenceMap[ReducerEntry(Core.Kt.C_Int, KtUnaryOperatorKind.PLUS)] = Core.Kt.Int.F_unaryPlus
        referenceMap[ReducerEntry(Core.Kt.C_Int, KtUnaryOperatorKind.MINUS)] = Core.Kt.Int.F_unaryMinus
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, KtUnaryOperatorKind.PLUS)] = Core.Vk.Ubit.F_unaryPlus
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, KtUnaryOperatorKind.MINUS)] = Core.Vk.Ubit.F_unaryMinus
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, KtUnaryOperatorKind.EXCL)] = Core.Vk.Ubit.F_not
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, KtUnaryOperatorKind.PLUS)] = Core.Vk.Sbit.F_unaryPlus
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, KtUnaryOperatorKind.MINUS)] = Core.Vk.Sbit.F_unaryMinus
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, KtUnaryOperatorKind.EXCL)] = Core.Vk.Sbit.F_not
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UnaryExpressionReducerVisitor)
    }

    data class ReducerEntry(
        val expressionDeclaration: CoreClassDeclaration,
        val kind: KtUnaryOperatorKind
    )

    private object UnaryExpressionReducerVisitor : TreeVisitor() {

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            val expressionDeclaration = unaryExpression.expression.type.reference
            val kind = unaryExpression.kind
            // TODO proper handling of null
            if (kind == KtUnaryOperatorKind.EXCL_EXCL) {
                unaryExpression.replace(unaryExpression.expression)
                return
            }
            if (expressionDeclaration is CoreClassDeclaration) {
                val reference = referenceMap[ReducerEntry(expressionDeclaration, kind)]
                if (reference != null) {
                    unaryExpression.replace(
                        ECallExpression(
                            unaryExpression.location,
                            unaryExpression.type,
                            reference,
                            unaryExpression.expression,
                            false,
                            ArrayList(),
                            ArrayList()
                        )
                    )
                    return
                }
            }
            if (kind.isReducible()) {
                Messages.INTERNAL_ERROR.on(unaryExpression, "Unary expression could not be reduced")
            }
        }
    }
}
