/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks for component instantiations are not situated in the right context.
 */
object ComponentInstantiationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ComponentInstantiationCheckerVisitor)
    }

    private object ComponentInstantiationCheckerVisitor : TreeVisitor() {

        private fun unwrapType(type: Type): Type {
            return if (type.reference == Core.Vk.C_Cluster) {
                unwrapType(type.arguments[1])
            } else type
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (unwrapType(property.type).isSubtype(Core.Vk.C_Component)) {
                val parent = property.parent
                if (parent is EKtClass && parent.type.isSubtype(Core.Vk.C_Component)) {
                    if (!property.hasAnnotationEntry(AnnotationEntries.MAKE)) {
                        Messages.MISSING_MAKE_ANNOTATION.on(property)
                    }
                } else {
                    if (property.hasAnnotationEntry(AnnotationEntries.MAKE)) {
                        Messages.MAKE_OUT_OF_CONTEXT.on(property)
                    }
                }
            } else {
                if (property.hasAnnotationEntry(AnnotationEntries.MAKE)) {
                    Messages.ILLEGAL_MAKE_ANNOTATION.on(property)
                }
            }
        }
    }
}
