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

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.common.EAbstractReceiverExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object UntransformedReferenceCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UntransformedReferenceCheckerVisitor)
    }

    private object UntransformedReferenceCheckerVisitor : TreeVisitor() {

        private const val message = "Reference has not been transformed to SystemVerilog"

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            checkType(typedElement.type, typedElement)
        }

        override fun visitAbstractReceiverExpression(abstractReceiverExpression: EAbstractReceiverExpression) {
            super.visitAbstractReceiverExpression(abstractReceiverExpression)
            if (abstractReceiverExpression.reference is CoreDeclaration)
                Messages.INTERNAL_ERROR.on(
                    abstractReceiverExpression,
                    "$message : ${abstractReceiverExpression.reference.name}"
                )
        }

        private fun checkType(type: Type, element: EElement) {
            type.arguments.forEach { checkType(it, element) }
            if (type.reference is CoreDeclaration)
                Messages.INTERNAL_ERROR.on(element, "$message : ${type.reference.name}")
        }
    }
}
