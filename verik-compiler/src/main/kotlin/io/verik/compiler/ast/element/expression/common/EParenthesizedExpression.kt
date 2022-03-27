/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EParenthesizedExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var expression: EExpression
) : EAbstractContainerExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        expression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitParenthesizedExpression(this)
    }
}
