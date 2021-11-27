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

package io.verik.importer.parse

import io.verik.importer.common.PreprocessorFragment
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.misc.Interval

class LexerCharStream(val preprocessorFragments: ArrayList<PreprocessorFragment>) : CharStream {

    private val stream: CharStream

    init {
        val builder = StringBuilder()
        preprocessorFragments.forEach {
            builder.appendLine(it.content)
        }
        stream = CharStreams.fromString(builder.toString())
    }

    fun getLocation(line: Int, charPositionInLine: Int): SourceLocation {
        if (line > preprocessorFragments.size) {
            val location = preprocessorFragments.last().location
            return SourceLocation(location.path, location.line + 1, 1)
        }
        val preprocessorFragment = preprocessorFragments[line - 1]
        return if (preprocessorFragment.isOriginal) {
            val location = preprocessorFragment.location
            return SourceLocation(location.path, location.line, location.column + charPositionInLine)
        } else {
            preprocessorFragment.location
        }
    }

    override fun consume() {
        stream.consume()
    }

    override fun LA(i: Int): Int {
        return stream.LA(i)
    }

    override fun mark(): Int {
        return stream.mark()
    }

    override fun release(marker: Int) {
        stream.release(marker)
    }

    override fun index(): Int {
        return stream.index()
    }

    override fun seek(index: Int) {
        return stream.seek(index)
    }

    override fun size(): Int {
        return stream.size()
    }

    override fun getSourceName(): String {
        return "null"
    }

    override fun getText(p0: Interval?): String {
        return stream.getText(p0)
    }
}
