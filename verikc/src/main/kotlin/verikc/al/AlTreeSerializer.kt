/*
 * Copyright 2020 Francis Wang
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

package verikc.al

import verikc.base.ast.Line
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

object AlTreeSerializer {

    fun serialize(tree: AlTree): ByteArray {
        val buffer = ByteArrayOutputStream()
        serializeRecursive(tree, buffer)
        return buffer.toByteArray()
    }

    fun deserialize(byteArray: ByteArray): AlTree {
        val buffer = ByteArrayInputStream(byteArray)
        return deserializeRecursive(buffer)
    }

    private fun serializeRecursive(tree: AlTree, buffer: ByteArrayOutputStream) {
        val line = tree.line.line
        if (line > 0xffff) throw IllegalArgumentException("line number out of bounds")
        buffer.write(line)
        buffer.write(line shr 8)

        buffer.write(tree.index)

        val textBytes = tree.text.toByteArray(StandardCharsets.UTF_8)
        val textSize = textBytes.size
        if (textSize > 0xffff) throw IllegalArgumentException("text size out of bounds")
        buffer.write(textSize)
        buffer.write(textSize shr 8)
        buffer.write(textBytes)

        if (tree.children.size > 0xff) throw IllegalArgumentException("children count out of bounds")
        buffer.write(tree.children.size)
        tree.children.forEach { serializeRecursive(it, buffer) }
    }

    private fun deserializeRecursive(buffer: ByteArrayInputStream): AlTree {
        val line = buffer.read() + (buffer.read() shl 8)
        val index = buffer.read()
        val textSize = buffer.read() + (buffer.read() shl 8)
        val text = String(buffer.readNBytes(textSize), StandardCharsets.UTF_8)
        val childrenSize = buffer.read()
        val children = ArrayList<AlTree>()
        for (i in 0 until childrenSize) {
            children.add(deserializeRecursive(buffer))
        }
        return AlTree(Line(line), index, text, children)
    }
}