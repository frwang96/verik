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

import io.verik.importer.antlr.PreprocessorParser
import io.verik.importer.message.SourceLocation

object BasePreprocessor {

    fun preprocessDirectiveIfdef(
        ctx: PreprocessorParser.DirectiveIfdefContext,
        preprocessContext: PreprocessContext,
    ) {
        val name = ctx.text.substringAfter("ifdef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro != null)
    }

    fun preprocessDirectiveIfndef(
        ctx: PreprocessorParser.DirectiveIfndefContext,
        preprocessContext: PreprocessContext,
    ) {
        val name = ctx.text.substringAfter("ifndef").trim()
        val macro = preprocessContext.getMacro(name)
        preprocessContext.pushEnable(macro == null)
    }

    fun preprocessDirectiveEndif(
        ctx: PreprocessorParser.DirectiveEndifContext,
        preprocessContext: PreprocessContext,
    ) {
        preprocessContext.popEnable(ctx.DIRECTIVE_ENDIF())
    }

    fun preprocessCode(ctx: PreprocessorParser.CodeContext, preprocessContext: PreprocessContext) {
        if (preprocessContext.isEnable()) {
            val preprocessorFragment = PreprocessorFragment(
                SourceLocation.get(ctx.CODE()),
                ctx.CODE().text,
                true
            )
            preprocessContext.preprocessorFragments.add(preprocessorFragment)
        }
    }
}
