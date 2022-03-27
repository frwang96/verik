/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.PortKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EPort(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    val kind: PortKind
) : EAbstractValueParameter() {

    override val endLocation = location

    override var expression: EExpression? = null

    override fun accept(visitor: Visitor) {
        visitor.visitPort(this)
    }
}
