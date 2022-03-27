/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.element.expression.common.EStringEntryExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class EInjectedExpression(
    override val location: SourceLocation,
    override val entries: List<StringEntry>
) : EStringEntryExpression() {

    override var type = Target.C_Void.toType()

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        entries.forEach {
            if (it is ExpressionStringEntry) {
                it.expression.parent = this
            }
        }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitInjectedExpression(this)
    }
}
