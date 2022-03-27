/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class EScopeExpression(
    override val location: SourceLocation,
    val scope: Type,
    val typeParameters: List<ETypeParameter>
) : EExpression() {

    override var type = Target.C_Void.toType()

    override val serializationKind = SerializationKind.EXPRESSION

    override fun accept(visitor: Visitor) {
        visitor.visitScopeExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}
}
