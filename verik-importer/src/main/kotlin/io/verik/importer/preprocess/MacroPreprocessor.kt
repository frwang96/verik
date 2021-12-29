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

import io.verik.importer.antlr.preprocess.PreprocessorLexer
import io.verik.importer.antlr.preprocess.PreprocessorParser
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

object MacroPreprocessor {

    fun preprocessDirectiveUndefineAll(preprocessContext: PreprocessContext) {
        preprocessContext.removeAllMacros()
    }

    fun preprocessDirectiveUndef(
        ctx: PreprocessorParser.DirectiveUndefContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.text.substringAfter("undef").trim()
        preprocessContext.removeMacro(name)
    }

    fun preprocessDirectiveDefine(
        ctx: PreprocessorParser.DirectiveDefineContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DEFINE_MACRO().text.trim()
        val text = getDefineText(ctx)
        val macro = Macro(listOf(), text)
        preprocessContext.setMacro(name, macro)
    }

    fun preprocessDirectiveDefineArg(
        ctx: PreprocessorParser.DirectiveDefineArgContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DEFINE_MACRO_ARG().text.trim().dropLast(1)
        val argumentsCtx = ctx.arguments()?.argument() ?: listOf()
        val arguments = argumentsCtx.map { it.text }

        val text = getDefineText(ctx)
        val macro = Macro(arguments, text)
        preprocessContext.setMacro(name, macro)
    }

    fun preprocessDirectiveMacro(
        ctx: PreprocessorParser.DirectiveMacroContext,
        preprocessContext: PreprocessContext
    ) {
        val terminalNode = ctx.DIRECTIVE_MACRO()
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

    private fun getDefineText(ctx: ParserRuleContext): String {
        val builder = StringBuilder()
        ctx.children.forEach {
            if (it is TerminalNode) {
                when (it.symbol.type) {
                    PreprocessorLexer.TEXT ->
                        builder.append(it.text)
                    PreprocessorLexer.TEXT_LINE_CONTINUATION ->
                        builder.appendLine()
                }
            }
        }
        return builder.toString()
    }
}
