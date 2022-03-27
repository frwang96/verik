/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtValueParameter(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var expression: EExpression?,
    var isPrimaryConstructorProperty: Boolean,
    var isMutable: Boolean
) : EAbstractValueParameter() {

    override val endLocation = location

    init {
        expression?.parent = this
    }

    fun fill(
        type: Type,
        annotationEntries: List<AnnotationEntry>,
        expression: EExpression?,
        isPrimaryConstructorProperty: Boolean,
        isMutable: Boolean
    ) {
        expression?.parent = this
        this.type = type
        this.annotationEntries = annotationEntries
        this.expression = expression
        this.isPrimaryConstructorProperty = isPrimaryConstructorProperty
        this.isMutable = isMutable
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtValueParameter(this)
    }
}
