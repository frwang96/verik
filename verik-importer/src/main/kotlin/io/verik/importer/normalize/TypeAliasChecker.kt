/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.normalize

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

/**
 * Normalization checker that checks that types are not aliased between elements.
 */
object TypeAliasChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val typeIndexerVisitor = TypeIndexerVisitor(projectStage)
        projectContext.project.accept(typeIndexerVisitor)
    }

    private class TypeIndexerVisitor(
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private val typeMap = HashMap<Int, ArrayList<Type>>()

        fun addType(type: Type, element: EElement) {
            val hashCode = System.identityHashCode(type)
            val types = typeMap[hashCode]
            if (types != null) {
                types.forEach {
                    if (type === it) {
                        Messages.NORMALIZATION_ERROR.on(
                            element,
                            projectStage,
                            "Unexpected type aliasing: $type in $element"
                        )
                    }
                }
                types.add(type)
            } else {
                typeMap[hashCode] = arrayListOf(type)
            }
            type.arguments.forEach { addType(it, element) }
        }

        override fun visitDescriptor(descriptor: EDescriptor) {
            super.visitDescriptor(descriptor)
            addType(descriptor.type, descriptor)
        }
    }
}
