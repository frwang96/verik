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

package verikc.lang.module

import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_AND_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_AND_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_EXT_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_INV_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GEQ_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_UBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GT_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_LEQ_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_LT_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NOT_UBIT
import verikc.lang.LangSymbol.FUNCTION_OR_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_OR_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_RED_AND_UBIT
import verikc.lang.LangSymbol.FUNCTION_RED_OR_UBIT
import verikc.lang.LangSymbol.FUNCTION_RED_XOR_UBIT
import verikc.lang.LangSymbol.FUNCTION_SHL_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_SHL_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_SHR_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_SHR_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_TRU_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_T_UBIT
import verikc.lang.LangSymbol.FUNCTION_U_INT
import verikc.lang.LangSymbol.FUNCTION_U_INT_INT
import verikc.lang.LangSymbol.FUNCTION_U_SBIT
import verikc.lang.LangSymbol.FUNCTION_XOR_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_XOR_UBIT_UBIT
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_LOGIC
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangTypeList
import verikc.lang.resolve.LangResolverCommon
import verikc.lang.resolve.LangResolverFunction
import verikc.lang.util.LangExtractorUtil
import verikc.ps.ast.PsExpressionLiteral
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType
import verikc.sv.ast.SvTypeExtracted

object LangModuleUbit: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "Ubit",
            TYPE_LOGIC,
            true,
            {
                SvTypeExtracted(
                    "logic",
                    LangExtractorUtil.widthToPacked(it.typeGenerified.getInt(0)),
                    ""
                )
            },
            TYPE_UBIT
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "t_Ubit",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            TYPE,
            {
                val width = LangResolverCommon.evaluateToInt(it.expression.args[0], it.symbolTable)
                if (width <= 0) throw LineException("width of ubit cannot be $width", it.expression.line)
                TYPE_UBIT.toTypeGenerified(width)
            },
            { null },
            FUNCTION_T_UBIT
        )

        list.add(
            ">",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverCommon.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOLEAN.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.GT, listOf(it.args[0])) },
            FUNCTION_NATIVE_GT_UBIT_UBIT
        )

        list.add(
            ">=",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverCommon.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOLEAN.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.GEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_GEQ_UBIT_UBIT
        )

        list.add(
            "<",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverCommon.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOLEAN.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LT, listOf(it.args[0])) },
            FUNCTION_NATIVE_LT_UBIT_UBIT
        )

        list.add(
            "<=",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverCommon.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOLEAN.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_LEQ_UBIT_UBIT
        )

        list.add(
            "get",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_UBIT_INT
        )

        list.add(
            "get",
            TYPE_UBIT,
            listOf(TYPE_INT, TYPE_INT),
            listOf(VALUE, VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveNativeGetIntInt(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_PART, it.args) },
            FUNCTION_NATIVE_GET_UBIT_INT_INT
        )

        list.add(
            "!",
            TYPE_UBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_NEGATION, listOf()) },
            FUNCTION_NATIVE_NOT_UBIT
        )

        list.add(
            "shl",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.typeGenerified!! },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SLL, it.args) },
            FUNCTION_SHL_UBIT_INT
        )

        list.add(
            "shl",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.typeGenerified!! },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SLL, it.args) },
            FUNCTION_SHL_UBIT_UBIT
        )

        list.add(
            "shr",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.typeGenerified!! },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SRL, it.args) },
            FUNCTION_SHR_UBIT_INT
        )

        list.add(
            "shr",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.typeGenerified!! },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SRL, it.args) },
            FUNCTION_SHR_UBIT_UBIT
        )

        list.add(
            "and",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_AND, it.args) },
            FUNCTION_AND_UBIT_UBIT
        )

        list.add(
            "and",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_AND, it.args) },
            FUNCTION_AND_UBIT_SBIT
        )

        list.add(
            "or",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_OR, it.args) },
            FUNCTION_OR_UBIT_UBIT
        )

        list.add(
            "or",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_OR, it.args) },
            FUNCTION_OR_UBIT_SBIT
        )

        list.add(
            "xor",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_XOR, it.args) },
            FUNCTION_XOR_UBIT_UBIT
        )

        list.add(
            "xor",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_XOR, it.args) },
            FUNCTION_XOR_UBIT_SBIT
        )

        list.add(
            "inv",
            TYPE_UBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { it.expression.receiver!!.typeGenerified!! },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_NEGATION, listOf()) },
            FUNCTION_INV_UBIT
        )

        list.add(
            "red_and",
            TYPE_UBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.REDUCTION_AND, listOf()) },
            FUNCTION_RED_AND_UBIT
        )

        list.add(
            "red_or",
            TYPE_UBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.REDUCTION_OR, listOf()) },
            FUNCTION_RED_OR_UBIT
        )

        list.add(
            "red_xor",
            TYPE_UBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.REDUCTION_XOR, listOf()) },
            FUNCTION_RED_XOR_UBIT
        )

        list.add(
            "ext",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveExt(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.CAST, it.args) },
            FUNCTION_EXT_UBIT_INT
        )

        list.add(
            "tru",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveTru(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.CAST, it.args) },
            FUNCTION_TRU_UBIT_INT
        )

        list.add(
            "u",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_UBIT.toTypeGenerified(0) },
            {
                val width = it.expression.typeGenerified.getInt(0)
                if (width == 0) throw LineException("could not infer width of ubit", it.expression.line)
                if (it.expression.args[0] is PsExpressionLiteral) {
                    val value = LiteralValue.encodeBitInt(
                        width,
                        LangExtractorUtil.intLiteralToInt(it.expression.args[0]),
                        it.expression.line
                    )
                    SvExpressionLiteral(it.expression.line, "${value.width}'h${value.hexString()}")
                } else {
                    it.args[0]
                }
            },
            FUNCTION_U_INT
        )

        list.add(
            "u",
            null,
            listOf(TYPE_INT, TYPE_INT),
            listOf(VALUE, VALUE),
            false,
            VALUE,
            {
                val width = LangResolverCommon.evaluateToInt(it.expression.args[0], it.symbolTable)
                if (width <= 0) throw LineException("width of ubit cannot be $width", it.expression.line)
                TYPE_UBIT.toTypeGenerified(width)
            },
            {
                if (it.expression.args[1] is PsExpressionLiteral) {
                    val value = LiteralValue.encodeBitInt(
                        it.expression.typeGenerified.getInt(0),
                        LangExtractorUtil.intLiteralToInt(it.expression.args[1]),
                        it.expression.line
                    )
                    SvExpressionLiteral(it.expression.line, "${value.width}'h${value.hexString()}")
                } else {
                    SvExpressionOperator(
                        it.expression.line,
                        it.args[1],
                        SvOperatorType.CAST,
                        listOf(SvExpressionLiteral(it.expression.line, "${it.expression.typeGenerified.getInt(0)}"))
                    )
                }
            },
            FUNCTION_U_INT_INT
        )

        list.add(
            "u",
            null,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_UBIT.toTypeGenerified(LangResolverCommon.bitToWidth(it.expression.args[0])) },
            {
                SvExpressionOperator(
                    it.expression.line,
                    it.args[0],
                    SvOperatorType.CAST,
                    listOf(SvExpressionLiteral(it.expression.line, "unsigned"))
                )
            },
            FUNCTION_U_SBIT
        )
    }
}