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

package verikc.sv.ast

import verikc.base.config.FileConfig
import verikc.ps.ast.PsFile
import verikc.sv.table.SvSymbolTable

data class SvFile(
    val config: FileConfig,
    val components: List<SvComponent>,
    val primaryProperties: List<SvPrimaryProperty>,
    val enums: List<SvEnum>,
    val structs: List<SvStruct>,
    val clses: List<SvCls>
) {

    constructor(file: PsFile, symbolTable: SvSymbolTable): this(
        file.config,
        file.components.map { SvComponent(it, symbolTable) },
        file.primaryProperties.map { SvPrimaryProperty(it, symbolTable) },
        file.enums.map { SvEnum(it, symbolTable) },
        file.structs.map { SvStruct(it, symbolTable) },
        file.clses.map { SvCls(it, symbolTable) }
    )

    fun hasComponentDeclarations(): Boolean {
        return components.isNotEmpty()
    }

    fun hasPkgDeclarations(): Boolean {
        return primaryProperties.isNotEmpty() || enums.isNotEmpty() || structs.isNotEmpty() || clses.isNotEmpty()
    }
}
