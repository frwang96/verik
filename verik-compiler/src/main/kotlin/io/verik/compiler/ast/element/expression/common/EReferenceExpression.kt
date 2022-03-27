/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EReferenceExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var reference: Declaration,
    override var receiver: EExpression?,
    override var isSafeAccess: Boolean
) : EReceiverExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        receiver?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitReferenceExpression(this)
    }

    companion object {

        fun of(location: SourceLocation, declaration: EDeclaration): EReferenceExpression {
            return EReferenceExpression(
                location,
                declaration.type.copy(),
                declaration,
                null,
                false
            )
        }
    }
}
