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

package verikc.vk.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.Symbol
import verikc.kt.ast.*
import verikc.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verikc.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verikc.lang.LangSymbol.TYPE_ENUM
import verikc.lang.LangSymbol.TYPE_INT

data class VkEnum(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val typeConstructorFunctionSymbol: Symbol,
    val properties: List<VkEnumProperty>,
    val width: Int
): VkDeclaration {

    companion object {

        fun isEnum(declaration: KtDeclaration): Boolean {
            return (declaration is KtType && declaration.typeParent.typeSymbol == TYPE_ENUM)
        }

        operator fun invoke(declaration: KtDeclaration): VkEnum {
            val type = declaration.let {
                if (it is KtType) it
                else throw LineException("type declaration expected", it.line)
            }

            if (type.annotations.isNotEmpty()) throw LineException("invalid annotation", type.line)

            if (type.typeParent.typeSymbol != TYPE_ENUM) {
                throw LineException("expected type to inherit from enum", type.line)
            }

            val typeConstructorFunctionSymbol = type.declarations.let {
                val typeConstructorFunction = it.getOrNull(0)
                if (typeConstructorFunction is KtFunction
                    && typeConstructorFunction.type == KtFunctionType.TYPE_CONSTRUCTOR
                ) {
                        typeConstructorFunction.symbol
                } else throw LineException("could not find type constructor function", type.line)
            }

            if (type.parameters.size != 1) throw LineException("enum value parameter expected", type.line)
            val labelingExpression = type.parameters[0].expression
            val labelingFunction = if (labelingExpression != null) {
                if (labelingExpression is KtExpressionFunction) {
                    labelingExpression.functionSymbol!!
                } else throw LineException("enum labeling function expected", type.line)
            } else null

            val enumProperties = type.declarations.mapNotNull {
                if (it is KtEnumProperty) it
                else if (it is KtFunction && it.type == KtFunctionType.TYPE_CONSTRUCTOR) null
                else throw LineException("only enum properties are permitted", type.line)
            }
            if (enumProperties.isEmpty()) throw LineException("expected enum properties", type.line)
            val properties = enumProperties.mapIndexed { index, it -> VkEnumProperty(it, index, labelingFunction) }

            val width = properties.map { it.expression.value.width }.maxOrNull()!! - 1

            return VkEnum(
                type.line,
                type.identifier,
                type.symbol,
                typeConstructorFunctionSymbol,
                properties,
                width
            )
        }
    }
}

data class VkEnumProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val typeSymbol: Symbol,
    val expression: VkExpressionLiteral
): VkProperty {

    companion object {

        operator fun invoke(enumProperty: KtEnumProperty, index: Int, labelingFunctionSymbol: Symbol?): VkEnumProperty {
            val expression = if (labelingFunctionSymbol != null) {
                val value = when (labelingFunctionSymbol) {
                    FUNCTION_ENUM_SEQUENTIAL -> LiteralValue.fromInt(index)
                    FUNCTION_ENUM_ONE_HOT -> when {
                        index >= 31 -> throw LineException("enum index out of range", enumProperty.line)
                        else -> LiteralValue.fromInt(1 shl index)
                    }
                    FUNCTION_ENUM_ZERO_ONE_HOT -> when {
                        index >= 32 -> throw LineException("enum index out of range", enumProperty.line)
                        index > 0 -> LiteralValue.fromInt(1 shl (index - 1))
                        else -> LiteralValue.fromInt(0)
                    }
                    else -> throw LineException("enum labeling function not recognized", enumProperty.line)
                }
                if (enumProperty.arg != null) throw LineException("enum value not permitted", enumProperty.line)
                VkExpressionLiteral(enumProperty.line, TYPE_INT, value)
            } else {
                if (enumProperty.arg != null) {
                    if (enumProperty.arg is KtExpressionLiteral && enumProperty.arg.typeSymbol == TYPE_INT) {
                        VkExpressionLiteral(enumProperty.arg)
                    } else throw LineException("int literal expected for enum value", enumProperty.line)
                } else throw LineException("enum value expected", enumProperty.line)
            }

            return VkEnumProperty(
                enumProperty.line,
                enumProperty.identifier,
                enumProperty.symbol,
                enumProperty.typeSymbol!!,
                expression
            )
        }
    }
}

