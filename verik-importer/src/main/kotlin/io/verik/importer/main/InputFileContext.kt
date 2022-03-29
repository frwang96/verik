/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.common.TextFile
import io.verik.importer.preprocess.PreprocessorFragment
import org.antlr.v4.runtime.TokenStream

/**
 * Context that stores an input [textFile], the preprocessed [preprocessorFragments], the lexed [parserTokenStream],
 * and the parsed [ruleContext].
 */
class InputFileContext(val textFile: TextFile) {

    lateinit var preprocessorFragments: ArrayList<PreprocessorFragment>
    lateinit var parserTokenStream: TokenStream
    lateinit var ruleContext: SystemVerilogParser.SourceTextContext
}
