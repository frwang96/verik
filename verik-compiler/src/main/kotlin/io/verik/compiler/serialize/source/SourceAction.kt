/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.message.SourceLocation

/**
 * A source action that is used to format the contexts and whitespace of a source file.
 */
data class SourceAction(
    val type: SourceActionType,
    val content: String,
    val location: SourceLocation
)
