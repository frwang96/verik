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
import verikc.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verikc.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_U_INT
import verikc.lang.LangSymbol.TYPE_ENUM
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rs.ast.RsExpressionFunction
import verikc.rs.ast.RsExpressionLiteral
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkEnumEntry
import verikc.vk.ast.VkExpressionLiteral
import verikc.vk.ast.VkProperty
import kotlin.math.max

object VkBuilderEnum {

    fun match(type: RsType): Boolean {
        return type.typeParent.getTypeGenerifiedNotNull().typeSymbol == TYPE_ENUM
    }

    fun build(type: RsType): VkEnum {
        if (type.typeParent.getTypeGenerifiedNotNull().typeSymbol != TYPE_ENUM) {
            throw LineException("expected type to inherit from enum", type.line)
        }

        val labelingExpression = type.getEnumConstructorFunctionNotNull().parameterProperties[0].expression
        val labelingFunctionSymbol = if (labelingExpression != null) {
            if (labelingExpression is RsExpressionFunction) {
                labelingExpression.getFunctionSymbolNotNull()
            } else throw LineException("enum labeling function expected", type.line)
        } else null

        if (type.enumProperties.isEmpty()) throw LineException("expected enum properties", type.line)

        val labelExpressions = if (labelingFunctionSymbol == null) {
            getExpressionsWithoutLabelingFunction(type.enumProperties)
        } else {
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
            VkProperty(type.typeObject),
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

    private fun getExpressionsWithoutLabelingFunction(enumProperties: List<RsProperty>): List<VkExpressionLiteral> {
        val intValues = enumProperties.map {
            if (it.expression != null && it.expression is RsExpressionFunction) {
                val ubitExpression = it.expression.args[0]
                if (ubitExpression is RsExpressionFunction && ubitExpression.functionSymbol == FUNCTION_U_INT) {
                    val expressionLiteral = ubitExpression.args[0]
                    if (expressionLiteral is RsExpressionLiteral
                        && expressionLiteral.getTypeGenerifiedNotNull().typeSymbol == TYPE_INT
                    ) {
                        expressionLiteral.getValueNotNull().toInt()
                    } else throw LineException("int literal expected in ubit function", it.line)
                } else throw LineException("ubit expected for enum value", it.line)
            } else throw LineException("function expression expected", it.line)
        }
        val width = max(1, intValues.map {
            32 - it.countLeadingZeroBits()
        }.maxOrNull()!!)
        return intValues.mapIndexed { index, it ->
            val line = enumProperties[index].line
            VkExpressionLiteral(line, TYPE_UBIT.toTypeGenerified(width), LiteralValue.fromBitInt(width, it, line))
        }
    }

    private fun buildEnumEntry(enumProperty: RsProperty, labelExpression: VkExpressionLiteral): VkEnumEntry {
        return VkEnumEntry(
            VkProperty(
                enumProperty.line,
                enumProperty.identifier,
                enumProperty.symbol,
                enumProperty.mutabilityType,
                enumProperty.getTypeGenerifiedNotNull(),
            ),
            labelExpression
        )
    }
}
