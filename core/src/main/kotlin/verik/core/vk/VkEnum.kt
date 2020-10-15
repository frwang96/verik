/*
 * Copyright 2020 Francis Wang
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

package verik.core.vk

import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.kt.KtDeclaration
import verik.core.kt.KtEnumProperty
import verik.core.kt.KtPrimaryType
import verik.core.lang.LangSymbol.TYPE_ENUM

data class VkEnumEntry(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val value: LiteralValue
): VkDeclaration {

    companion object {

        operator fun invoke(enumProperty: KtEnumProperty): VkEnumEntry {
            return VkEnumEntry(
                    enumProperty.line,
                    enumProperty.identifier,
                    enumProperty.symbol,
                    LiteralValue.fromInt(0)
            )
        }
    }
}

data class VkEnum(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val entries: List<VkEnumEntry>,
        val width: Int
): VkDeclaration {

    companion object {

        fun isEnum(declaration: KtDeclaration): Boolean {
            return if (declaration is KtPrimaryType) {
                if (declaration.constructorInvocation.type == TYPE_ENUM) true
                else declaration.objectType?.enumProperties != null
            } else false
        }

        operator fun invoke(declaration: KtDeclaration): VkEnum {
            val primaryType = declaration.let {
                if (it is KtPrimaryType) it
                else throw LineException("type declaration expected", it)
            }

            if (primaryType.annotations.isNotEmpty()) throw LineException("invalid annotation", primaryType)

            if (primaryType.constructorInvocation.type != TYPE_ENUM) {
                throw LineException("expected type to inherit from enum", primaryType)
            }

            val enumProperties = primaryType.objectType?.enumProperties
                    ?: throw LineException("expected enum entries", primaryType)
            val entries = enumProperties.map { VkEnumEntry(it) }

            return VkEnum(
                    primaryType.line,
                    primaryType.identifier,
                    primaryType.symbol,
                    entries,
                    0
            )
        }
    }
}

