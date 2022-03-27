/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.post

import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object SuperDescriptorTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(SuperDescriptorTransformerVisitor)
    }

    object SuperDescriptorTransformerVisitor : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val superDescriptor = cls.superDescriptor
            if (superDescriptor.type.reference is ETypeParameter) {
                val simpleDescriptor = ESimpleDescriptor(
                    superDescriptor.location,
                    Core.C_Class.toType()
                )
                simpleDescriptor.parent = cls
                cls.superDescriptor = simpleDescriptor
            }
        }
    }
}
