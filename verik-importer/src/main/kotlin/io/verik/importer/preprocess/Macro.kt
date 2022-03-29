/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

/**
 * A macro with [parameters] and [contentEntries].
 */
class Macro(
    val parameters: List<String>,
    val contentEntries: List<MacroContentEntry>
)
