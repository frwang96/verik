/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.pre

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Cardinal
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that simplifies cardinal types by evaluating the cardinal functions.
 */
object CardinalTypeSimplifierStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CardinalTypeSimplifierVisitor)
    }

    object CardinalTypeSimplifierVisitor : TreeVisitor() {

        private fun simplify(type: Type) {
            type.arguments.forEach { simplify(it) }
            when (type.reference) {
                Core.T_ADD -> {
                    val leftValue = type.arguments[0].asCardinalValueOrNull()
                    val rightValue = type.arguments[1].asCardinalValueOrNull()
                    if (leftValue != null && rightValue != null) {
                        type.reference = Cardinal.of(leftValue + rightValue)
                        type.arguments = ArrayList()
                    }
                }
                Core.T_SUB -> {
                    val leftValue = type.arguments[0].asCardinalValueOrNull()
                    val rightValue = type.arguments[1].asCardinalValueOrNull()
                    if (leftValue != null && rightValue != null) {
                        type.reference = Cardinal.of(leftValue - rightValue)
                        type.arguments = ArrayList()
                    }
                }
            }
        }

        override fun visitDescriptor(descriptor: EDescriptor) {
            super.visitDescriptor(descriptor)
            simplify(descriptor.type)
        }
    }
}
