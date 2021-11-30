/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

object UnsupportedModifierCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach { it.accept(UnsupportedModifierCheckerVisitor) }
    }

    object UnsupportedModifierCheckerVisitor : KtTreeVisitorVoid() {

        private val unsupportedModifiers = listOf(
            KtTokens.ANNOTATION_KEYWORD,
            KtTokens.CROSSINLINE_KEYWORD,
            KtTokens.OPERATOR_KEYWORD,
            KtTokens.REIFIED_KEYWORD,
            KtTokens.SEALED_KEYWORD,
            KtTokens.SUSPEND_KEYWORD,
            KtTokens.VARARG_KEYWORD
        )

        override fun visitModifierList(list: KtModifierList) {
            unsupportedModifiers.forEach {
                if (list.hasModifier(it))
                    Messages.MODIFIER_NOT_SUPPORTED.on(list, it)
            }
        }
    }
}
