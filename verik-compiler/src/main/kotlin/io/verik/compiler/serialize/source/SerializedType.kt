/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.message.Messages

class SerializedType(
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
