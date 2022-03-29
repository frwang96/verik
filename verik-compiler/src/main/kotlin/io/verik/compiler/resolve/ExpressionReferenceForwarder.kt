/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import io.verik.compiler.specialize.SpecializeContext

/**
 * Utility class for indexing the receiver expressions with references that need to be forwarded and performing the
 * reference forwarding.
 */
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
            forwardWithReceiver(entry.receiverExpression, receiver, reference, specializeContext)
        } else {
            forwardWithoutReceiver(entry, reference, specializeContext)
        }
    }

    private fun forwardWithReceiver(
        receiverExpression: EReceiverExpression,
        receiver: EExpression,
        reference: EDeclaration,
        specializeContext: SpecializeContext
    ): Boolean {
        val receiverType = receiver.type
        return if (!receiverType.isResolved()) {
            false
        } else {
            val referenceParentClass = reference.getParentClassOrNull()
                ?: Messages.INTERNAL_ERROR.on(receiverExpression, "Unable to find parent class of reference")
            val forwardedReference = forwardClassDeclaration(
                receiverExpression,
                referenceParentClass.cast(),
                receiverType,
                specializeContext
            )
            receiverExpression.reference = forwardedReference
            true
        }
    }

    private fun forwardWithoutReceiver(
        entry: ExpressionReferenceForwarderEntry,
        reference: EDeclaration,
        specializeContext: SpecializeContext
    ): Boolean {
        val referenceParent = reference.parent
        val isTopLevel = referenceParent is EFile ||
            referenceParent is ECompanionObject ||
            (referenceParent is EKtClass && referenceParent.isObject) ||
            reference is EPrimaryConstructor ||
            reference is ESecondaryConstructor ||
            reference is EEnumEntry

        return if (isTopLevel) {
            forwardWithoutReceiverTopLevel(entry.receiverExpression, reference, specializeContext)
        } else {
            forwardWithoutReceiverNotTopLevel(entry, reference, specializeContext)
            true
        }
    }

    private fun forwardWithoutReceiverTopLevel(
        receiverExpression: EReceiverExpression,
        reference: EDeclaration,
        specializeContext: SpecializeContext
    ): Boolean {
        return if (receiverExpression is ECallExpression && reference is EKtAbstractFunction) {
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
    }

    private fun forwardWithoutReceiverNotTopLevel(
        entry: ExpressionReferenceForwarderEntry,
        reference: EDeclaration,
        specializeContext: SpecializeContext
    ) {
        val referenceParentClass = reference.getParentClassOrNull()
        val expressionParentClass = entry.receiverExpression.getParentClassOrNull()
        if (referenceParentClass != null && expressionParentClass != null) {
            val implicitReceiverType = expressionParentClass.type.copy()
            implicitReceiverType.arguments = ArrayList(entry.parentTypeArguments)
            entry.receiverExpression.reference = forwardClassDeclaration(
                entry.receiverExpression,
                referenceParentClass.cast(),
                implicitReceiverType,
                specializeContext
            )
        } else {
            entry.receiverExpression.reference = specializeContext.forward(
                reference,
                entry.parentTypeArguments,
                entry.receiverExpression
            )
        }
    }

    private fun forwardClassDeclaration(
        receiverExpression: EReceiverExpression,
        referenceParentClass: EKtClass,
        receiverType: Type,
        specializeContext: SpecializeContext
    ): Declaration {
        val receiverSuperTypes = receiverType.getSuperTypes()
        receiverSuperTypes.forEach {
            if (it.reference == referenceParentClass) {
                return specializeContext.forward(
                    receiverExpression.reference,
                    it.arguments,
                    receiverExpression
                )
            }
        }
        Messages.INTERNAL_ERROR.on(receiverExpression, "Super type not found: ${referenceParentClass.name}")
    }

    private class ExpressionReferenceForwarderIndexerVisitor : TreeVisitor() {

        val entries = ArrayList<ExpressionReferenceForwarderEntry>()

        private var parentTypeArguments: List<Type> = listOf()

        override fun visitKtClass(cls: EKtClass) {
            if (cls.parent is EFile) {
                parentTypeArguments = cls.typeParameters.map { it.type }
            }
            super.visitKtClass(cls)
            if (cls.parent is EFile) {
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
