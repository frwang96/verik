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

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.ps.symbol.PsSymbolTable
import verikc.rf.ast.RfEnum
import verikc.rf.ast.RfFile
import verikc.rf.ast.RfModule
import verikc.sv.ast.SvFile

data class PsFile(
    val fileSymbol: Symbol,
    val declarations: List<PsDeclaration>
) {

    fun extractModuleFile(symbolTable: PsSymbolTable): SvFile? {
        val moduleDeclarations = declarations.mapNotNull {
            if (it is PsModule) it.extract(symbolTable)
            else null
        }
        return if (moduleDeclarations.isNotEmpty()) {
            SvFile(moduleDeclarations)
        } else null
    }

    fun extractPkgFile(): SvFile? {
        val pkgDeclarations = declarations.mapNotNull {
            if (it is PsEnum) it.extract()
            else null
        }
        return if (pkgDeclarations.isNotEmpty()) {
            SvFile(pkgDeclarations)
        } else null
    }

    companion object {

        operator fun invoke(file: RfFile): PsFile {
            val declarations = ArrayList<PsDeclaration>()
            for (declaration in file.declarations) {
                when (declaration) {
                    is RfModule -> declarations.add(PsModule(declaration))
                    is RfEnum -> declarations.add(PsEnum(declaration))
                    else -> throw LineException("top level declaration not supported", declaration.line)
                }
            }

            return PsFile(
                file.fileSymbol,
                declarations
            )
        }
    }
}
