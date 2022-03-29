/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a type parameter declaration. The upper bound of the type parameter is given by [type].
 */
class ETypeParameter(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type
) : EClassifier() {

    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    fun fill(type: Type) {
        this.type = type
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitTypeParameter(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}
}
