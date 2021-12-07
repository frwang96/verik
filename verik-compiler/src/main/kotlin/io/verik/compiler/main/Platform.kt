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

import java.nio.file.Path
import java.nio.file.Paths

object Platform {

    const val separator = "/"

    val isWindows = System.getProperty("os.name").lowercase().contains("win")

    fun getPathFromString(path: String): Path {
        return if (path.matches(Regex("/\\w+:.*"))) {
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
