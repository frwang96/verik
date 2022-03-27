/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
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
            if (fromDeclaration is EDeclaration && toDeclaration is EDeclaration) {
                val dependency = Dependency(fromDeclaration, toDeclaration, element)
                if (isValidDependency(parent, element, reference)) {
                    dependencyRegistry.add(parent, dependency)
                }
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

    private fun isValidDependency(parent: EElement, element: EElement, reference: EDeclaration): Boolean {
        val elementParentClass = if (element is ESvClass) element else element.getParentClassOrNull()
        val referenceParentClass = if (reference is ESvClass) reference else reference.getParentClassOrNull()
        val isDifferentParentClass = elementParentClass is ESvClass &&
            referenceParentClass is ESvClass &&
            elementParentClass != referenceParentClass
        return when {
            !isReorderable(parent) -> false
            isDifferentParentClass -> parent is EProject
            reference is EModuleInterface -> false
            else -> true
        }
    }

    private fun isReorderable(parent: EElement): Boolean {
        return parent is EProject ||
            parent is EPackage ||
            parent is EFile ||
            parent is EAbstractContainerComponent ||
            parent is EAbstractContainerClass
    }

    override fun visitSvClass(cls: ESvClass) {
        super.visitSvClass(cls)
        val reference = cls.superType.reference
        if (reference is EDeclaration) processDependency(cls, reference)
    }

    override fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
        super.visitAbstractProperty(abstractProperty)
        val reference = abstractProperty.type.reference
        if (reference is EDeclaration) processDependency(abstractProperty, reference)
    }

    override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
        super.visitReceiverExpression(receiverExpression)
        if (receiverExpression.receiver == null) {
            val reference = receiverExpression.reference
            if (reference is EDeclaration) processDependency(receiverExpression, reference)
        }
    }
}
