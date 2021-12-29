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
        val content = getDefineContent(ctx)
        val macro = Macro(listOf(), content)
        preprocessContext.setMacro(name, macro)
    }

    fun preprocessDirectiveDefineArg(
        ctx: PreprocessorParser.DirectiveDefineArgContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DEFINE_MACRO_ARG().text.dropLast(1).trim()
        val argumentsCtx = ctx.arguments()?.argument() ?: listOf()
        val arguments = argumentsCtx.map { it.text }

        val content = getDefineContent(ctx)
        val macro = Macro(arguments, content)
        preprocessContext.setMacro(name, macro)
    }

    fun preprocessDirectiveMacro(
        ctx: PreprocessorParser.DirectiveMacroContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DIRECTIVE_MACRO().text.trim()
        val macro = preprocessContext.getMacro(name)
        if (macro != null) {
            val content = MacroEvaluator.evaluate(macro, listOf())
            preprocessContext.preprocess(content, SourceLocation.get(ctx.BACKTICK()))
        } else {
            Messages.UNDEFINED_MACRO.on(ctx.BACKTICK(), name)
        }
    }

    fun preprocessDirectiveMacroArg(
        ctx: PreprocessorParser.DirectiveMacroArgContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DIRECTIVE_MACRO_ARG().text.dropLast(1).trim()
        val macro = preprocessContext.getMacro(name)
        val arguments = ctx.runArguments().runArgument().map { it.text }
        if (macro != null) {
            val content = MacroEvaluator.evaluate(macro, arguments)
            preprocessContext.preprocess(content, SourceLocation.get(ctx.BACKTICK()))
        } else {
            Messages.UNDEFINED_MACRO.on(ctx.BACKTICK(), name)
        }
    }

    private fun getDefineContent(ctx: ParserRuleContext): String {
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
