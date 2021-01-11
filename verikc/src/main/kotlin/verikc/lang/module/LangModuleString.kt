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
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_PRINTLN
import verikc.lang.LangSymbol.FUNCTION_PRINTLN_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_PRINT_INSTANCE
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvTypeExtracted
import verikc.sv.extract.SvExtractorExpressionString
import verikc.sv.table.SvFunctionExtractorRequest

object LangModuleString: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_string",
            TYPE_INSTANCE,
            { SvTypeExtracted("string", "", "") },
            TYPE_STRING
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
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
                        getPrintArgs(it)
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
                    SvExpressionFunction(it.expression.line, null, "\$display", getPrintArgs(it))
                }
            },
            FUNCTION_PRINTLN_INSTANCE
        )
    }

    private fun getPrintArgs(request: SvFunctionExtractorRequest): List<SvExpression> {
        val formatString = SvExtractorExpressionString.defaultFormatString(
            request.expression.args[0].typeGenerified,
            request.expression.line
        )
        return listOf(
            SvExpressionLiteral(request.expression.line, "\"$formatString\""),
            request.args[0]
        )
    }
}
