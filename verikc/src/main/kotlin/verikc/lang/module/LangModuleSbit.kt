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

import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeClass
import verikc.lang.BitType
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_SBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_LOGIC
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangTypeList
import verikc.lang.extract.LangExtractorUtil
import verikc.lang.reify.LangReifierFunction
import verikc.lang.reify.LangReifierUtil
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
            { SvTypeExtracted("logic signed", LangExtractorUtil.widthToPacked(it.args[0]), "") },
            TYPE_SBIT
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "_sbit",
            null,
            listOf(TYPE_INT),
            listOf(TypeClass.INSTANCE),
            false,
            TYPE_SBIT,
            {
                val width = LangReifierUtil.intLiteralToInt(it.args[0])
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.line)
                TYPE_SBIT.toTypeReifiedType(width)
            },
            { null },
            FUNCTION_TYPE_SBIT
        )

        list.add(
            "sbit",
            null,
            listOf(TYPE_INT),
            listOf(TypeClass.INSTANCE),
            false,
            TYPE_SBIT,
            { TYPE_SBIT.toTypeReifiedInstance(0) },
            {
                val width = it.expression.typeReified.args[0]
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
            FUNCTION_SBIT_INT
        )

        list.add(
            "sbit",
            null,
            listOf(TYPE_INT, TYPE_INT),
            listOf(TypeClass.INSTANCE, TypeClass.INSTANCE),
            false,
            TYPE_SBIT,
            {
                val width = LangReifierUtil.intLiteralToInt(it.args[0])
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.line)
                TYPE_SBIT.toTypeReifiedInstance(width)
            },
            {
                if (it.expression.args[1] is PsExpressionLiteral) {
                    val value = LiteralValue.fromBitInt(
                        it.expression.typeReified.args[0],
                        LangExtractorUtil.intLiteralToInt(it.expression.args[1]),
                        it.expression.line
                    )
                    SvExpressionLiteral(it.expression.line, "${value.width}'sh${value.hexString()}")
                } else {
                    it.args[0]
                }
            },
            FUNCTION_SBIT_INT_INT
        )

        list.add(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(TypeClass.INSTANCE),
            false,
            TYPE_BOOL,
            { TYPE_BOOL.toTypeReifiedInstance() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT
        )

        list.add(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT, TYPE_INT),
            listOf(TypeClass.INSTANCE, TypeClass.INSTANCE),
            false,
            TYPE_SBIT,
            { LangReifierFunction.reifyNativeGet(it, BitType.SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_PART, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT_INT
        )

        list.add(
            "+",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(TypeClass.INSTANCE),
            false,
            TYPE_SBIT,
            { LangReifierFunction.reifyNativeAddBit(it, BitType.SBIT) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_SBIT_SBIT
        )
    }
}