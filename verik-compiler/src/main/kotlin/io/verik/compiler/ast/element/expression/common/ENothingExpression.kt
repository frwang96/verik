/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

/**
 * Expression that represents nothing, such as in the case of component instantiations with ports that are not
 * connected. It may also be used as a null object pattern to replace erroneous expressions.
 */
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
