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

import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object BinaryExpressionReducerStage : ProjectStage() {

    override val checkNormalization = true

    private val referenceMap = HashMap<ReducerEntry, CoreKtFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(Core.Kt.C_INT, Core.Kt.C_INT, KtBinaryOperatorKind.MUL)] = Core.Kt.Int.F_TIMES_INT
        referenceMap[ReducerEntry(Core.Kt.C_INT, Core.Kt.C_INT, KtBinaryOperatorKind.PLUS)] = Core.Kt.Int.F_PLUS_INT
        referenceMap[ReducerEntry(Core.Kt.C_INT, Core.Kt.C_INT, KtBinaryOperatorKind.MINUS)] = Core.Kt.Int.F_MINUS_INT
        referenceMap[ReducerEntry(Core.Vk.C_UBIT, Core.Vk.C_UBIT, KtBinaryOperatorKind.PLUS)] = Core.Vk.Ubit.F_PLUS_UBIT
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BinaryExpressionReducerVisitor)
    }

    data class ReducerEntry(
        val receiverDeclaration: CoreClassDeclaration,
        val selectorDeclaration: CoreClassDeclaration,
        val kind: KtBinaryOperatorKind
    )

    object BinaryExpressionReducerVisitor : TreeVisitor() {

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val receiverDeclaration = binaryExpression.left.type.reference
            val selectorDeclaration = binaryExpression.right.type.reference
            val kind = binaryExpression.kind
            if (receiverDeclaration is CoreClassDeclaration && selectorDeclaration is CoreClassDeclaration) {
                val reference = referenceMap[ReducerEntry(receiverDeclaration, selectorDeclaration, kind)]
                if (reference != null) {
                    binaryExpression.replace(
                        EKtCallExpression(
                            binaryExpression.location,
                            binaryExpression.type,
                            reference,
                            binaryExpression.left,
                            arrayListOf(binaryExpression.right),
                            arrayListOf()
                        )
                    )
                    return
                }
            }
            if (kind != KtBinaryOperatorKind.EQ)
                Messages.INTERNAL_ERROR.on(binaryExpression, "Binary expression could not be reduced")
        }
    }
}
