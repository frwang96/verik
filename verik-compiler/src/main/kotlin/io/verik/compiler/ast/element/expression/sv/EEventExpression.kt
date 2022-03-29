/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.element.expression.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.EdgeKind
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

/**
 * Element that represents a SystemVerilog event expression. Event expressions trigger on an edge of [expression].
 * The edge kind is specified by [kind].
 */
class EEventExpression(
    override val location: SourceLocation,
    override var expression: EExpression,
    var kind: EdgeKind
) : EAbstractContainerExpression() {

    override var type = Target.C_Event.toType()

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        expression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitEventExpression(this)
    }
}
