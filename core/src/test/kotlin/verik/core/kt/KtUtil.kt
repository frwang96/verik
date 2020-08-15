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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.config.FileConfig
import verik.core.config.PkgConfig
import verik.core.symbol.Symbol
import verik.core.symbol.SymbolContext
import java.io.File

fun parseFile(rule: AlRule): KtFile {
    val file = Symbol(1, 1, 0)
    val symbolContext = SymbolContext()
    symbolContext.registerConfigs(
            PkgConfig(File(""), File(""), File(""), "x", null),
            listOf(FileConfig(File(""), File(""), File("")))
    )
    return KtFile(rule, file, symbolContext)
}

fun parseDeclaration(rule: AlRule): KtDeclaration {
    val file = Symbol(1, 1, 0)
    val symbolContext = SymbolContext()
    symbolContext.registerConfigs(
            PkgConfig(File(""), File(""), File(""), "x", null),
            listOf(FileConfig(File(""), File(""), File("")))
    )
    val indexer = { symbolContext.nextSymbol(file) }
    return KtDeclaration(rule, indexer)
}
