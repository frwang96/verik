/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.misc.Interval

/**
 * Character stream that keeps track of the [location] in the original text file. [isOriginal] indicates that the
 * content comes directly from a text file and not a macro expansion.
 */
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
