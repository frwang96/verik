/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

/**
 * A virtual line consisting of a list of [sourceActions]. Each virtual line can be serialized as one or more physical
 * lines to accommodate line wrapping.
 */
data class SourceActionLine(
    val indents: Int,
    val sourceActions: ArrayList<SourceAction>
)
