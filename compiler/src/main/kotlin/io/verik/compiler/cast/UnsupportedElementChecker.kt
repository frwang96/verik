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

package io.verik.compiler.cast

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector
import org.jetbrains.kotlin.psi.*

object UnsupportedElementChecker  {

    fun check(projectContext: ProjectContext) {
        projectContext.ktFiles.forEach { it.accept(UnsupportedElementVisitor) }
    }

    object UnsupportedElementVisitor: KtTreeVisitorVoid() {

        override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
            messageCollector.error("Destructuring declaration not supported", destructuringDeclaration)
        }

        override fun visitImportAlias(importAlias: KtImportAlias) {
            messageCollector.error("Import alias not supported", importAlias)
        }

        override fun visitPropertyDelegate(delegate: KtPropertyDelegate) {
            messageCollector.error("Property delegate not supported", delegate)
        }

        override fun visitThrowExpression(expression: KtThrowExpression) {
            messageCollector.error("Throw expression not supported", expression)
        }

        override fun visitTryExpression(expression: KtTryExpression) {
            messageCollector.error("Try expression not supported", expression)
        }

        override fun visitDoubleColonExpression(expression: KtDoubleColonExpression) {
            messageCollector.error("Double colon expression not supported", expression)
        }

        override fun visitClassLiteralExpression(expression: KtClassLiteralExpression) {
            messageCollector.error("Class literal expression not supported", expression)
        }

        override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
            messageCollector.error("Safe qualified expression not supported", expression)
        }

        override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
            messageCollector.error("Object literal expression not supported", expression)
        }
    }
}