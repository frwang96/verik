/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.antlr.SystemVerilogPreprocessorLexer
import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Preprocessor for directives that involve macro definitions and macro expansions.
 */
object MacroPreprocessor {

    fun preprocessDirectiveUndefineAll(preprocessContext: PreprocessContext) {
        preprocessContext.removeAllMacros()
    }

    fun preprocessDirectiveUndef(
        ctx: SystemVerilogPreprocessorParser.DirectiveUndefContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.text.substringAfter("undef").trim()
        preprocessContext.removeMacro(name)
    }

    fun preprocessDirectiveDefine(
        ctx: SystemVerilogPreprocessorParser.DirectiveDefineContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DEFINE_MACRO().text.trim()
        val parameters = (ctx.parameters()?.parameter() ?: listOf()).map { it.CONTENT_IDENTIFIER().text }
        val contentEntries = ctx.contents().content().mapNotNull { getContentEntry(it, parameters) }
        val macro = Macro(parameters, contentEntries)
        preprocessContext.setMacro(name, macro)
    }

    fun preprocessDirectiveMacro(
        ctx: SystemVerilogPreprocessorParser.DirectiveMacroContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DIRECTIVE_MACRO().text.trim()
        when (name) {
            "__FILE__" -> {
                val location = SourceLocation.get(ctx.DIRECTIVE_MACRO())
                val preprocessorFragment = PreprocessorFragment(location, "\"${location.path}\"", false)
                preprocessContext.preprocessorFragments.add(preprocessorFragment)
                return
            }
            "__LINE__" -> {
                val location = SourceLocation.get(ctx.DIRECTIVE_MACRO())
                val preprocessorFragment = PreprocessorFragment(location, location.line.toString(), false)
                preprocessContext.preprocessorFragments.add(preprocessorFragment)
                return
            }
        }
        val macro = preprocessContext.getMacro(name)
        if (macro != null) {
            val location = SourceLocation.get(ctx.BACKTICK())
            val content = evaluate(macro, listOf(), location)
            preprocessContext.preprocess(content, location)
        } else {
            Messages.UNDEFINED_MACRO.on(ctx.BACKTICK(), name)
        }
    }

    fun preprocessDirectiveMacroArg(
        ctx: SystemVerilogPreprocessorParser.DirectiveMacroArgContext,
        preprocessContext: PreprocessContext
    ) {
        val name = ctx.DIRECTIVE_MACRO_ARG().text.dropLast(1).trim()
        val macro = preprocessContext.getMacro(name)
        val arguments = ctx.arguments().argument().map { it.text.trim() }
        if (macro != null) {
            val location = SourceLocation.get(ctx.BACKTICK())
            val content = evaluate(macro, arguments, location)
            preprocessContext.preprocess(content, location)
        } else {
            Messages.UNDEFINED_MACRO.on(ctx.BACKTICK(), name)
        }
    }

    private fun getContentEntry(
        ctx: SystemVerilogPreprocessorParser.ContentContext,
        parameters: List<String>,
    ): MacroContentEntry? {
        val child = ctx.children.first() as TerminalNode
        return when (child.symbol.type) {
            SystemVerilogPreprocessorLexer.CONTENT_LINE_CONTINUATION -> TextMacroContentEntry("\n")
            SystemVerilogPreprocessorLexer.CONTENT_CONCAT -> null
            SystemVerilogPreprocessorLexer.CONTENT_ESCAPE_DQ -> TextMacroContentEntry("\"")
            SystemVerilogPreprocessorLexer.CONTENT_ESCAPE_BACK_SLASH_DQ -> TextMacroContentEntry("\\\"")
            SystemVerilogPreprocessorLexer.CONTENT_IDENTIFIER -> {
                val index = parameters.indexOf(ctx.text)
                if (index != -1) {
                    ParameterMacroContentEntry(index)
                } else {
                    TextMacroContentEntry(ctx.text)
                }
            }
            else -> TextMacroContentEntry(ctx.text)
        }
    }

    private fun evaluate(macro: Macro, arguments: List<String>, location: SourceLocation): String {
        if (macro.parameters.size != arguments.size) {
            Messages.INCORRECT_MACRO_ARGUMENTS.on(location, macro.parameters.size, arguments.size)
        }
        val builder = StringBuilder()
        macro.contentEntries.forEach {
            when (it) {
                is TextMacroContentEntry -> builder.append(it.text)
                is ParameterMacroContentEntry -> {
                    if (it.index < arguments.size) {
                        builder.append(arguments[it.index])
                    }
                }
            }
        }
        return builder.toString()
    }
}
