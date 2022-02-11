/*
 * Copyright (c) 2022 Francis Wang
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

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.specialize.SpecializeContext

object TypeReferenceForwarderStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val typeReferenceForwarderVisitor = TypeReferenceForwarderVisitor(projectContext.specializeContext!!)
        projectContext.project.accept(typeReferenceForwarderVisitor)
        projectContext.specializeContext = null
    }

    private class TypeReferenceForwarderVisitor(
        private val specializeContext: SpecializeContext
    ) : TreeVisitor() {

        fun forwardType(type: Type, element: EElement): Type {
            val reference = type.reference
            return if (reference is EDeclaration) {
                specializeContext.forward(reference, type.arguments, element).toType()
            } else {
                val arguments = type.arguments.map { forwardType(it, element) }
                reference.toType(arguments)
            }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            typedElement.type = forwardType(typedElement.type, typedElement)
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            cls.superType = forwardType(cls.superType, cls)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments = ArrayList(
                callExpression.typeArguments.map { forwardType(it, callExpression) }
            )
        }
    }
}
