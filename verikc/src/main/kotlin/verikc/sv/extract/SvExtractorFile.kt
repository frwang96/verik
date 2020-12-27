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

import verikc.base.ast.LineException
import verikc.ps.ast.PsEnum
import verikc.ps.ast.PsFile
import verikc.ps.ast.PsModule
import verikc.sv.ast.SvDeclaration
import verikc.sv.ast.SvFile
import verikc.sv.symbol.SvSymbolTable

object SvExtractorFile {

    fun extract(file: PsFile, symbolTable: SvSymbolTable): SvFile {
        val moduleDeclarations = ArrayList<SvDeclaration>()
        val pkgDeclarations = ArrayList<SvDeclaration>()
        file.declarations.forEach {
            when (it) {
                is PsModule -> moduleDeclarations.add(SvExtractorModule.extract(it, symbolTable))
                is PsEnum -> pkgDeclarations.add(SvExtractorEnum.extract(it))
                else -> throw LineException("top level declaration not supported", it.line)
            }
        }

        return SvFile(
            file.config,
            moduleDeclarations,
            pkgDeclarations
        )
    }
}
