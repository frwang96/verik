/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EIsExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that reduces reference expressions that are smart casts.
 */
object SmartCastReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val smartCastIndexerVisitor = SmartCastIndexerVisitor()
        projectContext.project.accept(smartCastIndexerVisitor)
        val smartCastReducerVisitor = SmartCastReducerVisitor(
            projectContext.castContext.smartCastExpressions,
            smartCastIndexerVisitor.smartCastEntryMap
        )
        projectContext.project.accept(smartCastReducerVisitor)
    }

    private class SmartCastIndexerVisitor : TreeVisitor() {

        val smartCastEntryMap = HashMap<SmartCastEntry, EProperty>()

        override fun visitIsExpression(isExpression: EIsExpression) {
            super.visitIsExpression(isExpression)
            val expression = isExpression.expression
            if (expression is EReferenceExpression) {
                val reference = expression.reference
                if (reference is EAbstractProperty) {
                    val smartCastEntry = SmartCastEntry(reference, isExpression.castType)
                    if (smartCastEntry in smartCastEntryMap) {
                        Messages.INTERNAL_ERROR.on(isExpression, "Unable to reduce smart casts for is expression")
                    } else {
                        smartCastEntryMap[smartCastEntry] = isExpression.property.cast()
                    }
                }
            }
        }
    }

    private class SmartCastReducerVisitor(
        private val smartCastExpressions: HashSet<EReferenceExpression>,
        private val smartCastEntryMap: HashMap<SmartCastEntry, EProperty>
    ) : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            if (referenceExpression in smartCastExpressions) {
                val reference = referenceExpression.reference
                if (reference is EAbstractProperty) {
                    val smartCastEntry = SmartCastEntry(reference, referenceExpression.type)
                    val property = smartCastEntryMap[smartCastEntry]
                    if (property != null) {
                        referenceExpression.reference = property
                        referenceExpression.receiver = null
                        return
                    }
                } else {
                    Messages.INTERNAL_ERROR.on(referenceExpression, "Unable to reduce smart cast")
                }
            }
        }
    }

    private data class SmartCastEntry(val property: EAbstractProperty, val type: Type)
}
