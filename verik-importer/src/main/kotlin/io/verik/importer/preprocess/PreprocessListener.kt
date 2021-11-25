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
import io.verik.importer.common.Fragment
import io.verik.importer.common.FragmentStream
import io.verik.importer.message.SourceLocation
import java.nio.file.Path

class PreprocessListener(
    private val file: Path,
    private val fragmentStream: FragmentStream
) : SystemVerilogPreprocessorParserBaseListener() {

    override fun enterCode(ctx: SystemVerilogPreprocessorParser.CodeContext?) {
        super.enterCode(ctx)
        val symbol = ctx!!.CODE().symbol
        val line = symbol.line
        val column = symbol.charPositionInLine
        val fragment = Fragment(SourceLocation(column, line, file), symbol.text, true)
        fragmentStream.add(fragment)
    }
}
