/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object InjectedPropertyInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val injectedPropertyInterpreterVisitor = InjectedPropertyInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(injectedPropertyInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class InjectedPropertyInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretInjectedProperty(property: EProperty): EInjectedProperty? {
            val initializer = property.initializer
            return if (initializer is EStringTemplateExpression) {
                val injectedExpression = EInjectedExpression(property.location, initializer.entries)
                EInjectedProperty(
                    property.location,
                    property.name,
                    injectedExpression
                )
            } else if (initializer is ECallExpression && initializer.reference == Core.Kt.Text.F_trimIndent) {
                val receiver = initializer.receiver!!
                if (receiver is EStringTemplateExpression) {
                    val injectedExpression = EInjectedExpression(
                        property.location,
                        StringEntry.trimIndent(receiver.entries)
                    )
                    EInjectedProperty(
                        property.location,
                        property.name,
                        injectedExpression
                    )
                } else {
                    Messages.ILLEGAL_INJECTED_PROPERTY.on(property, property.name)
                    null
                }
            } else {
                Messages.ILLEGAL_INJECTED_PROPERTY.on(property, property.name)
                null
            }
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (property.hasAnnotationEntry(AnnotationEntries.INJ)) {
                val injectedProperty = interpretInjectedProperty(property)
                if (injectedProperty != null) {
                    referenceUpdater.replace(property, injectedProperty)
                }
            }
        }
    }
}
