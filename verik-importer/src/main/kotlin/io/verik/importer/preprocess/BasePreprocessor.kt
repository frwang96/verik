/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.common.TextFile
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Preprocessor for directives that do not involve macros definitions and macro expansions.
 */
object BasePreprocessor {

    private val INCLUDE_PATTERN = Regex("\\s*\"(.+)\".*")

    fun preprocessDirectiveIfdef(
        ctx: SystemVerilogPreprocessorParser.DirectiveIfdefContext,
        preprocessContext: PreprocessContext,
    ) {
        val name = ctx.text.substringAfter("ifdef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro != null)
    }

    fun preprocessDirectiveIfndef(
        ctx: SystemVerilogPreprocessorParser.DirectiveIfndefContext,
        preprocessContext: PreprocessContext,
    ) {
        val name = ctx.text.substringAfter("ifndef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro == null)
    }

    fun preprocessDirectiveElse(
        ctx: SystemVerilogPreprocessorParser.DirectiveElseContext,
        preprocessContext: PreprocessContext
    ) {
        val enable = preprocessContext.popEnable(ctx.DIRECTIVE_ELSE())
        preprocessContext.pushEnable(!enable)
    }

    fun preprocessDirectiveEndif(
        ctx: SystemVerilogPreprocessorParser.DirectiveEndifContext,
        preprocessContext: PreprocessContext,
    ) {
        preprocessContext.popEnable(ctx.DIRECTIVE_ENDIF())
    }

    fun preprocessDirectiveInclude(
        ctx: SystemVerilogPreprocessorParser.DirectiveIncludeContext,
        preprocessContext: PreprocessContext
    ) {
        val match = INCLUDE_PATTERN.matchEntire(ctx.contents().text)
        if (match != null) {
            val path = Paths.get(match.destructured.component1())
            val location = SourceLocation.get(ctx.DIRECTIVE_INCLUDE())
            val textFile = getIncludedTextFile(path, location, preprocessContext)
            if (textFile != null) {
                preprocessContext.preprocess(textFile)
            } else {
                Messages.INCLUDED_FILE_NOT_FOUND.on(location, path)
            }
        }
    }

    fun preprocessCode(ctx: SystemVerilogPreprocessorParser.CodeContext, preprocessContext: PreprocessContext) {
        val preprocessorFragment = PreprocessorFragment(
            SourceLocation.get(ctx.CODE()),
            ctx.CODE().text,
            true
        )
        preprocessContext.preprocessorFragments.add(preprocessorFragment)
    }

    private fun getIncludedTextFile(
        path: Path,
        location: SourceLocation,
        preprocessContext: PreprocessContext
    ): TextFile? {
        val includedTextFiles = preprocessContext.includedTextFiles
        if (path.isAbsolute) {
            return includedTextFiles[path]
        }
        includedTextFiles[location.path.resolveSibling(path)]?.let { return it }
        preprocessContext.includeDirs.forEach {
            val textFile = includedTextFiles[it.resolve(path)]
            if (textFile != null) {
                return textFile
            }
        }
        return null
    }
}
