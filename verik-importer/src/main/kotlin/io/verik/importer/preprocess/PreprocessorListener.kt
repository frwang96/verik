/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.antlr.SystemVerilogPreprocessorParserBaseListener

/**
 * Listener that traverses the preprocessor AST to build preprocessor fragments.
 */
class PreprocessorListener(
    private val preprocessContext: PreprocessContext
) : SystemVerilogPreprocessorParserBaseListener() {

    override fun enterDirectiveIfdef(ctx: SystemVerilogPreprocessorParser.DirectiveIfdefContext?) {
        BasePreprocessor.preprocessDirectiveIfdef(ctx!!, preprocessContext)
    }

    override fun enterDirectiveIfndef(ctx: SystemVerilogPreprocessorParser.DirectiveIfndefContext?) {
        BasePreprocessor.preprocessDirectiveIfndef(ctx!!, preprocessContext)
    }

    override fun enterDirectiveElse(ctx: SystemVerilogPreprocessorParser.DirectiveElseContext?) {
        BasePreprocessor.preprocessDirectiveElse(ctx!!, preprocessContext)
    }

    override fun enterDirectiveEndif(ctx: SystemVerilogPreprocessorParser.DirectiveEndifContext?) {
        BasePreprocessor.preprocessDirectiveEndif(ctx!!, preprocessContext)
    }

    override fun enterDirectiveInclude(ctx: SystemVerilogPreprocessorParser.DirectiveIncludeContext?) {
        if (preprocessContext.isEnable()) {
            BasePreprocessor.preprocessDirectiveInclude(ctx!!, preprocessContext)
        }
    }

    override fun enterDirectiveUndefineAll(ctx: SystemVerilogPreprocessorParser.DirectiveUndefineAllContext?) {
        if (preprocessContext.isEnable()) {
            MacroPreprocessor.preprocessDirectiveUndefineAll(preprocessContext)
        }
    }

    override fun enterDirectiveUndef(ctx: SystemVerilogPreprocessorParser.DirectiveUndefContext?) {
        if (preprocessContext.isEnable()) {
            MacroPreprocessor.preprocessDirectiveUndef(ctx!!, preprocessContext)
        }
    }

    override fun enterDirectiveDefine(ctx: SystemVerilogPreprocessorParser.DirectiveDefineContext?) {
        if (preprocessContext.isEnable()) {
            MacroPreprocessor.preprocessDirectiveDefine(ctx!!, preprocessContext)
        }
    }

    override fun enterDirectiveMacro(ctx: SystemVerilogPreprocessorParser.DirectiveMacroContext?) {
        if (preprocessContext.isEnable()) {
            MacroPreprocessor.preprocessDirectiveMacro(ctx!!, preprocessContext)
        }
    }

    override fun enterDirectiveMacroArg(ctx: SystemVerilogPreprocessorParser.DirectiveMacroArgContext?) {
        if (preprocessContext.isEnable()) {
            MacroPreprocessor.preprocessDirectiveMacroArg(ctx!!, preprocessContext)
        }
    }

    override fun enterCode(ctx: SystemVerilogPreprocessorParser.CodeContext?) {
        if (preprocessContext.isEnable()) {
            BasePreprocessor.preprocessCode(ctx!!, preprocessContext)
        }
    }
}
