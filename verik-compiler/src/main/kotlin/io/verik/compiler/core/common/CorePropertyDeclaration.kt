/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression

/**
 * A core declaration that represents a property. The property transforms according to [transform].
 */
abstract class CorePropertyDeclaration(
    final override val parent: CoreDeclaration,
    final override var name: String
) : CoreDeclaration {

    override val signature: String = "val $name"

    abstract fun transform(referenceExpression: EReferenceExpression): EExpression
}
