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
import verikc.vk.ast.*

object VkBuilderFile {

    fun build(file: RsFile): VkFile {
        val components = ArrayList<VkComponent>()
        val busses = ArrayList<VkBus>()
        val enums = ArrayList<VkEnum>()
        val clses = ArrayList<VkCls>()

        file.types.forEach {
            when {
                VkBuilderComponent.match(it) -> components.add(VkBuilderComponent.build(it))
                VkBuilderBus.match(it) -> busses.add(VkBuilderBus.build(it))
                VkBuilderEnum.match(it) -> enums.add(VkBuilderEnum.build(it))
                VkBuilderCls.match(it) -> clses.add(VkBuilderCls.build(it))
                else -> throw LineException("top level type not supported", it.line)
            }
        }

        val primaryProperties = file.properties.map { VkPrimaryProperty(it) }

        return VkFile(file.config, components, busses, primaryProperties, enums, clses)
    }
}
