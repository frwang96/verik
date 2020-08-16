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

package verik.core.it

import verik.core.main.LineException
import verik.core.sv.SvFile
import verik.core.main.symbol.Symbol
import verik.core.vk.VkFile
import verik.core.vk.VkModule

data class ItFile(
        val file: Symbol,
        val declarations: List<ItDeclaration>
) {

    fun extract(): SvFile {
        val modules = declarations.map {
            if (it is ItModule) it.extract()
            else throw LineException("declaration extraction not supported", it)
        }
        return SvFile(modules)
    }

    companion object {

        operator fun invoke(file: VkFile): ItFile {
            val declarations = ArrayList<ItDeclaration>()
            for (declaration in file.declarations) {
                if (declaration is VkModule) {
                    declarations.add(ItModule(declaration))
                } else {
                    throw LineException("top level declaration not supported", declaration)
                }
            }

            return ItFile(
                    file.file,
                    declarations
            )
        }
    }
}