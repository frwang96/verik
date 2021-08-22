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
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object BinaryExpressionReducer : PreTransformerStage() {

    private val referenceMap = HashMap<ReducerEntry, CoreKtFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(Core.Kt.INT, Core.Kt.INT, KtBinaryOperatorKind.MUL)] = Core.Kt.Int.TIMES_INT
        referenceMap[ReducerEntry(Core.Kt.INT, Core.Kt.INT, KtBinaryOperatorKind.PLUS)] = Core.Kt.Int.PLUS_INT
        referenceMap[ReducerEntry(Core.Kt.INT, Core.Kt.INT, KtBinaryOperatorKind.MINUS)] = Core.Kt.Int.MINUS_INT
        referenceMap[ReducerEntry(Core.Vk.UBIT, Core.Vk.UBIT, KtBinaryOperatorKind.PLUS)] = Core.Vk.Ubit.PLUS_UBIT
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BinaryExpressionVisitor)
    }

    data class ReducerEntry(
        val receiverDeclaration: CoreClassDeclaration,
        val selectorDeclaration: CoreClassDeclaration,
        val kind: KtBinaryOperatorKind
    )

    object BinaryExpressionVisitor : TreeVisitor() {

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
                            null
                        )
                    )
                    return
                }
            }
            if (kind != KtBinaryOperatorKind.EQ)
                m.error("Binary expression could not be reduced", binaryExpression)
        }
    }
}