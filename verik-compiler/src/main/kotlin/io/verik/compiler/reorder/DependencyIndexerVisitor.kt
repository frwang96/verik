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

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EAbstractContainerClass
import io.verik.compiler.ast.element.common.EAbstractPackage
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.common.TreeVisitor
import java.lang.Integer.min

class DependencyIndexerVisitor(
    private val dependencyRegistry: DependencyRegistry
) : TreeVisitor() {

    private fun processDependency(element: EElement, reference: EDeclaration) {
        val elementParents = getParents(element)
        val referenceParents = getParents(reference)
        var commonParentIndex: Int? = null
        for (index in 1 until min(elementParents.size, referenceParents.size)) {
            val elementParent = elementParents[index]
            val referenceParent = referenceParents[index]
            if (elementParent != referenceParent) {
                commonParentIndex = index - 1
                break
            }
        }
        if (commonParentIndex != null) {
            val parent = elementParents[commonParentIndex]
            val fromDeclaration = elementParents[commonParentIndex + 1]
            val toDeclaration = referenceParents[commonParentIndex + 1]
            if (isReorderable(parent) && fromDeclaration is EDeclaration && toDeclaration is EDeclaration) {
                val dependency = Dependency(fromDeclaration, toDeclaration, element)
                dependencyRegistry.add(parent, dependency)
            }
        }
    }

    private fun getParents(element: EElement): List<EElement> {
        val parents = ArrayList<EElement>()
        var parent: EElement? = element
        while (parent != null) {
            parents.add(parent)
            parent = parent.parent
        }
        return parents.asReversed()
    }

    private fun isReorderable(parent: EElement): Boolean {
        return parent is EProject ||
            parent is EAbstractPackage ||
            parent is EFile ||
            parent is EAbstractContainerComponent ||
            parent is EAbstractContainerClass
    }

    override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
        super.visitReceiverExpression(receiverExpression)
        if (receiverExpression.receiver == null) {
            val reference = receiverExpression.reference
            if (reference is EDeclaration)
                processDependency(receiverExpression, reference)
        }
    }
}
