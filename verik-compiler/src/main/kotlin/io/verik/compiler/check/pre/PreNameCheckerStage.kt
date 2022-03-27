/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

object PreNameCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach {
            it.accept(PreNameCheckerVisitor)
        }
    }

    private object PreNameCheckerVisitor : KtTreeVisitorVoid() {

        private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9]*")

        override fun visitDeclaration(dcl: KtDeclaration) {
            super.visitDeclaration(dcl)
            if (dcl is KtClassInitializer) return
            val name = dcl.name!!
            if (!name.matches(nameRegex)) {
                Messages.ILLEGAL_NAME.on(dcl, name)
            }
        }
    }
}
