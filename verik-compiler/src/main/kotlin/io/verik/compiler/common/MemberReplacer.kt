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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.EAbstractValueParameter
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.ElementContainer
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.ast.property.Type
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

class MemberReplacer(val projectContext: ProjectContext) {

    private val replacementMap = HashMap<Declaration, Declaration>()

    fun replace(oldElement: EElement, newElement: EElement) {
        val parent = oldElement.parentNotNull()
        if (parent is ElementContainer)
            parent.replaceChild(oldElement, newElement)
        else
            Messages.INTERNAL_ERROR.on(oldElement, "Could not replace $oldElement in $parent")

        if (oldElement !is Declaration)
            Messages.INTERNAL_ERROR.on(oldElement, "Declaration expected but got: $oldElement")
        else if (newElement !is Declaration)
            Messages.INTERNAL_ERROR.on(newElement, "Declaration expected but got: $newElement")
        else
            replacementMap[oldElement] = newElement
    }

    fun updateReferences() {
        val referenceUpdateVisitor = ReferenceUpdateVisitor(replacementMap)
        projectContext.project.accept(referenceUpdateVisitor)
        replacementMap.clear()
    }

    class ReferenceUpdateVisitor(private val replacementMap: Map<Declaration, Declaration>) : TreeVisitor() {

        private fun updateTypeReferences(type: Type) {
            val reference = replacementMap[type.reference]
            if (reference != null)
                type.reference = reference
            type.arguments.forEach { updateTypeReferences(it) }
        }

        override fun visitAbstractValueParameter(abstractValueParameter: EAbstractValueParameter) {
            super.visitAbstractValueParameter(abstractValueParameter)
            updateTypeReferences(abstractValueParameter.type)
        }

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            updateTypeReferences(expression.type)
            if (expression is Reference) {
                val reference = replacementMap[expression.reference]
                if (reference != null)
                    expression.reference = reference
            }
        }
    }
}
