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

package io.verik.compiler.main

import io.verik.compiler.common.TextFile
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Platform {

    const val separator = "/"

    fun readTextFile(path: Path): TextFile {
        try {
            val inputStream = FileInputStream(path.toFile())
            val charsetDecoder = Charset.forName("UTF-8").newDecoder()
            charsetDecoder.onMalformedInput(CodingErrorAction.IGNORE)
            val inputStreamReader = InputStreamReader(inputStream, charsetDecoder)
            val bufferedReader = BufferedReader(inputStreamReader)
            val lines = bufferedReader.readLines()
            return TextFile(path.toAbsolutePath(), lines.joinToString(separator = "\n", postfix = "\n"))
        } catch (exception: Exception) {
            Messages.FILE_READ_ERROR.on(SourceLocation.NULL, path)
        }
    }

    fun writeTextFile(textFile: TextFile) {
        try {
            Files.createDirectories(textFile.path.parent)
            Files.writeString(textFile.path, textFile.content)
        } catch (exception: Exception) {
            Messages.FILE_WRITE_ERROR.on(SourceLocation.NULL, textFile.path)
        }
    }

    fun getPathFromString(path: String): Path {
        return if (path.matches(Regex("[\\\\/]\\w+:.*"))) {
            Paths.get(path.substring(1))
        } else {
            Paths.get(path)
        }
    }

    fun getStringFromPath(path: Path): String {
        val names = (0 until path.nameCount).map { path.getName(it).toString() }
        val namesString = names.joinToString(separator = "/")
        return if (path.isAbsolute) "/$namesString"
        else namesString
    }
}
