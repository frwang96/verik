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
import verikc.lang.LangSymbol
import verikc.rs.ast.RsType
import verikc.vk.ast.VkProperty
import verikc.vk.ast.VkStruct

object VkBuilderStruct {

    fun match(type: RsType): Boolean {
        return type.typeParent.getTypeGenerifiedNotNull().typeSymbol == LangSymbol.TYPE_STRUCT
    }

    fun build(type: RsType): VkStruct {
        if (type.typeParent.getTypeGenerifiedNotNull().typeSymbol != LangSymbol.TYPE_STRUCT) {
            throw LineException("expected type to inherit from struct", type.line)
        }

        if (type.topObject != null)
            throw LineException("struct not allowed as top module in hierarchy", type.line)
        if (type.functions.isNotEmpty())
            throw LineException("unsupported function in struct", type.line)

        return VkStruct(
            type.line,
            type.identifier,
            type.symbol,
            VkBuilderMethodBlock.build(type.getInstanceConstructorNotNull(), true),
            type.properties.map { VkProperty(it) }
        )
    }
}