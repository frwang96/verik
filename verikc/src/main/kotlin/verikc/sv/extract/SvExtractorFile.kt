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

package verikc.sv.extract

import verikc.ps.ast.PsFile
import verikc.sv.ast.SvDeclaration
import verikc.sv.ast.SvEnum
import verikc.sv.ast.SvFile
import verikc.sv.ast.SvModule
import verikc.sv.table.SvSymbolTable

object SvExtractorFile {

    fun extract(file: PsFile, symbolTable: SvSymbolTable): SvFile {
        val componentDeclarations = ArrayList<SvDeclaration>()
        val pkgDeclarations = ArrayList<SvDeclaration>()
        file.modules.forEach { componentDeclarations.add(SvModule(it, symbolTable)) }
        file.enums.forEach { pkgDeclarations.add(SvEnum(it)) }

        return SvFile(
            file.config,
            componentDeclarations,
            pkgDeclarations
        )
    }
}
