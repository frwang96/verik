/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration

class Dependency(
    val fromDeclaration: EDeclaration,
    val toDeclaration: EDeclaration,
    val element: EElement
) {

    override fun equals(other: Any?): Boolean {
        return other is Dependency &&
            other.fromDeclaration == fromDeclaration &&
            other.toDeclaration == toDeclaration
    }

    override fun hashCode(): Int {
        var result = fromDeclaration.hashCode()
        result = 31 * result + toDeclaration.hashCode()
        return result
    }

    override fun toString(): String {
        return "From ${fromDeclaration.name} to ${toDeclaration.name}"
    }
}
