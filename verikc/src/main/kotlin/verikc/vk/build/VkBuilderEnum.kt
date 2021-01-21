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
import verikc.lang.LangSymbol.TYPE_ENUM
import verikc.rs.ast.RsType
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkProperty

object VkBuilderEnum {

    fun match(type: RsType): Boolean {
        return type.typeParent.getTypeGenerifiedNotNull().typeSymbol == TYPE_ENUM
    }

    fun build(type: RsType): VkEnum {
        if (type.typeParent.getTypeGenerifiedNotNull().typeSymbol != TYPE_ENUM) {
            throw LineException("expected type to inherit from enum", type.line)
        }
        if (type.enumProperties.isEmpty())
            throw LineException("expected enum properties", type.line)

        return VkEnum(
            type.line,
            type.identifier,
            type.symbol,
            VkProperty(type.typeObject),
            type.enumProperties.map { VkProperty(it) }
        )
    }
}
