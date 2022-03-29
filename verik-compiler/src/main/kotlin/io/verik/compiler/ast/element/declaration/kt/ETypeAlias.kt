/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.declaration.common.EClassifier
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a Kotlin type alias.
 */
class ETypeAlias(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var typeParameters: ArrayList<ETypeParameter>
) : EClassifier(), TypeParameterized {

    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    init {
        typeParameters.forEach { it.parent = this }
    }

    fun fill(type: Type, typeParameters: List<ETypeParameter>) {
        typeParameters.forEach { it.parent = this }
        this.type = type
        this.typeParameters = ArrayList(typeParameters)
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitTypeAlias(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        typeParameters.forEach { it.accept(visitor) }
    }
}
