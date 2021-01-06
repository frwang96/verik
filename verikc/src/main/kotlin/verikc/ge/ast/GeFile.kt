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

import verikc.base.ast.LineException
import verikc.base.config.FileConfig
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkFile
import verikc.vk.ast.VkModule

data class GeFile(
    val config: FileConfig,
    val declarations: List<GeDeclaration>
) {

    companion object {

        operator fun invoke(file: VkFile): GeFile {
            val declarations = ArrayList<GeDeclaration>()
            for (declaration in file.declarations) {
                when (declaration) {
                    is VkModule -> declarations.add(GeModule(declaration))
                    is VkEnum -> declarations.add(GeEnum(declaration))
                    else -> throw LineException("top level declaration not supported", declaration.line)
                }
            }

            return GeFile(file.config, declarations)
        }
    }
}
