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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object TypeReferenceTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeReferenceTransformerVisitor)
    }

    private object TypeReferenceTransformerVisitor : TreeVisitor() {

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            transform(typedElement.type, typedElement)
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            callExpression.typeArguments.forEach { transform(it, callExpression) }
        }

        private fun transform(type: Type, element: EElement) {
            type.arguments.forEach { transform(it, element) }
            val reference = type.reference
            if (reference is CoreClassDeclaration) {
                val targetClassDeclaration = reference.targetClassDeclaration
                if (targetClassDeclaration != null) {
                    type.reference = targetClassDeclaration
                } else {
                    Messages.INTERNAL_ERROR.on(element, "Unable to transform type reference : ${reference.name}")
                }
            }
        }
    }
}
