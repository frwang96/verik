/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.element.declaration.common.EAbstractFunction
import io.verik.compiler.common.TreeVisitor

/**
 * Base class for all Kotlin function declarations.
 */
abstract class EKtAbstractFunction : EAbstractFunction() {

    abstract var valueParameters: ArrayList<EKtValueParameter>

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        valueParameters.forEach { it.accept(visitor) }
    }
}
