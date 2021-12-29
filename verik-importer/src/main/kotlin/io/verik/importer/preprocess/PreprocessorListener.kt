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
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.tree.TerminalNode

class PreprocessorListener(
    private val preprocessorFragments: ArrayList<PreprocessorFragment>
) : SystemVerilogPreprocessorParserBaseListener() {

    private val preprocessContext = PreprocessContext()

    override fun enterDirective(ctx: SystemVerilogPreprocessorParser.DirectiveContext?) {
        if (ctx != null) {
            ctx.IFNDEF()?.let { processIfndef(it) }
            ctx.IFDEF()?.let { processIfdef(it) }
            ctx.ENDIF()?.let { processEndif(it) }
        }
    }

    override fun enterCode(ctx: SystemVerilogPreprocessorParser.CodeContext?) {
        super.enterCode(ctx)
        if (preprocessContext.isEnable()) {
            val terminalNode = ctx!!.CODE()
            val preprocessorFragment = PreprocessorFragment(
                SourceLocation.get(terminalNode),
                terminalNode.text,
                true
            )
            preprocessorFragments.add(preprocessorFragment)
        }
    }

    private fun processIfndef(terminalNode: TerminalNode) {
        val identifier = terminalNode.text.substringAfter("ifndef").trim()
        preprocessContext.pushEnable(!preprocessContext.isDefined(identifier))
    }

    private fun processIfdef(terminalNode: TerminalNode) {
        val identifier = terminalNode.text.substringAfter("ifdef").trim()
        preprocessContext.pushEnable(preprocessContext.isDefined(identifier))
    }

    private fun processEndif(terminalNode: TerminalNode) {
        preprocessContext.popEnable(terminalNode)
    }
}
