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

package verikc.lang.modules

import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeClass.TYPE
import verikc.base.ast.TypeReified
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.FUNCTION_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_SBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.FUNCTION_TYPE_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_SBIT
import verikc.lang.LangSymbol.FUNCTION_TYPE_UBIT
import verikc.lang.LangSymbol.FUNCTION_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_UBIT_INT_INT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.extract.LangExtractorUtil
import verikc.lang.reify.LangReifierUtil
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvTypeExtracted

object LangModuleData: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
            "_data",
            TYPE_INSTANCE,
            { null },
            TYPE_DATA
        )

        list.addType(
            "_bool",
            TYPE_DATA,
            { SvTypeExtracted("logic", "", "") },
            TYPE_BOOL
        )

        list.addFunction(
            "_bool",
            null,
            listOf(),
            listOf(),
            TYPE_BOOL,
            { TypeReified(TYPE_BOOL, TYPE, listOf()) },
            { null },
            FUNCTION_TYPE_BOOL
        )

        list.addType(
            "_int",
            TYPE_DATA,
            { SvTypeExtracted("integer", "", "") },
            TYPE_INT
        )

        list.addFunction(
            "_int",
            null,
            listOf(),
            listOf(),
            TYPE_INT,
            { TypeReified(TYPE_INT, TYPE, listOf()) },
            { null },
            FUNCTION_TYPE_INT
        )

        list.addType(
            "_ubit",
            TYPE_DATA,
            { SvTypeExtracted("logic", LangExtractorUtil.toPacked(it.args[0]), "") },
            TYPE_UBIT
        )

        list.addFunction(
            "_ubit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_UBIT,
            {
                val width = LangReifierUtil.toInt(it.args[0])
                if (width <= 0) throw LineException("width of ubit cannot be $width", it.line)
                TypeReified(TYPE_UBIT, TYPE, listOf(width))
            },
            { null },
            FUNCTION_TYPE_UBIT
        )

        list.addFunction(
            "ubit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_UBIT,
            { TypeReified(TYPE_UBIT, INSTANCE, listOf(0)) },
            {
                val width = it.function.typeReified.args[0]
                if (width == 0) throw LineException("could not infer width of ubit", it.function.line)
                val value = LiteralValue.fromBitInt(
                    width,
                    LangReifierUtil.toInt(it.function.args[0]),
                    it.function.line
                )
                SvExpressionLiteral(it.function.line, "${value.width}'h${value.hexString()}")
            },
            FUNCTION_UBIT_INT
        )

        list.addFunction(
            "ubit",
            null,
            listOf(TYPE_INT, TYPE_INT),
            listOf(INSTANCE, INSTANCE),
            TYPE_UBIT,
            {
                val width = LangReifierUtil.toInt(it.args[0])
                if (width <= 0) throw LineException("width of ubit cannot be $width", it.line)
                TypeReified(TYPE_UBIT, INSTANCE, listOf(width))
            },
            {
                val value = LiteralValue.fromBitInt(
                    it.function.typeReified.args[0],
                    LangReifierUtil.toInt(it.function.args[1]),
                    it.function.line
                )
                SvExpressionLiteral(it.function.line, "${value.width}'h${value.hexString()}")
            },
            FUNCTION_UBIT_INT_INT
        )

        list.addType(
            "_sbit",
            TYPE_DATA,
            { SvTypeExtracted("logic signed", LangExtractorUtil.toPacked(it.args[0]), "") },
            TYPE_SBIT
        )

        list.addFunction(
            "_sbit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_SBIT,
            {
                val width = LangReifierUtil.toInt(it.args[0])
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.line)
                TypeReified(TYPE_SBIT, TYPE, listOf(width))
            },
            { null },
            FUNCTION_TYPE_SBIT
        )

        list.addFunction(
            "sbit",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_SBIT,
            { TypeReified(TYPE_SBIT, INSTANCE, listOf(0)) },
            {
                val width = it.function.typeReified.args[0]
                if (width == 0) throw LineException("could not infer width of sbit", it.function.line)
                val value = LiteralValue.fromBitInt(
                    width,
                    LangReifierUtil.toInt(it.function.args[0]),
                    it.function.line
                )
                SvExpressionLiteral(it.function.line, "${value.width}'sh${value.hexString()}")
            },
            FUNCTION_SBIT_INT
        )

        list.addFunction(
            "sbit",
            null,
            listOf(TYPE_INT, TYPE_INT),
            listOf(INSTANCE, INSTANCE),
            TYPE_SBIT,
            {
                val width = LangReifierUtil.toInt(it.args[0])
                if (width <= 0) throw LineException("width of sbit cannot be $width", it.line)
                TypeReified(TYPE_SBIT, INSTANCE, listOf(width))
            },
            {
                val value = LiteralValue.fromBitInt(
                    it.function.typeReified.args[0],
                    LangReifierUtil.toInt(it.function.args[1]),
                    it.function.line
                )
                SvExpressionLiteral(it.function.line, "${value.width}'sh${value.hexString()}")
            },
            FUNCTION_SBIT_INT_INT
        )
    }
}
