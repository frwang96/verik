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
import io.verik.importer.antlr.SystemVerilogPreprocessorParserBaseListener
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.tree.TerminalNode

class PreprocessorListener(
    private val preprocessorFragments: ArrayList<PreprocessorFragment>
) : SystemVerilogPreprocessorParserBaseListener() {

    private val preprocessContext = PreprocessContext()

    override fun enterIfndef(ctx: SystemVerilogPreprocessorParser.IfndefContext?) {
        val name = ctx!!.text.substringAfter("ifndef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro == null)
    }

    override fun enterIfdef(ctx: SystemVerilogPreprocessorParser.IfdefContext?) {
        val name = ctx!!.text.substringAfter("ifdef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro != null)
    }

    override fun enterEndif(ctx: SystemVerilogPreprocessorParser.EndifContext?) {
        preprocessContext.popEnable(ctx!!.ENDIF())
    }

    override fun enterDefineDirective(ctx: SystemVerilogPreprocessorParser.DefineDirectiveContext?) {
        val name = ctx!!.DEFINE_MACRO().text
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

    override fun enterMacroDirective(ctx: SystemVerilogPreprocessorParser.MacroDirectiveContext?) {
        val terminalNode = ctx!!.DEFINED_MACRO()
        val name = terminalNode.text
        val macro = preprocessContext.getMacro(name)
        if (macro != null) {
            val preprocessorFragment = PreprocessorFragment(
                SourceLocation.get(terminalNode),
                macro.text,
                false
            )
            preprocessorFragments.add(preprocessorFragment)
        } else {
            Messages.UNDEFINED_MACRO.on(terminalNode, name)
        }
    }

    override fun enterCode(ctx: SystemVerilogPreprocessorParser.CodeContext?) {
        if (preprocessContext.isEnable()) {
            val terminalNode = ctx!!.CODE()
            val preprocessorFragment = PreprocessorFragment(
                SourceLocation.get(terminalNode),
                terminalNode.text,
                true
            )
            preprocessorFragments.add(preprocessorFragment)
        }
    }
}
