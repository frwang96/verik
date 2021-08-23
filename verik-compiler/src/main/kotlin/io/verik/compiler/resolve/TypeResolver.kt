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

package io.verik.compiler.resolve

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object TypeResolver : ResolverStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeResolverVisitor)
    }

    object TypeResolverVisitor : TreeVisitor() {

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            val initializer = property.initializer
            if (initializer != null)
                property.type = initializer.type.copy()
        }

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            if (blockExpression.hasStatements())
                blockExpression.type = blockExpression.statements.last().type.copy()
        }

        override fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
            super.visitKtReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is EExpression)
                referenceExpression.type = reference.type.copy()
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is CoreKtFunctionDeclaration)
                reference.resolve(callExpression)
        }
    }
}
