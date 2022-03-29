/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtDoubleColonExpression
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTryExpression

/**
 * Check for unsupported elements.
 */
object UnsupportedElementCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach {
            it.accept(UnsupportedElementCheckerVisitor)
        }
    }

    private object UnsupportedElementCheckerVisitor : KtTreeVisitorVoid() {

        override fun visitPropertyDelegate(delegate: KtPropertyDelegate) {
            Messages.UNSUPPORTED_ELEMENT.on(delegate, "Property delegate")
        }

        override fun visitThrowExpression(expression: KtThrowExpression) {
            Messages.UNSUPPORTED_ELEMENT.on(expression, "Throw expression")
        }

        override fun visitTryExpression(expression: KtTryExpression) {
            Messages.UNSUPPORTED_ELEMENT.on(expression, "Try expression")
        }

        override fun visitDoubleColonExpression(expression: KtDoubleColonExpression) {
            Messages.UNSUPPORTED_ELEMENT.on(expression, "Double colon expression")
        }

        override fun visitClassLiteralExpression(expression: KtClassLiteralExpression) {
            Messages.UNSUPPORTED_ELEMENT.on(expression, "Class literal expression")
        }

        override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
            Messages.UNSUPPORTED_ELEMENT.on(expression, "Object literal expression")
        }
    }
}
