/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.sv.EClockingBlock
import io.verik.compiler.ast.element.declaration.sv.EModulePort

object SerializerUtil {

    fun declarationIsHidden(element: EElement): Boolean {
        return element is EModulePort ||
            element is EClockingBlock ||
            element is EEnumEntry
    }
}
