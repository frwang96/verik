/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks that declaration names are valid SystemVerilog identifiers.
 */
object PostNameCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PostNameCheckerVisitor)
    }

    private object PostNameCheckerVisitor : TreeVisitor() {

        private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9$]*")

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is EDeclaration) {
                if (element is EPackage || element is EFile)
                    return
                if (!element.name.matches(nameRegex))
                    Messages.ILLEGAL_NAME.on(element, element.name)
            }
        }
    }
}
