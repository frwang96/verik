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

import io.verik.compiler.ast.element.common.CCallExpression
import io.verik.compiler.ast.element.common.CDotQualifiedExpression
import io.verik.compiler.ast.element.common.CValueArgument
import io.verik.compiler.ast.element.kt.KBinaryExpression
import io.verik.compiler.ast.property.KOperatorKind
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.CoreClass
import io.verik.compiler.core.CoreClassDeclaration
import io.verik.compiler.core.CoreFunction
import io.verik.compiler.core.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object BinaryExpressionReducer : ProjectPass {

    private val referenceMap = HashMap<ReducerEntry, CoreKtFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(CoreClass.Kotlin.INT, CoreClass.Kotlin.INT, KOperatorKind.PLUS)] =
            CoreFunction.Kotlin.Int.PLUS_INT
        referenceMap[ReducerEntry(CoreClass.Kotlin.INT, CoreClass.Kotlin.INT, KOperatorKind.MUL)] =
            CoreFunction.Kotlin.Int.TIMES_INT
    }

    override fun pass(projectContext: ProjectContext) {
        projectContext.verikFiles.forEach {
            it.accept(BinaryExpressionVisitor)
        }
    }

    data class ReducerEntry(
        val receiverClass: CoreClassDeclaration,
        val selectorClass: CoreClassDeclaration,
        val kind: KOperatorKind
    )

    object BinaryExpressionVisitor : TreeVisitor() {

        override fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
            super.visitKBinaryExpression(binaryExpression)
            val receiverClass = binaryExpression.left.type.reference
            val selectorClass = binaryExpression.right.type.reference
            val kind = binaryExpression.kind
            if (receiverClass is CoreClassDeclaration && selectorClass is CoreClassDeclaration) {
                val reference = referenceMap[ReducerEntry(receiverClass, selectorClass, kind)]
                if (reference != null) {
                    val callExpression = CCallExpression(
                        binaryExpression.location,
                        binaryExpression.type,
                        reference,
                        arrayListOf(
                            CValueArgument(
                                binaryExpression.right.location,
                                NullDeclaration,
                                binaryExpression.right
                            )
                        )
                    )
                    binaryExpression.replace(
                        CDotQualifiedExpression(
                            binaryExpression.location,
                            binaryExpression.type,
                            binaryExpression.left,
                            callExpression
                        )
                    )
                }
            }
        }
    }
}