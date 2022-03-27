/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.target.common.Target

abstract class EAbstractComponent : EAbstractClass() {

    override var superType = Target.C_Void.toType()

    abstract val ports: List<EPort>

    override fun acceptChildren(visitor: TreeVisitor) {
        ports.forEach { it.accept(visitor) }
    }
}
