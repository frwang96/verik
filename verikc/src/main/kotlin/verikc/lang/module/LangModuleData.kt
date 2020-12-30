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

package verikc.lang.module

import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.FUNCTION_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_SBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.FUNCTION_TYPE_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_UBIT
import verikc.lang.LangSymbol.FUNCTION_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_UBIT_INT_INT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangTypeList
import verikc.lang.extract.LangExtractorUtil
import verikc.lang.reify.LangReifierUtil
import verikc.ps.ast.PsExpressionLiteral
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvTypeExtracted

object LangModuleData: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_data",
            TYPE_INSTANCE,
            { null },
            TYPE_DATA
        )

        list.add(
            "_bool",
            TYPE_DATA,
            { SvTypeExtracted("logic", "", "") },
            TYPE_BOOL
        )

        list.add(
            "_int",
            TYPE_DATA,
            { SvTypeExtracted("integer", "", "") },
            TYPE_INT
        )

        list.add(
            "_ubit",
            TYPE_DATA,
            { SvTypeExtracted("logic", LangExtractorUtil.widthToPacked(it.args[0]), "") },
            TYPE_UBIT
        )

        list.add(
            "_sbit",
            TYPE_DATA,
            { SvTypeExtracted("logic signed", LangExtractorUtil.widthToPacked(it.args[0]), "") },
            TYPE_SBIT
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "_bool",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_BOOL,
            { TYPE_BOOL.toTypeReifiedType() },
            { null },
            FUNCTION_TYPE_BOOL
        )

        list.add(
            "_int",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedType() },
            { null },
            FUNCTION_TYPE_INT
        )

        list.add(
            "_ubit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            {
                val width = LangReifierUtil.intLiteralToInt(it.args[0])
                if (width <= 0) throw LineException("width of ubit cannot be $width", it.line)
                TYPE_UBIT.toTypeReifiedType(width)
            },
            { null },
            FUNCTION_TYPE_UBIT
        )

        list.add(
            "_sbit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_SBIT,
            {
                val width = LangReifierUtil.intLiteralToInt(it.args[0])
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.line)
                TYPE_SBIT.toTypeReifiedType(width)
            },
            { null },
            LangSymbol.FUNCTION_TYPE_SBIT
        )

        list.add(
            "ubit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { TYPE_UBIT.toTypeReifiedInstance(0) },
            {
                val width = it.function.typeReified.args[0]
                if (width == 0) throw LineException("could not infer width of ubit", it.function.line)
                if (it.function.args[0] is PsExpressionLiteral) {
                    val value = LiteralValue.fromBitInt(
                        width,
                        LangExtractorUtil.intLiteralToInt(it.function.args[0]),
                        it.function.line
                    )
                    SvExpressionLiteral(it.function.line, "${value.width}'h${value.hexString()}")
                } else {
                    it.args[0]
                }
            },
            FUNCTION_UBIT_INT
        )

        list.add(
            "ubit",
            null,
            listOf(TYPE_INT, TYPE_INT),
            listOf(INSTANCE, INSTANCE),
            false,
            TYPE_UBIT,
            {
                val width = LangReifierUtil.intLiteralToInt(it.args[0])
                if (width <= 0) throw LineException("width of ubit cannot be $width", it.line)
                TYPE_UBIT.toTypeReifiedInstance(width)
            },
            {
                if (it.function.args[1] is PsExpressionLiteral) {
                    val value = LiteralValue.fromBitInt(
                        it.function.typeReified.args[0],
                        LangExtractorUtil.intLiteralToInt(it.function.args[1]),
                        it.function.line
                    )
                    SvExpressionLiteral(it.function.line, "${value.width}'h${value.hexString()}")
                } else {
                    it.args[0]
                }
            },
            FUNCTION_UBIT_INT_INT
        )

        list.add(
            "sbit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_SBIT,
            { TYPE_SBIT.toTypeReifiedInstance(0) },
            {
                val width = it.function.typeReified.args[0]
                if (width == 0) throw LineException("could not infer width of sbit", it.function.line)
                if (it.function.args[0] is PsExpressionLiteral) {
                    val value = LiteralValue.fromBitInt(
                        width,
                        LangExtractorUtil.intLiteralToInt(it.function.args[0]),
                        it.function.line
                    )
                    SvExpressionLiteral(it.function.line, "${value.width}'sh${value.hexString()}")
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
            listOf(INSTANCE, INSTANCE),
            false,
            TYPE_SBIT,
            {
                val width = LangReifierUtil.intLiteralToInt(it.args[0])
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.line)
                TYPE_SBIT.toTypeReifiedInstance(width)
            },
            {
                if (it.function.args[1] is PsExpressionLiteral) {
                    val value = LiteralValue.fromBitInt(
                        it.function.typeReified.args[0],
                        LangExtractorUtil.intLiteralToInt(it.function.args[1]),
                        it.function.line
                    )
                    SvExpressionLiteral(it.function.line, "${value.width}'sh${value.hexString()}")
                } else {
                    it.args[0]
                }
            },
            FUNCTION_SBIT_INT_INT
        )
    }
}
