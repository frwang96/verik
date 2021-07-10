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

import io.verik.compiler.ast.element.common.VkDeclaration
import io.verik.compiler.ast.element.common.VkDeclarationContainer
import io.verik.compiler.ast.element.common.VkExpression
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

class DeclarationReplacer(val projectContext: ProjectContext) {

    private val replacementMap = HashMap<VkDeclaration, VkDeclaration>()

    fun replace(oldDeclaration: VkDeclaration, newDeclaration: VkDeclaration) {
        val parent = oldDeclaration.parent
        if (parent is VkDeclarationContainer)
            parent.replaceChild(oldDeclaration, newDeclaration)
        else
            m.error("Could not replace declaration $oldDeclaration", oldDeclaration)
        replacementMap[oldDeclaration] = newDeclaration
    }

    fun updateReferences() {
        val referenceUpdateVisitor = ReferenceUpdateVisitor(replacementMap)
        projectContext.vkFiles.forEach {
            it.accept(referenceUpdateVisitor)
        }
        replacementMap.clear()
    }

    class ReferenceUpdateVisitor(private val replacementMap: Map<VkDeclaration, VkDeclaration>) : TreeVisitor() {

        override fun visitExpression(expression: VkExpression) {
            super.visitExpression(expression)
            if (expression is Reference) {
                val reference = replacementMap[expression.reference]
                if (reference != null)
                    expression.reference = reference
            }
        }
    }
}