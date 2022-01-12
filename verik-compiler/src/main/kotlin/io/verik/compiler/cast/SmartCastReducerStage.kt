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

package io.verik.compiler.cast

import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

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
                    } else {
                        Messages.INTERNAL_ERROR.on(referenceExpression, "Unable to reduce smart cast")
                    }
                } else {
                    Messages.INTERNAL_ERROR.on(referenceExpression, "Unable to reduce smart cast")
                }
            }
        }
    }

    private data class SmartCastEntry(val property: EAbstractProperty, val type: Type)
}
