/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.declaration.common.EAbstractFunction
import io.verik.compiler.common.TreeVisitor

/**
 * Base class for all SystemVerilog function declarations.
 */
abstract class ESvAbstractFunction : EAbstractFunction(), TypeParameterized {

    abstract var valueParameters: ArrayList<ESvValueParameter>
    abstract val isStatic: Boolean

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        typeParameters.forEach { it.accept(visitor) }
        valueParameters.forEach { it.accept(visitor) }
    }
}
