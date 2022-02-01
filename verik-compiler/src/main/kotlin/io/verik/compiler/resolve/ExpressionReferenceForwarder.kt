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

package io.verik.compiler.resolve

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.specialize.SpecializeContext

object ExpressionReferenceForwarder {

    fun getExpressionReferenceForwarderEntries(
        projectContext: ProjectContext
    ): List<ExpressionReferenceForwarderEntry> {
        val expressionReferenceForwarderIndexerVisitor = ExpressionReferenceForwarderIndexerVisitor()
        projectContext.project.accept(expressionReferenceForwarderIndexerVisitor)
        return expressionReferenceForwarderIndexerVisitor.entries
    }

    fun forward(entry: ExpressionReferenceForwarderEntry, specializeContext: SpecializeContext): Boolean {
        val reference = entry.receiverExpression.reference
        if (reference !is EDeclaration)
            return true
        val receiver = entry.receiverExpression.receiver
        return if (receiver != null) {
            forwardWithReceiver(entry.receiverExpression, receiver, specializeContext)
        } else {
            forwardWithoutReceiver(entry, reference, specializeContext)
        }
    }

    private fun forwardWithReceiver(
        receiverExpression: EReceiverExpression,
        receiver: EExpression,
        specializeContext: SpecializeContext
    ): Boolean {
        val receiverType = receiver.type
        if (!receiverType.isResolved())
            return false
        receiverExpression.reference = specializeContext.forward(
            receiverExpression.reference,
            receiverType.arguments,
            receiverExpression
        )
        return true
    }

    private fun forwardWithoutReceiver(
        entry: ExpressionReferenceForwarderEntry,
        reference: EDeclaration,
        specializeContext: SpecializeContext
    ): Boolean {
        val receiverExpression = entry.receiverExpression
        val isTopLevel = reference.parent is EFile || reference is EPrimaryConstructor || reference is EEnumEntry
        return if (isTopLevel) {
            if (receiverExpression is ECallExpression && reference is EKtAbstractFunction) {
                val typeArguments = receiverExpression.typeArguments
                if (typeArguments.any { !it.isResolved() }) {
                    false
                } else {
                    receiverExpression.reference = specializeContext.forward(
                        reference,
                        typeArguments,
                        receiverExpression
                    )
                    receiverExpression.typeArguments = ArrayList()
                    true
                }
            } else {
                receiverExpression.reference = specializeContext.forward(
                    reference,
                    listOf(),
                    receiverExpression
                )
                true
            }
        } else {
            receiverExpression.reference = specializeContext.forward(
                reference,
                entry.parentTypeArguments,
                receiverExpression
            )
            true
        }
    }

    private class ExpressionReferenceForwarderIndexerVisitor : TreeVisitor() {

        val entries = ArrayList<ExpressionReferenceForwarderEntry>()

        private var parentTypeArguments: List<Type> = listOf()

        override fun visitKtClass(`class`: EKtClass) {
            if (`class`.parent is EFile) {
                parentTypeArguments = `class`.typeParameters.map { it.type }
            }
            super.visitKtClass(`class`)
            if (`class`.parent is EFile) {
                parentTypeArguments = listOf()
            }
        }

        override fun visitKtFunction(function: EKtFunction) {
            if (function.parent is EFile) {
                parentTypeArguments = function.typeParameters.map { it.type }
            }
            super.visitKtFunction(function)
            if (function.parent is EFile) {
                parentTypeArguments = listOf()
            }
        }

        override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
            super.visitReceiverExpression(receiverExpression)
            entries.add(ExpressionReferenceForwarderEntry(receiverExpression, parentTypeArguments))
        }
    }
}
