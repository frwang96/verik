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
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.FUNCTION_AND_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_AND_SBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_EXT_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_INV_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GEQ_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GT_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_LEQ_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_LT_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NOT_SBIT
import verikc.lang.LangSymbol.FUNCTION_OR_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_OR_SBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_RED_AND_SBIT
import verikc.lang.LangSymbol.FUNCTION_RED_OR_SBIT
import verikc.lang.LangSymbol.FUNCTION_SL_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_SR_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_S_INT
import verikc.lang.LangSymbol.FUNCTION_S_INT_INT
import verikc.lang.LangSymbol.FUNCTION_TRU_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_SBIT
import verikc.lang.LangSymbol.FUNCTION_XOR_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_XOR_SBIT_UBIT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_LOGIC
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangTypeList
import verikc.lang.extract.LangExtractorUtil
import verikc.lang.resolve.LangResolverFunction
import verikc.lang.resolve.LangResolverUtil
import verikc.ps.ast.PsExpressionLiteral
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType
import verikc.sv.ast.SvTypeExtracted

object LangModuleSbit: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_sbit",
            TYPE_LOGIC,
            true,
            {
                SvTypeExtracted(
                    "logic signed",
                    LangExtractorUtil.widthToPacked(it.typeGenerified.getInt(0)),
                    ""
                )
            },
            TYPE_SBIT
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "_sbit",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            TYPE,
            {
                val width = LangResolverUtil.evaluateToInt(it.expression.args[0], it.symbolTable)
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.expression.line)
                TYPE_SBIT.toTypeGenerified(width)
            },
            { null },
            FUNCTION_TYPE_SBIT
        )

        list.add(
            ">",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverUtil.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOL.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.GT, listOf(it.args[0])) },
            FUNCTION_NATIVE_GT_SBIT_SBIT
        )

        list.add(
            ">=",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverUtil.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOL.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.GEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_GEQ_SBIT_SBIT
        )

        list.add(
            "<",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverUtil.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOL.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LT, listOf(it.args[0])) },
            FUNCTION_NATIVE_LT_SBIT_SBIT
        )

        list.add(
            "<=",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverUtil.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                TYPE_BOOL.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_LEQ_SBIT_SBIT
        )

        list.add(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT
        )

        list.add(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT, TYPE_INT),
            listOf(VALUE, VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveNativeGetIntInt(it, TYPE_SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_PART, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT_INT
        )

        list.add(
            "!",
            TYPE_SBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_NEGATION, listOf()) },
            FUNCTION_NATIVE_NOT_SBIT
        )

        list.add(
            "sl",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.getTypeGenerifiedNotNull() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SLA, it.args) },
            FUNCTION_SL_SBIT_INT
        )

        list.add(
            "sr",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.getTypeGenerifiedNotNull() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SRA, it.args) },
            FUNCTION_SR_SBIT_INT
        )

        list.add(
            "and",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_AND, it.args) },
            FUNCTION_AND_SBIT_UBIT
        )

        list.add(
            "and",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_AND, it.args) },
            FUNCTION_AND_SBIT_SBIT
        )

        list.add(
            "or",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_OR, it.args) },
            FUNCTION_OR_SBIT_UBIT
        )

        list.add(
            "or",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_OR, it.args) },
            FUNCTION_OR_SBIT_SBIT
        )

        list.add(
            "xor",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_UBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_XOR, it.args) },
            FUNCTION_XOR_SBIT_UBIT
        )

        list.add(
            "xor",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveBitwise(it, TYPE_SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_XOR, it.args) },
            FUNCTION_XOR_SBIT_SBIT
        )

        list.add(
            "inv",
            TYPE_SBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_UBIT.toTypeGenerified(LangResolverUtil.bitToWidth(it.expression.receiver!!)) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.BITWISE_NEGATION, listOf()) },
            FUNCTION_INV_SBIT
        )

        list.add(
            "red_and",
            TYPE_SBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.REDUCTION_AND, listOf()) },
            FUNCTION_RED_AND_SBIT
        )

        list.add(
            "red_or",
            TYPE_SBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.REDUCTION_OR, listOf()) },
            FUNCTION_RED_OR_SBIT
        )

        list.add(
            "red_xor",
            TYPE_SBIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.REDUCTION_XOR, listOf()) },
            LangSymbol.FUNCTION_RED_XOR_SBIT
        )

        list.add(
            "ext",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveExt(it, TYPE_SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.CAST_WIDTH, it.args) },
            FUNCTION_EXT_SBIT_INT
        )

        list.add(
            "tru",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveTru(it, TYPE_SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.CAST_WIDTH, it.args) },
            FUNCTION_TRU_SBIT_INT
        )

        list.add(
            "s",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_SBIT.toTypeGenerified(0) },
            {
                val width = it.expression.typeGenerified.getInt(0)
                if (width == 0) throw LineException("could not infer width of sbit", it.expression.line)
                if (it.expression.args[0] is PsExpressionLiteral) {
                    val value = LiteralValue.fromBitInt(
                        width,
                        LangExtractorUtil.intLiteralToInt(it.expression.args[0]),
                        it.expression.line
                    )
                    SvExpressionLiteral(it.expression.line, "${value.width}'sh${value.hexString()}")
                } else {
                    it.args[0]
                }
            },
            FUNCTION_S_INT
        )

        list.add(
            "s",
            null,
            listOf(TYPE_INT, TYPE_INT),
            listOf(VALUE, VALUE),
            false,
            VALUE,
            {
                val width = LangResolverUtil.evaluateToInt(it.expression.args[0], it.symbolTable)
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.expression.line)
                TYPE_SBIT.toTypeGenerified(width)
            },
            {
                if (it.expression.args[1] is PsExpressionLiteral) {
                    val value = LiteralValue.fromBitInt(
                        it.expression.typeGenerified.getInt(0),
                        LangExtractorUtil.intLiteralToInt(it.expression.args[1]),
                        it.expression.line
                    )
                    SvExpressionLiteral(it.expression.line, "${value.width}'sh${value.hexString()}")
                } else {
                    it.args[0]
                }
            },
            FUNCTION_S_INT_INT
        )
    }
}