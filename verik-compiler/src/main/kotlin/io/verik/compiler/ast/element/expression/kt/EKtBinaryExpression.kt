/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EAbstractBinaryExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtBinaryExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var left: EExpression,
    override var right: EExpression,
    var kind: KtBinaryOperatorKind
) : EAbstractBinaryExpression() {

    override val serializationKind = SerializationKind.INTERNAL

    init {
        left.parent = this
        right.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtBinaryExpression(this)
    }
}
