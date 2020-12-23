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

package verikc.base.config

import verikc.base.symbol.Symbol
import java.io.File

data class PkgConfig(
    val identifierKt: String,
    val identifierSv: String,
    val dir: File,
    val copyDir: File,
    val outDir: File,
    val symbol: Symbol,
    val fileConfigs: List<FileConfig>
) {

    val header = dir.resolve("headers.kt")
    val pkgWrapperFile = outDir.resolve("pkg.sv")

    fun fileCount(): Int {
        return fileConfigs.size
    }
}
