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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.common.KtOperatorKind
import io.verik.compiler.ast.common.NullDeclaration
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkCallExpression
import io.verik.compiler.ast.element.VkDotQualifiedExpression
import io.verik.compiler.ast.element.VkKtBinaryExpression
import io.verik.compiler.ast.element.VkValueArgument
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.core.CoreClass
import io.verik.compiler.core.CoreClassDeclaration
import io.verik.compiler.core.CoreFunction
import io.verik.compiler.core.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object BinaryExpressionReducer : ProjectPass {

    private val referenceMap = HashMap<ReducerEntry, CoreKtFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(CoreClass.Kotlin.INT, CoreClass.Kotlin.INT, KtOperatorKind.PLUS)] =
            CoreFunction.Kotlin.Int.PLUS_INT
        referenceMap[ReducerEntry(CoreClass.Kotlin.INT, CoreClass.Kotlin.INT, KtOperatorKind.MUL)] =
            CoreFunction.Kotlin.Int.TIMES_INT
    }

    override fun pass(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(BinaryExpressionVisitor)
        }
    }

    data class ReducerEntry(
        val receiverClass: CoreClassDeclaration,
        val selectorClass: CoreClassDeclaration,
        val kind: KtOperatorKind
    )

    object BinaryExpressionVisitor : TreeVisitor() {

        override fun visitKtBinaryExpression(ktBinaryExpression: VkKtBinaryExpression) {
            super.visitKtBinaryExpression(ktBinaryExpression)
            val receiverClass = ktBinaryExpression.left.type.reference
            val selectorClass = ktBinaryExpression.right.type.reference
            val kind = ktBinaryExpression.kind
            if (receiverClass is CoreClassDeclaration && selectorClass is CoreClassDeclaration) {
                val reference = referenceMap[ReducerEntry(receiverClass, selectorClass, kind)]
                if (reference != null) {
                    val callExpression = VkCallExpression(
                        ktBinaryExpression.location,
                        ktBinaryExpression.type,
                        reference,
                        arrayListOf(
                            VkValueArgument(
                                ktBinaryExpression.right.location,
                                NullDeclaration,
                                ktBinaryExpression.right
                            )
                        )
                    )
                    ktBinaryExpression.replace(
                        VkDotQualifiedExpression(
                            ktBinaryExpression.location,
                            ktBinaryExpression.type,
                            ktBinaryExpression.left,
                            callExpression
                        )
                    )
                }
            }
        }
    }
}