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
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtDoubleColonExpression
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTryExpression

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
