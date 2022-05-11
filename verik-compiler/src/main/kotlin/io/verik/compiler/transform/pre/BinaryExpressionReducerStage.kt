/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that reduces binary expressions to call expressions if possible.
 */
object BinaryExpressionReducerStage : ProjectStage() {

    private val referenceMap = HashMap<ReducerEntry, CoreFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.RANGE)] =
            Core.Kt.Int.F_rangeTo_Int
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.MUL)] =
            Core.Kt.Int.F_times_Int
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.DIV)] =
            Core.Kt.Int.F_div_Int
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.PLUS)] =
            Core.Kt.Int.F_plus_Int
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.MINUS)] =
            Core.Kt.Int.F_minus_Int
        referenceMap[ReducerEntry(Core.Kt.C_Double, Core.Kt.C_Int, KtBinaryOperatorKind.PLUS)] =
            Core.Kt.Double.F_plus_Int
        referenceMap[ReducerEntry(Core.Kt.C_Double, Core.Kt.C_Double, KtBinaryOperatorKind.PLUS)] =
            Core.Kt.Double.F_plus_Double
        referenceMap[ReducerEntry(Core.Kt.C_Double, Core.Kt.C_Int, KtBinaryOperatorKind.DIV)] =
            Core.Kt.Double.F_div_Int
        referenceMap[ReducerEntry(Core.Kt.C_String, Core.Kt.C_String, KtBinaryOperatorKind.PLUS)] =
            Core.Kt.String.F_plus_Any
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, Core.Vk.C_Ubit, KtBinaryOperatorKind.PLUS)] =
            Core.Vk.Ubit.F_plus_Ubit
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, Core.Vk.C_Sbit, KtBinaryOperatorKind.PLUS)] =
            Core.Vk.Ubit.F_plus_Sbit
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, Core.Vk.C_Ubit, KtBinaryOperatorKind.MINUS)] =
            Core.Vk.Ubit.F_minus_Ubit
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, Core.Vk.C_Sbit, KtBinaryOperatorKind.MINUS)] =
            Core.Vk.Ubit.F_minus_Sbit
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, Core.Vk.C_Ubit, KtBinaryOperatorKind.MUL)] =
            Core.Vk.Ubit.F_times_Ubit
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, Core.Vk.C_Sbit, KtBinaryOperatorKind.MUL)] =
            Core.Vk.Ubit.F_times_Sbit
        referenceMap[ReducerEntry(Core.Vk.C_Ubit, Core.Vk.C_Ubit, KtBinaryOperatorKind.DIV)] =
            Core.Vk.Ubit.F_div_Ubit
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, Core.Vk.C_Ubit, KtBinaryOperatorKind.PLUS)] =
            Core.Vk.Sbit.F_plus_Ubit
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, Core.Vk.C_Sbit, KtBinaryOperatorKind.PLUS)] =
            Core.Vk.Sbit.F_plus_Sbit
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, Core.Vk.C_Ubit, KtBinaryOperatorKind.MINUS)] =
            Core.Vk.Sbit.F_minus_Ubit
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, Core.Vk.C_Sbit, KtBinaryOperatorKind.MINUS)] =
            Core.Vk.Sbit.F_minus_Sbit
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, Core.Vk.C_Ubit, KtBinaryOperatorKind.MUL)] =
            Core.Vk.Sbit.F_times_Ubit
        referenceMap[ReducerEntry(Core.Vk.C_Sbit, Core.Vk.C_Sbit, KtBinaryOperatorKind.MUL)] =
            Core.Vk.Sbit.F_times_Sbit
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BinaryExpressionReducerVisitor)
    }

    data class ReducerEntry(
        val receiverDeclaration: CoreClassDeclaration,
        val selectorDeclaration: CoreClassDeclaration,
        val kind: KtBinaryOperatorKind
    )

    private object BinaryExpressionReducerVisitor : TreeVisitor() {

        // TODO generalize reduce for Core.Kt.String.F_plus_Any
        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val receiverDeclaration = binaryExpression.left.type.reference
            val selectorDeclaration = binaryExpression.right.type.reference
            val kind = binaryExpression.kind
            if (receiverDeclaration is CoreClassDeclaration && selectorDeclaration is CoreClassDeclaration) {
                val reference = referenceMap[ReducerEntry(receiverDeclaration, selectorDeclaration, kind)]
                if (reference != null) {
                    binaryExpression.replace(
                        ECallExpression(
                            binaryExpression.location,
                            binaryExpression.type,
                            reference,
                            binaryExpression.left,
                            false,
                            arrayListOf(binaryExpression.right),
                            arrayListOf()
                        )
                    )
                    return
                }
            }
            if (kind.isReducible()) {
                Messages.INTERNAL_ERROR.on(binaryExpression, "Binary expression could not be reduced")
            }
        }
    }
}
