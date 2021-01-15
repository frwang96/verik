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

object VkBuilderCls {

    fun match(type: RsType): Boolean {
        return type.typeParent.getTypeGenerifiedNotNull().typeSymbol == TYPE_CLASS
    }

    fun build(type: RsType): VkCls {
        if (type.annotations.isNotEmpty()) throw LineException("invalid annotation", type.line)

        if (type.typeParent.getTypeGenerifiedNotNull().typeSymbol != TYPE_CLASS) {
            throw LineException("expected type to inherit from class", type.line)
        }

        return VkCls(
            type.line,
            type.identifier,
            type.symbol
        )
    }
}