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
import io.verik.importer.message.SourceLocation

object BasePreprocessor {

    fun preprocessIfdef(ctx: SystemVerilogPreprocessorParser.IfdefContext, preprocessContext: PreprocessContext) {
        val name = ctx.text.substringAfter("ifdef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro != null)
    }

    fun preprocessIfndef(ctx: SystemVerilogPreprocessorParser.IfndefContext, preprocessContext: PreprocessContext) {
        val name = ctx.text.substringAfter("ifndef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro == null)
    }

    fun preprocessEndif(ctx: SystemVerilogPreprocessorParser.EndifContext, preprocessContext: PreprocessContext) {
        preprocessContext.popEnable(ctx.ENDIF())
    }

    fun preprocessCode(ctx: SystemVerilogPreprocessorParser.CodeContext, preprocessContext: PreprocessContext) {
        if (preprocessContext.isEnable()) {
            val terminalNode = ctx.CODE()
            val preprocessorFragment = PreprocessorFragment(
                SourceLocation.get(terminalNode),
                terminalNode.text,
                true
            )
            preprocessContext.preprocessorFragments.add(preprocessorFragment)
        }
    }
}
