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

import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.FUNCTION_NATIVE_STRING
import verikc.lang.LangSymbol.FUNCTION_PRINTLN
import verikc.lang.LangSymbol.FUNCTION_PRINTLN_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_PRINT_INSTANCE
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.ps.ast.PsExpression
import verikc.ps.ast.PsExpressionLiteral
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvTypeExtracted
import verikc.sv.table.SvFunctionExtractorRequest

object LangModuleString: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "String",
            TYPE_INSTANCE,
            false,
            { SvTypeExtracted("string", "", "") },
            TYPE_STRING
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "\"\"",
            TYPE_UNIT,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_STRING.toTypeGenerified() },
            {
                val stringLiterals = it.expression.args.map { arg ->
                    if (arg.typeGenerified.typeSymbol == TYPE_STRING && arg is PsExpressionLiteral) {
                        arg.value.decodeString().replace("%", "%%")
                    } else null
                }
                val strings = it.expression.args.mapIndexed { index, arg ->
                    stringLiterals[index] ?: formatString(arg, stringLiterals.getOrNull(index - 1))
                }
                val expressionLiteral = SvExpressionLiteral(
                    it.expression.line,
                    "\"${strings.joinToString(separator = "")}\""
                )
                val expressions = it.args.mapIndexedNotNull { index, arg ->
                    if (stringLiterals[index] == null) {
                        arg
                    } else null
                }
                SvExpressionFunction(
                    it.expression.line,
                    null,
                    "\$sformatf",
                    listOf(expressionLiteral) + expressions
                )
            },
            FUNCTION_NATIVE_STRING
        )

        list.add(
            "print",
            null,
            listOf(TYPE_INSTANCE),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            {
                if (it.expression.args[0].typeGenerified.typeSymbol == TYPE_STRING) {
                    SvExpressionFunction(
                        it.expression.line,
                        null,
                        "\$write",
                        listOf(it.args[0])
                    )
                } else {
                    SvExpressionFunction(
                        it.expression.line,
                        null,
                        "\$write",
                        printArgs(it)
                    )
                }
            },
            FUNCTION_PRINT_INSTANCE
        )

        list.add(
            "println",
            null,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { SvExpressionFunction(it.expression.line, null, "\$display", listOf()) },
            FUNCTION_PRINTLN
        )

        list.add(
            "println",
            null,
            listOf(TYPE_INSTANCE),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            {
                if (it.expression.args[0].typeGenerified.typeSymbol == TYPE_STRING) {
                    SvExpressionFunction(it.expression.line, null, "\$display", listOf(it.args[0]))
                } else {
                    SvExpressionFunction(it.expression.line, null, "\$display", printArgs(it))
                }
            },
            FUNCTION_PRINTLN_INSTANCE
        )
    }

    private fun formatString(expression: PsExpression, lastStringLiteral: String?): String {
        if (lastStringLiteral != null && lastStringLiteral.endsWith("0b", ignoreCase = true)) {
            if (expression.typeGenerified.typeSymbol !in listOf(
                    LangSymbol.TYPE_BOOLEAN,
                    LangSymbol.TYPE_INT,
                    LangSymbol.TYPE_UBIT,
                    LangSymbol.TYPE_SBIT
                )) {
                throw LineException("expression cannot be formatted in binary", expression.line)
            }
            return "%b"
        }
        if (lastStringLiteral != null && lastStringLiteral.endsWith("0x", ignoreCase = true)) {
            if (expression.typeGenerified.typeSymbol !in listOf(
                    LangSymbol.TYPE_BOOLEAN,
                    LangSymbol.TYPE_INT,
                    LangSymbol.TYPE_UBIT,
                    LangSymbol.TYPE_SBIT
                )) {
                throw LineException("expression cannot be formatted in hex", expression.line)
            }
            return "%h"
        }
        return defaultFormatString(expression.typeGenerified)
    }

    private fun printArgs(request: SvFunctionExtractorRequest): List<SvExpression> {
        val formatString = defaultFormatString(request.expression.args[0].typeGenerified)
        return listOf(
            SvExpressionLiteral(request.expression.line, "\"$formatString\""),
            request.args[0]
        )
    }

    private fun defaultFormatString(typeGenerified: TypeGenerified): String {
        return when (typeGenerified.typeSymbol) {
            LangSymbol.TYPE_BOOLEAN -> "%b"
            LangSymbol.TYPE_INT, LangSymbol.TYPE_UBIT, LangSymbol.TYPE_SBIT -> "%0d"
            LangSymbol.TYPE_TIME -> "%0t"
            TYPE_STRING -> "%s"
            else -> "%p"
        }
    }
}
