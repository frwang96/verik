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

package verikc.ps.ast

import verikc.base.config.PkgConfig
import verikc.base.symbol.Symbol
import verikc.sv.symbol.SvSymbolTable
import verikc.rf.ast.RfPkg
import verikc.sv.ast.SvPkg

data class PsPkg(
    val config: PkgConfig,
    val files: List<PsFile>
) {

    constructor(pkg: RfPkg): this(
        pkg.config,
        pkg.files.map { PsFile(it) }
    )

    fun extract(symbolTable: SvSymbolTable): SvPkg {
        return SvPkg(
            config,
            files.mapNotNull { it.extractModuleFile(symbolTable) },
            files.mapNotNull { it.extractPkgFile() }
        )
    }

    fun file(fileSymbol: Symbol): PsFile {
        return files.find { it.config.symbol == fileSymbol }
            ?: throw IllegalArgumentException("could not find file $fileSymbol in package ${config.symbol}")
    }
}
