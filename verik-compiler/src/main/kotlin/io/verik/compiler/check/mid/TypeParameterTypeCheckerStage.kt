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

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.core.common.CoreOptionalFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeParameterTypeCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeParameterTypeCheckerVisitor)
    }

    private object TypeParameterTypeCheckerVisitor : TreeVisitor() {

        private fun checkType(type: Type, element: EElement) {
            when (val reference = type.reference) {
                is CoreCardinalFunctionDeclaration ->
                    checkCardinalFunction(reference, type.arguments, element)
                is CoreOptionalFunctionDeclaration ->
                    checkOptionalFunction(type.arguments, element)
            }
            type.arguments.forEach { checkType(it, element) }
        }

        private fun checkCardinalFunction(
            declaration: CoreCardinalFunctionDeclaration,
            arguments: List<Type>,
            element: EElement
        ) {
            if (declaration == Core.Vk.T_IF) {
                if (!arguments[0].isOptionalType())
                    Messages.EXPECTED_OPTIONAL_TYPE.on(element, arguments[0])
                if (!arguments[1].isCardinalType())
                    Messages.EXPECTED_CARDINAL_TYPE.on(element, arguments[1])
                if (!arguments[2].isCardinalType())
                    Messages.EXPECTED_CARDINAL_TYPE.on(element, arguments[2])
            } else {
                arguments.forEach {
                    if (!it.isCardinalType())
                        Messages.EXPECTED_CARDINAL_TYPE.on(element, it)
                }
            }
        }

        private fun checkOptionalFunction(arguments: List<Type>, element: EElement) {
            arguments.forEach {
                if (!it.isOptionalType())
                    Messages.EXPECTED_OPTIONAL_TYPE.on(element, it)
            }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            checkType(typedElement.type, typedElement)
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            callExpression.typeArguments.forEach { checkType(it, callExpression) }
        }
    }
}
