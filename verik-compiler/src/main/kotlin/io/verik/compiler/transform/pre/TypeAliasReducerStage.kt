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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object TypeAliasReducerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeAliasReducerVisitor)
    }

    object TypeAliasReducerVisitor : TreeVisitor() {

        // TODO handle type alias with type parameters
        private fun reduce(type: Type, element: EElement) {
            var reference = type.reference
            while (reference is ETypeAlias) {
                type.reference = reference.type.reference
                type.arguments = ArrayList(reference.type.arguments.map { it.copy() })
                reference = type.reference
            }
            type.arguments.forEach { reduce(it, element) }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            reduce(typedElement.type, typedElement)
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            callExpression.typeArguments.forEach { reduce(it, callExpression) }
        }
    }
}
