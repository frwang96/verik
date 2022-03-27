/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtUnaryExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var expression: EExpression,
    val kind: KtUnaryOperatorKind
) : EAbstractContainerExpression() {

    override val serializationKind = SerializationKind.INTERNAL

    init {
        expression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtUnaryExpression(this)
    }
}
