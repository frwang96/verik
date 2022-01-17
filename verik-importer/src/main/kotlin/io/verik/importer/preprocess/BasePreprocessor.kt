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

import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.common.TextFile
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import java.nio.file.Path
import java.nio.file.Paths

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
        preprocessorContext: PreprocessContext
    ) {
        val match = INCLUDE_PATTERN.matchEntire(ctx.contents().text)
        if (match != null) {
            val path = Paths.get(match.destructured.component1())
            val location = SourceLocation.get(ctx.DIRECTIVE_INCLUDE())
            val textFile = getIncludedTextFile(path, location, preprocessorContext)
            if (textFile != null) {
                preprocessorContext.preprocess(textFile)
            } else {
                Messages.INCLUDED_FILE_NOT_FOUND.on(location, path)
            }
        }
    }

    fun preprocessCode(ctx: SystemVerilogPreprocessorParser.CodeContext, preprocessContext: PreprocessContext) {
        if (preprocessContext.isEnable()) {
            val preprocessorFragment = PreprocessorFragment(
                SourceLocation.get(ctx.CODE()),
                ctx.CODE().text,
                true
            )
            preprocessContext.preprocessorFragments.add(preprocessorFragment)
        }
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
