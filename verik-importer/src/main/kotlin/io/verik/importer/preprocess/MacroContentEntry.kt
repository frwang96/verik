/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

/**
 * Content entry of a macro. It can either hold text or a macro parameter.
 */
sealed class MacroContentEntry

/**
 * Macro content entry that holds [text].
 */
class TextMacroContentEntry(val text: String) : MacroContentEntry()

/**
 * Macro content entry that holds the parameter with [index].
 */
class ParameterMacroContentEntry(val index: Int) : MacroContentEntry()
