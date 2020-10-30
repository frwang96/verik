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

package verik.core.rf.ast

import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.SvFile
import verik.core.vk.ast.VkEnum
import verik.core.vk.ast.VkFile
import verik.core.vk.ast.VkModule

data class RfFile(
        val file: Symbol,
        val declarations: List<RfDeclaration>
) {

    fun extractModuleFile(symbolTable: RfSymbolTable): SvFile? {
        val moduleDeclarations = declarations.mapNotNull {
            if (it is RfModule) it.extract(symbolTable)
            else null
        }
        return if (moduleDeclarations.isNotEmpty()) {
            SvFile(moduleDeclarations)
        } else null
    }

    fun extractPkgFile(): SvFile? {
        val pkgDeclarations = declarations.mapNotNull {
            if (it is RfEnum) it.extract()
            else null
        }
        return if (pkgDeclarations.isNotEmpty()) {
            SvFile(pkgDeclarations)
        } else null
    }

    companion object {

        operator fun invoke(file: VkFile): RfFile {
            val declarations = ArrayList<RfDeclaration>()
            for (declaration in file.declarations) {
                when (declaration) {
                    is VkModule -> declarations.add(RfModule(declaration))
                    is VkEnum -> declarations.add(RfEnum(declaration))
                    else -> throw LineException("top level declaration not supported", declaration)
                }
            }

            return RfFile(
                    file.file,
                    declarations
            )
        }
    }
}