/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a Kotlin primary constructor.
 */
class EPrimaryConstructor(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var valueParameters: ArrayList<EKtValueParameter>,
    var superTypeCallExpression: ECallExpression?
) : EKtAbstractFunction() {

    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null
    override var body: EBlockExpression = EBlockExpression.empty(location)

    init {
        valueParameters.forEach { it.parent = this }
        body.parent = this
        superTypeCallExpression?.parent = this
    }

    fun fill(
        type: Type,
        valueParameters: List<EKtValueParameter>,
        superTypeCallExpression: ECallExpression?
    ) {
        valueParameters.forEach { it.parent = this }
        superTypeCallExpression?.parent = this
        this.type = type
        this.valueParameters = ArrayList(valueParameters)
        this.superTypeCallExpression = superTypeCallExpression
    }

    override fun accept(visitor: Visitor) {
        visitor.visitPrimaryConstructor(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        superTypeCallExpression?.accept(visitor)
    }
}
