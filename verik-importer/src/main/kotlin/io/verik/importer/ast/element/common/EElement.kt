/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.common

import io.verik.importer.ast.common.DeclarationContainer
import io.verik.importer.ast.common.DescriptorContainer
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.Visitor
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation

/**
 * Abstract base element class with a [location] and a [parent] element.
 */
abstract class EElement {

    abstract val location: SourceLocation

    var parent: EElement? = null

    fun parentNotNull(): EElement {
        return parent
            ?: Messages.INTERNAL_ERROR.on(this, "Parent element of $this should not be null")
    }

    inline fun <reified E : EElement> cast(): E {
        return when (this) {
            is E -> this
            else -> {
                Messages.INTERNAL_ERROR.on(this, "Could not cast element: Expected ${E::class.simpleName} actual $this")
            }
        }
    }

    fun replaceChildAsDeclarationContainer(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        if (this is DeclarationContainer) {
            if (!replaceChild(oldDeclaration, newDeclaration)) {
                Messages.INTERNAL_ERROR.on(oldDeclaration, "Could not find $oldDeclaration in $this")
            }
        } else {
            Messages.INTERNAL_ERROR.on(oldDeclaration, "Could not replace $oldDeclaration in $this")
        }
    }

    fun replaceChildAsDescriptorContainer(oldDescriptor: EDescriptor, newDescriptor: EDescriptor) {
        if (this is DescriptorContainer) {
            if (!replaceChild(oldDescriptor, newDescriptor)) {
                Messages.INTERNAL_ERROR.on(oldDescriptor, "Could not find $oldDescriptor in $this")
            }
        } else {
            Messages.INTERNAL_ERROR.on(oldDescriptor, "Could not replace $oldDescriptor in $this")
        }
    }

    abstract fun accept(visitor: Visitor)

    abstract fun acceptChildren(visitor: Visitor)

    override fun toString(): String {
        return "${this::class.simpleName}"
    }
}
