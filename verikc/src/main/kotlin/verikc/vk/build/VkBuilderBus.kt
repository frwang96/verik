/*
 * Copyright (c) 2021 Francis Wang
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
import verikc.lang.LangSymbol.TYPE_BUS
import verikc.rs.ast.RsType
import verikc.vk.ast.VkBus
import verikc.vk.ast.VkPort
import verikc.vk.ast.VkProperty

object VkBuilderBus {

    fun match(type: RsType): Boolean {
        return type.typeParent.getTypeGenerifiedNotNull().typeSymbol == TYPE_BUS
    }

    fun build(type: RsType): VkBus {
        if (type.typeParent.getTypeGenerifiedNotNull().typeSymbol != TYPE_BUS)
            throw LineException("expected type to inherit from bus", type.line)

        val ports = ArrayList<VkPort>()
        val properties = ArrayList<VkProperty>()
        type.properties.forEach {
            when {
                VkBuilderPort.match(it) -> ports.add(VkBuilderPort.build(it))
                else -> {
                    if (it.annotations.isEmpty()) properties.add(VkProperty(it))
                    else throw LineException("unable to identify property ${it.identifier}", it.line)
                }
            }
        }

        if (type.functions.isNotEmpty())
            throw LineException("functions in busses not supported", type.functions[0].line)

        return VkBus(
            type.line,
            type.identifier,
            type.symbol,
            ports,
            properties
        )
    }
}