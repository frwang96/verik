/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.element.expression.common.EStringEntryExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EStringTemplateExpression(
    override val location: SourceLocation,
    override val entries: List<StringEntry>
) : EStringEntryExpression() {

    override val serializationKind = SerializationKind.INTERNAL

    init {
        entries.forEach {
            if (it is ExpressionStringEntry)
                it.expression.parent = this
        }
    }

    override var type = Core.Kt.C_String.toType()

    override fun accept(visitor: Visitor) {
        visitor.visitStringTemplateExpression(this)
    }
}
