/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor

abstract class EKtAbstractFunction : EDeclaration() {

    abstract val valueParameters: List<EKtValueParameter>

    override fun acceptChildren(visitor: Visitor) {
        valueParameters.forEach { it.accept(visitor) }
    }
}
