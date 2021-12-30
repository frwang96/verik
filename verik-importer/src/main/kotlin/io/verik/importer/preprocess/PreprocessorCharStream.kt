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

import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.misc.Interval

class PreprocessorCharStream(
    val location: SourceLocation,
    val isOriginal: Boolean,
    content: String,
) : CharStream {

    private val stream = CharStreams.fromString(content)

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
