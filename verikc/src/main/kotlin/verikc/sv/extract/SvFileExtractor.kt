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

package verikc.sv.extract

import verikc.ps.ast.PsEnum
import verikc.ps.ast.PsFile
import verikc.ps.ast.PsModule
import verikc.sv.ast.SvFile
import verikc.sv.symbol.SvSymbolTable

object SvFileExtractor {

    fun extractModuleFile(file: PsFile, symbolTable: SvSymbolTable): SvFile? {
        val moduleDeclarations = file.declarations.mapNotNull {
            if (it is PsModule) SvModuleExtractor.extract(it, symbolTable)
            else null
        }
        return if (moduleDeclarations.isNotEmpty()) {
            SvFile(file.config, moduleDeclarations)
        } else null
    }

    fun extractPkgFile(file: PsFile): SvFile? {
        val pkgDeclarations = file.declarations.mapNotNull {
            if (it is PsEnum) SvEnumExtractor.extract(it)
            else null
        }
        return if (pkgDeclarations.isNotEmpty()) {
            SvFile(file.config, pkgDeclarations)
        } else null
    }
}
