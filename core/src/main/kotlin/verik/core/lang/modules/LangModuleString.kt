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

package verik.core.lang.modules

import verik.core.it.extract.ItExpressionExtractorString
import verik.core.lang.LangFunction
import verik.core.lang.LangFunctionExtractorRequest
import verik.core.lang.LangOperator
import verik.core.lang.LangSymbol.FUNCTION_PRINT
import verik.core.lang.LangSymbol.FUNCTION_PRINTLN
import verik.core.lang.LangSymbol.TYPE_ANY
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_STRING
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.lang.LangType
import verik.core.sv.SvExpression
import verik.core.sv.SvExpressionLiteral
import verik.core.sv.SvStatementExpression
import verik.core.sv.SvTypeReified

object LangModuleString: LangModule {

    override fun load(
            types: ArrayList<LangType>,
            functions: ArrayList<LangFunction>,
            operators: ArrayList<LangOperator>
    ) {
        types.add(LangType(
                "_string",
                TYPE_INSTANCE,
                "_instance",
                { SvTypeReified( "string", "", "" ) },
                TYPE_STRING
        ))

        functions.add(LangFunction(
                "print",
                null,
                listOf(TYPE_ANY),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                { if (it.function.args[0].typeReified!!.type == TYPE_STRING) {
                    SvStatementExpression.wrapFunction(
                            it.function.line,
                            null,
                            "\$write",
                            listOf(it.args[0])
                    )
                } else {
                    SvStatementExpression.wrapFunction(
                            it.function.line,
                            null,
                            "\$write",
                            getPrintArgs(it)
                    )
                } },
                FUNCTION_PRINT
        ))

        functions.add(LangFunction(
                "println",
                null,
                listOf(TYPE_ANY),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                { if (it.function.args[0].typeReified!!.type == TYPE_STRING) {
                    SvStatementExpression.wrapFunction(
                            it.function.line,
                            null,
                            "\$display",
                            listOf(it.args[0])
                    )
                } else {
                    SvStatementExpression.wrapFunction(
                            it.function.line,
                            null,
                            "\$display",
                            getPrintArgs(it)
                    )
                } },
                FUNCTION_PRINTLN
        ))
    }

    private fun getPrintArgs(request: LangFunctionExtractorRequest): List<SvExpression> {
        val formatString = ItExpressionExtractorString.defaultFormatString(
                request.function.args[0].typeReified!!,
                request.function
        )
        return listOf(
                SvExpressionLiteral(request.function.line, "\"$formatString\""),
                request.args[0]
        )
    }
}