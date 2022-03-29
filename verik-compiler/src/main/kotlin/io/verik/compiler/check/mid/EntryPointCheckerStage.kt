/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

/**
 * Stage that checks that the configured entry points exist and are legal entry points.
 */
object EntryPointCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val entryPointCheckerVisitor = EntryPointCheckerVisitor()
        projectContext.project.accept(entryPointCheckerVisitor)
        projectContext.config.entryPoints.forEach {
            if (it !in entryPointCheckerVisitor.names) {
                Messages.ENTRY_POINT_NOT_FOUND.on(SourceLocation.NULL, it)
            }
        }
    }

    private class EntryPointCheckerVisitor : TreeVisitor() {

        val names = ArrayList<String>()

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            if (declaration.hasAnnotationEntry(AnnotationEntries.ENTRY)) {
                declaration.getQualifiedName()?.let { names.add(it) }
            }
        }
    }
}
