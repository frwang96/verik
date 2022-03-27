/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

data class SourceActionLine(
    val indents: Int,
    val sourceActions: ArrayList<SourceAction>
)
