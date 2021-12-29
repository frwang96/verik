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

    override fun enterIfdef(ctx: SystemVerilogPreprocessorParser.IfdefContext?) {
        BasePreprocessor.preprocessIfdef(ctx!!, preprocessContext)
    }

    override fun enterIfndef(ctx: SystemVerilogPreprocessorParser.IfndefContext?) {
        BasePreprocessor.preprocessIfndef(ctx!!, preprocessContext)
    }

    override fun enterEndif(ctx: SystemVerilogPreprocessorParser.EndifContext?) {
        BasePreprocessor.preprocessEndif(ctx!!, preprocessContext)
    }

    override fun enterUndefAll(ctx: SystemVerilogPreprocessorParser.UndefAllContext?) {
        MacroPreprocessor.preprocessUndefAll(preprocessContext)
    }

    override fun enterUndef(ctx: SystemVerilogPreprocessorParser.UndefContext?) {
        MacroPreprocessor.preprocessUndef(ctx!!, preprocessContext)
    }

    override fun enterDefineDirective(ctx: SystemVerilogPreprocessorParser.DefineDirectiveContext?) {
        MacroPreprocessor.preprocessDefineDirective(ctx!!, preprocessContext)
    }

    override fun enterMacroDirective(ctx: SystemVerilogPreprocessorParser.MacroDirectiveContext?) {
        MacroPreprocessor.preprocessMacroDirective(ctx!!, preprocessContext)
    }

    override fun enterCode(ctx: SystemVerilogPreprocessorParser.CodeContext?) {
        BasePreprocessor.preprocessCode(ctx!!, preprocessContext)
    }
}
