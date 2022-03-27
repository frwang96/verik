/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

sealed class MacroContentEntry

class TextMacroContentEntry(val text: String) : MacroContentEntry()

class ParameterMacroContentEntry(val index: Int) : MacroContentEntry()
