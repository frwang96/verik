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
import verik.core.svx.SvxFile
import verik.core.symbol.Symbol
import verik.core.vkx.VkxComponent
import verik.core.vkx.VkxComponentType
import verik.core.vkx.VkxFile

data class ItFile(
        val file: Symbol,
        val declarations: List<ItDeclaration>
) {

    fun extract(): SvxFile {
        val modules = declarations.map {
            if (it is ItModule) it.extract()
            else throw LineException("declaration extraction not supported", it)
        }
        return SvxFile(modules)
    }

    companion object {

        operator fun invoke(file: VkxFile): ItFile {
            val declarations = ArrayList<ItDeclaration>()
            for (declaration in file.declarations) {
                if (declaration is VkxComponent && declaration.componentType == VkxComponentType.MODULE) {
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