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

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDoubleColonExpression
import org.jetbrains.kotlin.psi.KtImportAlias
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTryExpression

object UnsupportedElementCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach { it.accept(UnsupportedElementCheckerVisitor) }
    }

    object UnsupportedElementCheckerVisitor : KtTreeVisitorVoid() {

        override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
            Messages.ELEMENT_NOT_SUPPORTED.on(destructuringDeclaration, "Destructuring declaration")
        }

        override fun visitImportAlias(importAlias: KtImportAlias) {
            Messages.ELEMENT_NOT_SUPPORTED.on(importAlias, "Import alias")
        }

        override fun visitPropertyDelegate(delegate: KtPropertyDelegate) {
            Messages.ELEMENT_NOT_SUPPORTED.on(delegate, "Property delegate")
        }

        override fun visitThrowExpression(expression: KtThrowExpression) {
            Messages.ELEMENT_NOT_SUPPORTED.on(expression, "Throw expression")
        }

        override fun visitTryExpression(expression: KtTryExpression) {
            Messages.ELEMENT_NOT_SUPPORTED.on(expression, "Try expression")
        }

        override fun visitDoubleColonExpression(expression: KtDoubleColonExpression) {
            Messages.ELEMENT_NOT_SUPPORTED.on(expression, "Double colon expression")
        }

        override fun visitClassLiteralExpression(expression: KtClassLiteralExpression) {
            Messages.ELEMENT_NOT_SUPPORTED.on(expression, "Class literal expression")
        }

        override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
            Messages.ELEMENT_NOT_SUPPORTED.on(expression, "Safe qualified expression")
        }

        override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
            Messages.ELEMENT_NOT_SUPPORTED.on(expression, "Object literal expression")
        }
    }
}
