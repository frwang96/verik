/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that resolves types in the AST and forwards references of receiver expressions. Unfortunately, reference
 * forwarding depends on type resolution in some cases and so it cannot be performed independently beforehand.
 */
object TypeResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val specializeContext = projectContext.specializeContext!!
        var expressionReferenceForwarderEntries =
            ExpressionReferenceForwarder.getExpressionReferenceForwarderEntries(projectContext)
        var typeConstraints = TypeConstraintCollector.collect(projectContext)
        var isLoop: Boolean
        do {
            isLoop = false
            val newTypeConstraints = ArrayList<TypeConstraint>()
            if (expressionReferenceForwarderEntries.isNotEmpty()) {
                expressionReferenceForwarderEntries = expressionReferenceForwarderEntries.filter {
                    val isForwarded = ExpressionReferenceForwarder.forward(it, specializeContext)
                    if (isForwarded) {
                        newTypeConstraints.addAll(TypeConstraintCollector.collect(it.receiverExpression))
                        isLoop = true
                    }
                    !isForwarded
                }
            }
            newTypeConstraints.addAll(typeConstraints)
            if (newTypeConstraints.isNotEmpty()) {
                typeConstraints = newTypeConstraints.filter {
                    val isResolved = TypeConstraintResolver.resolve(it)
                    if (isResolved) {
                        isLoop = true
                    }
                    !isResolved
                }
            }
        } while (isLoop)
    }
}
