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

package verikc.vkx.builder

import verikc.base.ast.LineException
import verikc.rs.ast.RsFile
import verikc.vkx.ast.VkxDeclaration
import verikc.vkx.ast.VkxFile

object VkxBuilderFile {

    fun build(file: RsFile): VkxFile {
        val declarations = ArrayList<VkxDeclaration>()

        // TODO split declarations downstream
        for (declaration in file.types) {
            when {
                VkxBuilderModule.match(declaration) -> declarations.add(VkxBuilderModule.build(declaration))
                VkxBuilderEnum.match(declaration) -> declarations.add(VkxBuilderEnum.build(declaration))
                else -> throw LineException("top level declaration not supported", declaration.line)
            }
        }

        return VkxFile(
            file.config,
            declarations
        )
    }
}
