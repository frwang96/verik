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
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.TypeVisitor
import io.verik.compiler.core.common.CoreCardinalConstantDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object TypeChecker : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val cardinalResolvedTypeVisitor = CardinalResolvedTypeVisitor()
        val typeCheckerExpressionVisitor = TypeCheckerExpressionVisitor(cardinalResolvedTypeVisitor)
        projectContext.files.forEach { it.accept(typeCheckerExpressionVisitor) }
    }

    class TypeCheckerExpressionVisitor(
        private val cardinalResolvedTypeVisitor: CardinalResolvedTypeVisitor
    ) : TreeVisitor() {

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            if (!cardinalResolvedTypeVisitor.isResolved(expression.type))
               m.error("Type of ${expression::class.simpleName} has not been resolved: ${expression.type}", expression)
        }
    }

    class CardinalResolvedTypeVisitor : TypeVisitor() {

        private var isResolved = true

        fun isResolved(type: Type): Boolean {
            isResolved = true
            type.accept(this)
            return isResolved
        }

        override fun visit(type: Type) {
            super.visit(type)
            if (type.isCardinalType()) {
                if (type.reference !is CoreCardinalConstantDeclaration)
                    isResolved = false
            }
        }
    }
}