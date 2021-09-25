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

import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtAbstractFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object ArrayAccessExpressionReducerStage : ProjectStage() {

    override val checkNormalization = true

    private val getReferenceMap = HashMap<ReducerEntry, CoreKtAbstractFunctionDeclaration>()
    private val setReferenceMap = HashMap<Declaration, CoreKtAbstractFunctionDeclaration>()

    init {
        getReferenceMap[ReducerEntry(Core.Vk.C_Ubit, listOf(Core.Kt.C_Int))] = Core.Vk.Ubit.F_get_Int
        getReferenceMap[ReducerEntry(Core.Vk.C_Unpacked, listOf(Core.Kt.C_Int))] = Core.Vk.Unpacked.F_get_Int
        setReferenceMap[Core.Vk.Ubit.F_get_Int] = Core.Vk.Ubit.F_set_Int_Boolean
        setReferenceMap[Core.Vk.Unpacked.F_get_Int] = Core.Vk.Unpacked.F_set_Int_Any
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ArrayAccessExpressionReducerVisitor)
    }

    data class ReducerEntry(
        val arrayDeclaration: Declaration,
        val indexDeclarations: List<Declaration>
    )

    object ArrayAccessExpressionReducerVisitor : TreeVisitor() {

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val left = binaryExpression.left
            if (left is EKtCallExpression) {
                val reference = setReferenceMap[left.reference]
                if (reference != null) {
                    binaryExpression.replace(
                        EKtCallExpression(
                            binaryExpression.location,
                            binaryExpression.type,
                            reference,
                            left.receiver,
                            ArrayList(left.valueArguments + binaryExpression.right),
                            arrayListOf()
                        )
                    )
                }
            }
        }

        override fun visitKtArrayAccessExpression(arrayAccessExpression: EKtArrayAccessExpression) {
            super.visitKtArrayAccessExpression(arrayAccessExpression)
            val arrayDeclaration = arrayAccessExpression.array.type.reference
            val indexDeclarations = arrayAccessExpression.indices.map { it.type.reference }
            val reference = getReferenceMap[ReducerEntry(arrayDeclaration, indexDeclarations)]
            if (reference != null) {
                arrayAccessExpression.replace(
                    EKtCallExpression(
                        arrayAccessExpression.location,
                        arrayAccessExpression.type,
                        reference,
                        arrayAccessExpression.array,
                        arrayAccessExpression.indices,
                        arrayListOf()
                    )
                )
            } else {
                Messages.INTERNAL_ERROR.on(arrayAccessExpression, "Array access expression could not be reduced")
            }
        }
    }
}
