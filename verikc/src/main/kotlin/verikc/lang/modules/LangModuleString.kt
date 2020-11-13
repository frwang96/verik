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

import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.FUNCTION_PRINT
import verikc.lang.LangSymbol.FUNCTION_PRINTLN
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.ps.extract.PsExpressionExtractorString
import verikc.ps.symbol.PsFunctionExtractorRequest
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvExtractedType

object LangModuleString: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
                "_string",
                TYPE_INSTANCE,
                { SvExtractedType( "string", "", "" ) },
                TYPE_STRING
        )

        list.addFunction(
                "print",
                null,
                listOf(TYPE_ANY),
                listOf(INSTANCE),
                TYPE_UNIT,
                { TYPE_REIFIED_UNIT },
                { if (it.function.args[0].reifiedType.type == TYPE_STRING) {
                    SvExpressionFunction(
                            it.function.line,
                            null,
                            "\$write",
                            listOf(it.args[0])
                    )
                } else {
                    SvExpressionFunction(
                            it.function.line,
                            null,
                            "\$write",
                            getPrintArgs(it)
                    )
                } },
                FUNCTION_PRINT
        )

        list.addFunction(
                "println",
                null,
                listOf(TYPE_ANY),
                listOf(INSTANCE),
                TYPE_UNIT,
                { TYPE_REIFIED_UNIT },
                { if (it.function.args[0].reifiedType.type == TYPE_STRING) {
                    SvExpressionFunction(
                            it.function.line,
                            null,
                            "\$display",
                            listOf(it.args[0])
                    )
                } else {
                    SvExpressionFunction(
                            it.function.line,
                            null,
                            "\$display",
                            getPrintArgs(it)
                    )
                } },
                FUNCTION_PRINTLN
        )
    }

    private fun getPrintArgs(request: PsFunctionExtractorRequest): List<SvExpression> {
        val formatString = PsExpressionExtractorString.defaultFormatString(
                request.function.args[0].reifiedType,
                request.function
        )
        return listOf(
                SvExpressionLiteral(request.function.line, "\"$formatString\""),
                request.args[0]
        )
    }
}
