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

import io.verik.importer.antlr.preprocess.PreprocessorParser
import io.verik.importer.antlr.preprocess.PreprocessorParserBaseListener

class PreprocessorListener(
    private val preprocessContext: PreprocessContext
) : PreprocessorParserBaseListener() {

    override fun enterDirectiveIfdef(ctx: PreprocessorParser.DirectiveIfdefContext?) {
        BasePreprocessor.preprocessDirectiveIfdef(ctx!!, preprocessContext)
    }

    override fun enterDirectiveIfndef(ctx: PreprocessorParser.DirectiveIfndefContext?) {
        BasePreprocessor.preprocessDirectiveIfndef(ctx!!, preprocessContext)
    }

    override fun enterDirectiveEndif(ctx: PreprocessorParser.DirectiveEndifContext?) {
        BasePreprocessor.preprocessDirectiveEndif(ctx!!, preprocessContext)
    }

    override fun enterDirectiveUndefineAll(ctx: PreprocessorParser.DirectiveUndefineAllContext?) {
        MacroPreprocessor.preprocessDirectiveUndefineAll(preprocessContext)
    }

    override fun enterDirectiveUndef(ctx: PreprocessorParser.DirectiveUndefContext?) {
        MacroPreprocessor.preprocessDirectiveUndef(ctx!!, preprocessContext)
    }

    override fun enterDirectiveDefine(ctx: PreprocessorParser.DirectiveDefineContext?) {
        MacroPreprocessor.preprocessDirectiveDefine(ctx!!, preprocessContext)
    }

    override fun enterDirectiveDefineParam(ctx: PreprocessorParser.DirectiveDefineParamContext?) {
        MacroPreprocessor.preprocessDirectiveDefineParam(ctx!!, preprocessContext)
    }

    override fun enterDirectiveMacro(ctx: PreprocessorParser.DirectiveMacroContext?) {
        MacroPreprocessor.preprocessDirectiveMacro(ctx!!, preprocessContext)
    }

    override fun enterDirectiveMacroArg(ctx: PreprocessorParser.DirectiveMacroArgContext?) {
        MacroPreprocessor.preprocessDirectiveMacroArg(ctx!!, preprocessContext)
    }

    override fun enterCode(ctx: PreprocessorParser.CodeContext?) {
        BasePreprocessor.preprocessCode(ctx!!, preprocessContext)
    }
}
