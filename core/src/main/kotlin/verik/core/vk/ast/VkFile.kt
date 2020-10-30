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

package verik.core.vk.ast

import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol
import verik.core.kt.ast.KtFile

data class VkFile(
        val file: Symbol,
        val declarations: List<VkDeclaration>
) {

    companion object {

        operator fun invoke(file: KtFile): VkFile {
            val declarations = ArrayList<VkDeclaration>()

            for (declaration in file.declarations) {
                when {
                    VkModule.isModule(declaration) -> declarations.add(VkModule(declaration))
                    VkEnum.isEnum(declaration) -> declarations.add(VkEnum(declaration))
                    else -> throw LineException("top level declaration not supported", declaration)
                }
            }

            return VkFile(
                    file.file,
                    declarations
            )
        }
    }
}