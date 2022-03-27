/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.message.SourceLocation

data class SourceAction(
    val type: SourceActionType,
    val content: String,
    val location: SourceLocation
)
