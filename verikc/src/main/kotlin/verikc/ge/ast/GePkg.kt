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

package verikc.ge.ast

import verikc.base.config.PkgConfig
import verikc.base.symbol.Symbol
import verikc.vk.ast.VkPkg

data class GePkg(
    val config: PkgConfig,
    val files: List<GeFile>
) {

    constructor(pkg: VkPkg): this(
        pkg.config,
        pkg.files.map { GeFile(it) }
    )

    fun file(fileSymbol: Symbol): GeFile {
        return files.find { it.config.symbol == fileSymbol }
            ?: throw IllegalArgumentException("could not find file $fileSymbol in package ${config.symbol}")
    }
}