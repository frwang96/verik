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

import io.verik.importer.antlr.SystemVerilogPreprocessorLexer
import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.common.ImporterStage
import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree

object PreprocessParserStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        importerContext.importedParseTrees = importerContext.importedTextFiles.map { parse(it) }
    }

    private fun parse(textFile: TextFile): ParseTree {
        val charStream = CharStreams.fromString(textFile.content)
        val lexer = SystemVerilogPreprocessorLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = SystemVerilogPreprocessorParser(tokenStream)
        return parser.eval()
    }
}
