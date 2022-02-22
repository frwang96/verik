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

object BinaryExpressionReducerStage : ProjectStage() {

    private val referenceMap = HashMap<ReducerEntry, CoreFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.MUL)] =
            Core.Kt.Int.F_times_Int
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.PLUS)] =
            Core.Kt.Int.F_plus_Int
        referenceMap[ReducerEntry(Core.Kt.C_Int, Core.Kt.C_Int, KtBinaryOperatorKind.MINUS)] =
            Core.Kt.Int.F_minus_Int
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
            if (kind.isReducible())
                Messages.INTERNAL_ERROR.on(binaryExpression, "Binary expression could not be reduced")
        }
    }
}
