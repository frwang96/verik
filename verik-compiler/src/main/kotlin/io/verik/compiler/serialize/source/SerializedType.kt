/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.message.Messages

/**
 * Data class representing a SystemVerilog type that has been serialized. [isVirtual] indicates if the type may be
 * declared with the virtual keyword such as for SystemVerilog interfaces passed as value parameters.
 */
data class SerializedType(
    val base: String,
    val variableDimension: String? = null,
    val isVirtual: Boolean = false
) {

    fun checkNoVariableDimension(element: EElement) {
        if (variableDimension != null) {
            Messages.INTERNAL_ERROR.on(element, "Unexpected variable dimension on type: $variableDimension")
        }
    }
}
