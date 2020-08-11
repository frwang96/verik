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

package io.verik.core.kt

import io.verik.core.al.AlRule
import io.verik.core.config.FileConfig
import io.verik.core.kt.resolve.KtSymbolIndexer
import io.verik.core.kt.resolve.KtSymbolMap
import io.verik.core.main.FileTableFile
import io.verik.core.main.Symbol
import java.io.File

fun parseFile(rule: AlRule): KtFile {
    val file = FileTableFile(
            FileConfig(File(""), File(""), File(""), "", null),
            Symbol(1, 1)
    )
    return KtFile(rule, file, KtSymbolMap())
}

fun parseDeclaration(rule: AlRule): KtDeclaration {
    return KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
}
