/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.element.declaration.common.EAbstractFunction
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EInitializerBlock(
    override val location: SourceLocation,
    override var body: EBlockExpression
) : EAbstractFunction() {

    override var name = "init"
    override var type = Core.Kt.C_Unit.toType()
    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    init {
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitInitializerBlock(this)
    }
}
