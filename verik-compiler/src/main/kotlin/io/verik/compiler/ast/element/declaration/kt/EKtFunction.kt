/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a Kotlin function declaration.
 */
class EKtFunction(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override var body: EBlockExpression,
    override var valueParameters: ArrayList<EKtValueParameter>,
    override var typeParameters: ArrayList<ETypeParameter>,
    var overriddenFunction: Declaration?
) : EKtAbstractFunction(), TypeParameterized {

    init {
        body.parent = this
        valueParameters.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
    }

    @Suppress("DuplicatedCode")
    fun fill(
        type: Type,
        annotationEntries: List<AnnotationEntry>,
        documentationLines: List<String>?,
        body: EBlockExpression,
        valueParameters: List<EKtValueParameter>,
        typeParameters: List<ETypeParameter>,
        overriddenFunction: Declaration?
    ) {
        body.parent = this
        valueParameters.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
        this.type = type
        this.annotationEntries = annotationEntries
        this.documentationLines = documentationLines
        this.body = body
        this.valueParameters = ArrayList(valueParameters)
        this.typeParameters = ArrayList(typeParameters)
        this.overriddenFunction = overriddenFunction
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtFunction(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        typeParameters.forEach { it.accept(visitor) }
    }
}
