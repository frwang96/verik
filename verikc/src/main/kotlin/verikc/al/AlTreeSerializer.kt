/*
 * Copyright (c) 2020 Francis Wang
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

import verikc.al.ast.AlTree
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object AlTreeSerializer {

    fun hash(string: String): ByteArray {
        val messageDigest = MessageDigest.getInstance("MD5")
        return messageDigest.digest(string.toByteArray(StandardCharsets.UTF_8))
    }

    fun serialize(tree: AlTree, hash: ByteArray): ByteArray {
        val buffer = ByteArrayOutputStream()
        buffer.write(hash)
        serializeRecursive(tree, buffer)
        return buffer.toByteArray()
    }

    fun deserialize(fileSymbol: Symbol, file: File, hash: ByteArray): AlTree? {
        return try {
            if (file.exists()) deserialize(fileSymbol, file.readBytes(), hash)
            else null
        } catch (exception: IOException) {
            null
        }
    }

    fun deserialize(fileSymbol: Symbol, byteArray: ByteArray, hash: ByteArray): AlTree? {
        val buffer = ByteArrayInputStream(byteArray)
        if (!buffer.readNBytes(hash.size).contentEquals(hash)) return null
        val tree = deserializeRecursive(fileSymbol, buffer)
        return if (buffer.available() == 0) tree
        else throw IOException("did not reach end of buffer")
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

    private fun deserializeRecursive(fileSymbol: Symbol, buffer: ByteArrayInputStream): AlTree {
        val line = read(buffer) + (read(buffer) shl 8)
        val index = read(buffer)

        val textSize = read(buffer) + (read(buffer) shl 8)
        val textBytes = buffer.readNBytes(textSize)
        if (textBytes.size != textSize) throw IOException("reached end of buffer")
        val text = String(textBytes, StandardCharsets.UTF_8)

        val childrenSize = read(buffer)
        val children = ArrayList<AlTree>()
        for (i in 0 until childrenSize) {
            children.add(deserializeRecursive(fileSymbol, buffer))
        }
        return AlTree(Line(fileSymbol, line), index, text, children)
    }

    private fun read(buffer: ByteArrayInputStream): Int {
        val value = buffer.read()
        if (value == -1) throw IOException("reached end of buffer")
        return value
    }
}