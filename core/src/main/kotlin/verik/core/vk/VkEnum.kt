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
import verik.core.kt.*
import verik.core.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verik.core.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verik.core.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verik.core.lang.LangSymbol.TYPE_ENUM
import verik.core.lang.LangSymbol.TYPE_INT

data class VkEnumEntry(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val value: LiteralValue
): VkDeclaration {

    companion object {

        operator fun invoke(enumProperty: KtEnumProperty, index: Int, labelingFunction: Symbol?): VkEnumEntry {
            val value = if (labelingFunction != null) {
                if (enumProperty.arg != null) throw LineException("enum value not permitted", enumProperty)
                when (labelingFunction) {
                    FUNCTION_ENUM_SEQUENTIAL -> LiteralValue.fromInt(index)
                    FUNCTION_ENUM_ONE_HOT -> when {
                        index >= 31 -> throw LineException("enum index out of range", enumProperty)
                        else -> LiteralValue.fromInt(1 shl index)
                    }
                    FUNCTION_ENUM_ZERO_ONE_HOT -> when {
                        index >= 32 -> throw LineException("enum index out of range", enumProperty)
                        index > 0 -> LiteralValue.fromInt(1 shl (index - 1))
                        else -> LiteralValue.fromInt(0)
                    }
                    else -> throw LineException("enum labeling function not recognized", enumProperty)
                }
            } else {
                if (enumProperty.arg != null) {
                    if (enumProperty.arg is KtExpressionLiteral && enumProperty.arg.type == TYPE_INT) {
                        enumProperty.arg.value
                    } else throw LineException("int literal expected for enum value", enumProperty)
                } else throw LineException("enum value expected", enumProperty)
            }
            return VkEnumEntry(
                    enumProperty.line,
                    enumProperty.identifier,
                    enumProperty.symbol,
                    value
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

            if (primaryType.parameters.size != 1) throw LineException("enum value parameter expected", primaryType)
            val labelingExpression = primaryType.parameters[0].expression
            val labelingFunction = if (labelingExpression != null) {
                if (labelingExpression is KtExpressionFunction) {
                    labelingExpression.function!!
                } else throw LineException("enum labling function expected", primaryType)
            } else null

            val enumProperties = primaryType.objectType?.enumProperties
                    ?: throw LineException("expected enum entries", primaryType)
            if (enumProperties.isEmpty()) throw LineException("expected enum entries", primaryType)
            val entries = enumProperties.mapIndexed { index, it -> VkEnumEntry(it, index, labelingFunction) }

            val width = entries.map { it.value.width }.maxOrNull()!! - 1

            return VkEnum(
                    primaryType.line,
                    primaryType.identifier,
                    primaryType.symbol,
                    entries,
                    width
            )
        }
    }
}

