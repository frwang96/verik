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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.ge.ast.*
import verikc.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verikc.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_UBIT_INT
import verikc.lang.LangSymbol.TYPE_ENUM
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkEnumEntry
import verikc.vk.ast.VkExpressionLiteral
import verikc.vk.ast.VkProperty
import kotlin.math.max

object VkBuilderEnum {

    fun match(declaration: GeDeclaration): Boolean {
        return (declaration is GeType && declaration.typeParent.typeSymbol == TYPE_ENUM)
    }

    fun build(declaration: GeDeclaration): VkEnum {
        val type = declaration.let {
            if (it is GeType) it
            else throw LineException("type declaration expected", it.line)
        }

        if (type.annotations.isNotEmpty()) throw LineException("invalid annotation", type.line)

        if (type.typeParent.typeSymbol != TYPE_ENUM) {
            throw LineException("expected type to inherit from enum", type.line)
        }

        if (type.parameterProperties.size != 1
            || type.parameterProperties[0].identifier != "value"
            || type.parameterProperties[0].typeSymbol != TYPE_UBIT
        ) {
            throw LineException("enum parameter with identifier value and type _ubit expected", type.line)
        }

        val labelingExpression = type.parameterProperties[0].expression
        val labelingFunctionSymbol = if (labelingExpression != null) {
            if (labelingExpression is GeExpressionFunction) {
                labelingExpression.functionSymbol
            } else throw LineException("enum labeling function expected", type.line)
        } else null

        if (type.enumProperties.isEmpty()) throw LineException("expected enum properties", type.line)

        val labelExpressions = if (labelingFunctionSymbol == null) {
            getExpressionsWithoutLabelingFunction(type.enumProperties)
        } else {
            type.enumProperties.forEach {
                if (it.expression != null) throw LineException("enum value not permitted", it.line)
            }
            getExpressionsWithLabelingFunction(
                labelingFunctionSymbol,
                type.enumProperties.size,
                labelingExpression!!.line
            )
        }

        val entries = type.enumProperties.mapIndexed { index, it -> buildEnumEntry(it, labelExpressions[index]) }
        val width = labelExpressions[0].value.width

        return VkEnum(
            type.line,
            type.identifier,
            type.symbol,
            type.typeConstructorFunction.symbol,
            entries,
            width
        )
    }

    fun getExpressionsWithLabelingFunction(
        labelingFunctionSymbol: Symbol,
        count: Int,
        line: Line
    ): List<VkExpressionLiteral> {
        val literalValues =  when (labelingFunctionSymbol) {
            FUNCTION_ENUM_SEQUENTIAL -> {
                val width = if (count == 1) 1
                else 32 - (count - 1).countLeadingZeroBits()
                (0 until count).map { LiteralValue.fromBitInt(width, it, line) }
            }
            FUNCTION_ENUM_ONE_HOT -> {
                if (count >= 31) throw LineException("too many enum properties", line)
                (0 until count).map { LiteralValue.fromBitInt(count, 1 shl it, line) }
            }
            FUNCTION_ENUM_ZERO_ONE_HOT -> {
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
            VkExpressionLiteral(line, TYPE_UBIT.toTypeGenerified(it.width), it)
        }
    }

    private fun getExpressionsWithoutLabelingFunction(enumProperties: List<GeProperty>): List<VkExpressionLiteral> {
        val intValues = enumProperties.map {
            if (it.expression != null) {
                if (it.expression is GeExpressionFunction && it.expression.functionSymbol == FUNCTION_UBIT_INT) {
                    val expressionLiteral = it.expression.args[0]
                    if (expressionLiteral is GeExpressionLiteral && expressionLiteral.typeSymbol == TYPE_INT) {
                        expressionLiteral.value.toInt()
                    } else throw LineException("int literal expected in ubit function", it.line)
                } else throw LineException("ubit function expected for enum value", it.line)
            } else throw LineException("enum value expected", it.line)
        }
        val width = max(1, intValues.map {
            32 - it.countLeadingZeroBits()
        }.maxOrNull()!!)
        return intValues.mapIndexed { index, it ->
            val line = enumProperties[index].line
            VkExpressionLiteral(line, TYPE_UBIT.toTypeGenerified(width), LiteralValue.fromBitInt(width, it, line))
        }
    }

    private fun buildEnumEntry(enumProperty: GeProperty, labelExpression: VkExpressionLiteral): VkEnumEntry {
        return VkEnumEntry(
            VkProperty(
                enumProperty.line,
                enumProperty.identifier,
                enumProperty.symbol,
                enumProperty.getTypeGenerifiedNotNull(),
            ),
            labelExpression
        )
    }
}
