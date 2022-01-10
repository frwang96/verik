/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ExpressionReferenceForwarderStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val specializeContext = projectContext.specializeContext!!
        projectContext.project.files().forEach { file ->
            file.declarations.forEach {
                val expressionReferenceForwarderVisitor =
                    ExpressionReferenceForwarderVisitor(listOf(), specializeContext)
                it.accept(expressionReferenceForwarderVisitor)
            }
        }
    }

    private class ExpressionReferenceForwarderVisitor(
        private val typeArguments: List<Type>,
        private val specializeContext: SpecializeContext
    ) : TreeVisitor() {

        // TODO get type arguments from receiver
        private fun getTypeArgumentsFromReceiver(receiver: EExpression): List<Type> {
            return listOf()
        }

        private fun isTopLevel(reference: Declaration): Boolean {
            return reference is EDeclaration && (reference.parent is EFile || reference is EPrimaryConstructor)
        }

        override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
            super.visitReceiverExpression(receiverExpression)
            val reference = receiverExpression.reference
            val receiver = receiverExpression.receiver
            val expressionTypeArguments = when {
                receiver != null -> getTypeArgumentsFromReceiver(receiver)
                isTopLevel(reference) -> {
                    if (receiverExpression is EKtCallExpression && reference is EKtAbstractFunction) {
                        receiverExpression.typeArguments
                    } else listOf()
                }
                else -> typeArguments
            }
            receiverExpression.reference = specializeContext.forward(
                reference,
                expressionTypeArguments,
                receiverExpression
            )
            if (reference is EDeclaration && receiverExpression is EKtCallExpression) {
                receiverExpression.typeArguments = ArrayList()
            }
        }
    }
}
