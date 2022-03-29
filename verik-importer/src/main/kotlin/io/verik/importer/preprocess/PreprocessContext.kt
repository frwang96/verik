/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.common.TextFile
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.antlr.v4.runtime.tree.TerminalNode
import java.nio.file.Path

/**
 * Context for building preprocessor fragments from text.
 */
class PreprocessContext(
    val preprocessorFragments: ArrayList<PreprocessorFragment>,
    val includedTextFiles: HashMap<Path, TextFile>,
    val includeDirs: List<Path>
) {

    private val preprocessorListener = PreprocessorListener(this)
    private val macros = HashMap<String, Macro>()
    private val enableStack = ArrayList<Boolean>()

    fun preprocess(textFile: TextFile) {
        val parseTree = PreprocessorParser.parse(textFile)
        ParseTreeWalker.DEFAULT.walk(preprocessorListener, parseTree)
    }

    fun preprocess(content: String, location: SourceLocation) {
        val parseTree = PreprocessorParser.parse(content, location)
        ParseTreeWalker.DEFAULT.walk(preprocessorListener, parseTree)
    }

    fun setMacro(name: String, macro: Macro) {
        macros[name] = macro
    }

    fun removeAllMacros() {
        macros.clear()
    }

    fun removeMacro(name: String) {
        macros.remove(name)
    }

    fun getMacro(name: String): Macro? {
        return macros[name]
    }

    fun pushEnable(enable: Boolean) {
        enableStack.add(enable)
    }

    fun popEnable(terminalNode: TerminalNode): Boolean {
        val enable = isEnable()
        if (enableStack.isEmpty()) {
            Messages.UNMATCHED_DIRECTIVE.on(terminalNode)
        } else {
            enableStack.removeLast()
        }
        return enable
    }

    fun isEnable(): Boolean {
        return enableStack.lastOrNull() ?: true
    }
}
