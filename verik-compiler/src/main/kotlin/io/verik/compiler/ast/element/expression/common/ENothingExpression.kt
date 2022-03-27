/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class ENothingExpression(
    override val location: SourceLocation
) : EExpression() {

    override var type = Core.Kt.C_Nothing.toType()

    override val serializationKind = SerializationKind.EXPRESSION

    override fun accept(visitor: Visitor) {
        visitor.visitNothingExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}
}
