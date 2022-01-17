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
import io.verik.importer.antlr.SystemVerilogPreprocessorParserBaseListener

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
        BasePreprocessor.preprocessDirectiveInclude(ctx!!, preprocessContext)
    }

    override fun enterDirectiveUndefineAll(ctx: SystemVerilogPreprocessorParser.DirectiveUndefineAllContext?) {
        MacroPreprocessor.preprocessDirectiveUndefineAll(preprocessContext)
    }

    override fun enterDirectiveUndef(ctx: SystemVerilogPreprocessorParser.DirectiveUndefContext?) {
        MacroPreprocessor.preprocessDirectiveUndef(ctx!!, preprocessContext)
    }

    override fun enterDirectiveDefine(ctx: SystemVerilogPreprocessorParser.DirectiveDefineContext?) {
        MacroPreprocessor.preprocessDirectiveDefine(ctx!!, preprocessContext)
    }

    override fun enterDirectiveMacro(ctx: SystemVerilogPreprocessorParser.DirectiveMacroContext?) {
        MacroPreprocessor.preprocessDirectiveMacro(ctx!!, preprocessContext)
    }

    override fun enterDirectiveMacroArg(ctx: SystemVerilogPreprocessorParser.DirectiveMacroArgContext?) {
        MacroPreprocessor.preprocessDirectiveMacroArg(ctx!!, preprocessContext)
    }

    override fun enterCode(ctx: SystemVerilogPreprocessorParser.CodeContext?) {
        BasePreprocessor.preprocessCode(ctx!!, preprocessContext)
    }
}
