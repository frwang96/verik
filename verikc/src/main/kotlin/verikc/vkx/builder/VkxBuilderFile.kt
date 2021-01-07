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
import verikc.gex.ast.GexFile
import verikc.vkx.ast.VkxEnum
import verikc.vkx.ast.VkxFile
import verikc.vkx.ast.VkxModule

object VkxBuilderFile {

    fun build(file: GexFile): VkxFile {
        val modules = ArrayList<VkxModule>()
        val enums = ArrayList<VkxEnum>()

        file.types.forEach {
            when {
                VkxBuilderModule.match(it) -> modules.add(VkxBuilderModule.build(it))
                VkxBuilderEnum.match(it) -> enums.add(VkxBuilderEnum.build(it))
                else -> throw LineException("top level type not supported", it.line)
            }
        }

        return VkxFile(file.config, modules, enums)
    }
}
