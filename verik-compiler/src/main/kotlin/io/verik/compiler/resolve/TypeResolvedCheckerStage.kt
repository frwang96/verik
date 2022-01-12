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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeResolvedCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeResolvedCheckerVisitor)
    }

    private object TypeResolvedCheckerVisitor : TreeVisitor() {

        private fun isPositive(type: Type, element: EElement): Boolean {
            if (type.arguments.any { !isPositive(it, element) })
                return false
            return if (type.isCardinalType()) {
                return type.asCardinalValue(element) >= 0
            } else true
        }

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            if (declaration !is ETypeParameter) {
                if (!declaration.type.isResolved()) {
                    Messages.UNRESOLVED_DECLARATION.on(declaration, declaration.name)
                } else if (!isPositive(declaration.type, declaration)) {
                    Messages.CARDINAL_NEGATIVE.on(declaration, declaration.type)
                }
            }
        }

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            if (!expression.type.isResolved()) {
                Messages.UNRESOLVED_EXPRESSION.on(expression)
            } else if (!isPositive(expression.type, expression)) {
                Messages.CARDINAL_NEGATIVE.on(expression, expression.type)
            }
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments.forEach {
                if (!it.isResolved()) {
                    Messages.UNRESOLVED_TYPE_ARGUMENT.on(callExpression)
                } else if (!isPositive(it, callExpression)) {
                    Messages.CARDINAL_NEGATIVE.on(callExpression, it)
                }
            }
        }
    }
}
