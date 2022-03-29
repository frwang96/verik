/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/**
 * Check for unsupported modifiers.
 */
object UnsupportedModifierCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach {
            it.accept(UnsupportedModifierCheckerVisitor)
        }
    }

    private object UnsupportedModifierCheckerVisitor : KtTreeVisitorVoid() {

        private val unsupportedModifiers = listOf(
            KtTokens.ANNOTATION_KEYWORD,
            KtTokens.CROSSINLINE_KEYWORD,
            KtTokens.DATA_KEYWORD,
            KtTokens.EXTERNAL_KEYWORD,
            KtTokens.IN_KEYWORD,
            KtTokens.NOINLINE_KEYWORD,
            KtTokens.OPERATOR_KEYWORD,
            KtTokens.OUT_KEYWORD,
            KtTokens.REIFIED_KEYWORD,
            KtTokens.SEALED_KEYWORD,
            KtTokens.SUSPEND_KEYWORD,
            KtTokens.TAILREC_KEYWORD,
            KtTokens.VARARG_KEYWORD
        )

        override fun visitModifierList(list: KtModifierList) {
            unsupportedModifiers.forEach {
                if (list.hasModifier(it))
                    Messages.UNSUPPORTED_MODIFIER.on(list, it)
            }
        }
    }
}
