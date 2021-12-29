/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.importer.preprocess

import io.verik.importer.antlr.SystemVerilogPreprocessorLexer
import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.tree.TerminalNode

object MacroPreprocessor {

    fun preprocessUndefAll(preprocessContext: PreprocessContext) {
        preprocessContext.removeAllMacros()
    }

    fun preprocessUndef(
        ctx: SystemVerilogPreprocessorParser.UndefContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.text.substringAfter("undef").trim()
        preprocessContext.removeMacro(name)
    }

    fun preprocessDefineDirective(
        ctx: SystemVerilogPreprocessorParser.DefineDirectiveContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DEFINE_MACRO().text
        val builder = StringBuilder()
        ctx.children.forEach {
            if (it is TerminalNode) {
                when (it.symbol.type) {
                    SystemVerilogPreprocessorLexer.TEXT ->
                        builder.append(it.text)
                    SystemVerilogPreprocessorLexer.TEXT_LINE_CONTINUATION ->
                        builder.appendLine()
                }
            }
        }
        val macro = Macro(listOf(), builder.toString())
        preprocessContext.setMacro(name, macro)
    }

    fun preprocessMacroDirective(
        ctx: SystemVerilogPreprocessorParser.MacroDirectiveContext,
        preprocessContext: PreprocessContext
    ) {
        val terminalNode = ctx.DEFINED_MACRO()
        val name = terminalNode.text
        val macro = preprocessContext.getMacro(name)
        if (macro != null) {
            val preprocessorFragment = PreprocessorFragment(
                SourceLocation.get(terminalNode),
                macro.text,
                false
            )
            preprocessContext.preprocessorFragments.add(preprocessorFragment)
        } else {
            Messages.UNDEFINED_MACRO.on(terminalNode, name)
        }
    }
}
