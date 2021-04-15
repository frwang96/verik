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
import verikc.lang.LangSymbol.TYPE_CLASS
import verikc.rs.ast.RsType
import verikc.vk.ast.VkCls
import verikc.vk.ast.VkProperty

object VkBuilderCls {

    fun match(type: RsType): Boolean {
        return type.typeParent.getTypeGenerifiedNotNull().typeSymbol == TYPE_CLASS
    }

    fun build(type: RsType): VkCls {
        if (type.typeParent.getTypeGenerifiedNotNull().typeSymbol != TYPE_CLASS) {
            throw LineException("expected type to inherit from class", type.line)
        }
        if (type.topObject != null)
            throw LineException("class not allowed as top module in hierarchy", type.line)

        val properties = ArrayList<VkProperty>()
        type.properties.forEach {
            if (it.annotations.isEmpty()) properties.add(VkProperty(it))
            else throw LineException("unable to identify property ${it.symbol}", it.line)
        }

        val methodBlocks = type.functions.map {
            if (VkBuilderMethodBlock.match(it)) VkBuilderMethodBlock.build(it, false)
            else throw LineException("unable to identify function ${it.identifier}", it.line)
        }

        return VkCls(
            type.line,
            type.identifier,
            type.symbol,
            VkBuilderMethodBlock.build(type.getInstanceConstructorNotNull(), true),
            type.setvalFunction?.let{ VkBuilderMethodBlock.build(it, false) },
            properties,
            methodBlocks
        )
    }
}