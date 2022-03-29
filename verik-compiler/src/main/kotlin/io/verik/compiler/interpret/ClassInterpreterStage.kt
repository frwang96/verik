/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that interprets SystemVerilog classes from Kotlin classes.
 */
object ClassInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val classInterpreterVisitor = ClassInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(classInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ClassInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val interpretedClass = ESvClass(
                location = cls.location,
                bodyStartLocation = cls.bodyStartLocation,
                bodyEndLocation = cls.bodyEndLocation,
                name = cls.name,
                type = cls.type,
                annotationEntries = cls.annotationEntries,
                documentationLines = cls.documentationLines,
                superType = cls.superType,
                typeParameters = cls.typeParameters,
                declarations = cls.declarations,
                isObject = cls.isObject
            )
            referenceUpdater.replace(cls, interpretedClass)
        }
    }
}
