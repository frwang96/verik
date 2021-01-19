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

import verikc.base.ast.ComponentType
import verikc.base.ast.LineException
import verikc.lang.LangSymbol.TYPE_BUS
import verikc.lang.LangSymbol.TYPE_BUSPORT
import verikc.lang.LangSymbol.TYPE_CLOCKPORT
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.rs.ast.RsType
import verikc.vk.ast.*

object VkBuilderComponent {

    fun match(type: RsType): Boolean {
        return type.typeParent.getTypeGenerifiedNotNull().typeSymbol in
                listOf(TYPE_MODULE, TYPE_BUS, TYPE_BUSPORT, TYPE_CLOCKPORT)
    }

    fun build(type: RsType): VkComponent {
        val componentType = when (type.typeParent.getTypeGenerifiedNotNull().typeSymbol) {
            TYPE_MODULE -> ComponentType.MODULE
            TYPE_BUS -> ComponentType.BUS
            TYPE_BUSPORT -> ComponentType.BUSPORT
            TYPE_CLOCKPORT -> ComponentType.CLOCKPORT
            else -> throw LineException("component type not recognized", type.line)
        }

        val ports = ArrayList<VkPort>()
        val properties = ArrayList<VkProperty>()
        val componentInstances = ArrayList<VkComponentInstance>()
        type.properties.forEach {
            when {
                VkBuilderPort.match(it) -> ports.add(VkBuilderPort.build(it))
                VkBuilderComponentInstance.match(it) -> componentInstances.add(VkBuilderComponentInstance.build(it))
                else -> {
                    if (it.annotations.isEmpty()) properties.add(VkProperty(it))
                    else throw LineException("unable to identify property ${it.identifier}", it.line)
                }
            }
        }

        val actionBlocks = ArrayList<VkActionBlock>()
        val methodBlocks = ArrayList<VkMethodBlock>()
        type.functions.forEach {
            when {
                VkBuilderActionBlock.match(it) -> actionBlocks.add(VkBuilderActionBlock.build(it))
                VkBuilderMethodBlock.match(it) -> methodBlocks.add(VkBuilderMethodBlock.build(it))
                else -> throw LineException("unable to identify function ${it.identifier}", it.line)
            }
        }

        if (componentType in listOf(ComponentType.BUSPORT, ComponentType.CLOCKPORT)) {
            if (properties.isNotEmpty())
                throw LineException("component cannot contain properties", type.line)
            if (componentInstances.isNotEmpty())
                throw LineException("component cannot contain component instances", type.line)
            if (actionBlocks.isNotEmpty())
                throw LineException("component cannot contain action blocks", type.line)
            if (methodBlocks.isNotEmpty())
                throw LineException("component cannot contain method blocks", type.line)
        }

        return VkComponent(
            type.line,
            type.identifier,
            type.symbol,
            componentType,
            ports,
            properties,
            componentInstances,
            actionBlocks,
            methodBlocks,
            null
        )
    }
}
