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
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.FUNCTION_UBIT_INT
import verikc.lang.LangSymbol.TYPE_ENUM
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import kotlin.math.max

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
            val labelingFunctionSymbol = if (labelingExpression != null) {
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

            val labelExpressions = if (labelingFunctionSymbol == null) {
                getLabelExpressions(enumProperties)
            } else {
                enumProperties.forEach {
                    if (it.arg != null) throw LineException("enum value not permitted", it.line)
                }
                getLabelExpressions(
                    labelingFunctionSymbol,
                    enumProperties.size,
                    labelingExpression!!.line
                )
            }

            val properties = enumProperties.mapIndexed { index, it -> VkEnumProperty(it, labelExpressions[index]) }
            val width = labelExpressions[0].value.width

            return VkEnum(
                type.line,
                type.identifier,
                type.symbol,
                typeConstructorFunctionSymbol,
                properties,
                width
            )
        }

        private fun getLabelExpressions(properties: List<KtEnumProperty>): List<VkExpressionLiteral> {
            val intValues = properties.map {
                if (it.arg != null) {
                    if (it.arg is KtExpressionFunction && it.arg.functionSymbol == FUNCTION_UBIT_INT) {
                        val expressionLiteral = it.arg.args[0]
                        if (expressionLiteral is KtExpressionLiteral && expressionLiteral.typeSymbol == TYPE_INT) {
                            expressionLiteral.value.toInt()
                        } else throw LineException("int literal expected in ubit function", it.line)
                    } else throw LineException("ubit function expected for enum value", it.line)
                } else throw LineException("enum value expected", it.line)
            }
            val width = max(1, intValues.map {
                32 - it.countLeadingZeroBits()
            }.maxOrNull()!!)
            return intValues.mapIndexed { index, it ->
                val line = properties[index].line
                VkExpressionLiteral(line, TYPE_UBIT, LiteralValue.fromBitInt(width, it, line))
            }
        }

        fun getLabelExpressions(
            labelingFunctionSymbol: Symbol,
            count: Int,
            line: Line
        ): List<VkExpressionLiteral> {
            val literalValues =  when (labelingFunctionSymbol) {
                LangSymbol.FUNCTION_ENUM_SEQUENTIAL -> {
                    val width = if (count == 1) 1
                    else 32 - (count - 1).countLeadingZeroBits()
                    (0 until count).map { LiteralValue.fromBitInt(width, it, line) }
                }
                LangSymbol.FUNCTION_ENUM_ONE_HOT -> {
                    if (count >= 31) throw LineException("too many enum properties", line)
                    (0 until count).map { LiteralValue.fromBitInt(count, 1 shl it, line) }
                }
                LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT -> {
                    if (count >= 32) throw LineException("too many enum properties", line)
                    (0 until count).map { LiteralValue.fromBitInt(
                        count - 1,
                        if (it == 0) 0 else 1 shl (it - 1),
                        line
                    ) }
                }
                else -> throw LineException("enum labeling function not recognized", line)
            }

            return literalValues.map {
                VkExpressionLiteral(line, TYPE_UBIT, it)
            }
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

    constructor(enumProperty: KtEnumProperty, labelExpression: VkExpressionLiteral): this(
        enumProperty.line,
        enumProperty.identifier,
        enumProperty.symbol,
        enumProperty.typeSymbol!!,
        labelExpression
    )
}

