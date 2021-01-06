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

package verikc.vk.build

import verikc.base.ast.LineException
import verikc.rs.ast.RsFile
import verikc.vk.ast.VkDeclaration
import verikc.vk.ast.VkFile

object VkBuilderFile {

    fun build(file: RsFile): VkFile {
        val declarations = ArrayList<VkDeclaration>()

        // TODO split declarations downstream
        for (declaration in file.types) {
            when {
                VkBuilderModule.match(declaration) -> declarations.add(VkBuilderModule.build(declaration))
                VkBuilderEnum.match(declaration) -> declarations.add(VkBuilderEnum.build(declaration))
                else -> throw LineException("top level declaration not supported", declaration.line)
            }
        }

        return VkFile(
            file.config,
            declarations
        )
    }
}
