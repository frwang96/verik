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
import verikc.rsx.ast.RsxFile
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkFile
import verikc.vk.ast.VkModule

object VkBuilderFile {

    fun build(file: RsxFile): VkFile {
        val modules = ArrayList<VkModule>()
        val enums = ArrayList<VkEnum>()

        file.types.forEach {
            when {
                VkBuilderModule.match(it) -> modules.add(VkBuilderModule.build(it))
                VkBuilderEnum.match(it) -> enums.add(VkBuilderEnum.build(it))
                else -> throw LineException("top level type not supported", it.line)
            }
        }

        return VkFile(file.config, modules, enums)
    }
}
