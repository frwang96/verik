/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a Kotlin companion object.
 */
class ECompanionObject(
    override val location: SourceLocation,
    override var type: Type,
    override var declarations: ArrayList<EDeclaration>
) : EAbstractContainerClass() {

    override val bodyStartLocation = location
    override val bodyEndLocation = location
    override var name = "Companion"
    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null
    override var superType = Core.Kt.C_Any.toType()
    override var typeParameters: ArrayList<ETypeParameter> = ArrayList()

    init {
        declarations.forEach { it.parent = this }
    }

    fun fill(type: Type, declarations: List<EDeclaration>) {
        declarations.forEach { it.parent = this }
        this.type = type
        this.declarations = ArrayList(declarations)
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCompanionObject(this)
    }
}
