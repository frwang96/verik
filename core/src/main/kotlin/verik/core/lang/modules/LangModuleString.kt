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

import verik.core.lang.LangFunction
import verik.core.lang.LangFunctionTable
import verik.core.lang.LangSymbol.FUNCTION_PRINT
import verik.core.lang.LangSymbol.FUNCTION_PRINTLN
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_STRING
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.lang.LangType
import verik.core.lang.LangTypeTable
import verik.core.sv.SvExpressionFunction
import verik.core.sv.SvTypeReified

object LangModuleString: LangModule {

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable
    ) {
        typeTable.add(LangType(
                TYPE_STRING,
                { SvTypeReified( "string", "", "" ) },
                "_string"
        ))

        functionTable.add(LangFunction(
                FUNCTION_PRINT,
                listOf(TYPE_STRING),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                { SvExpressionFunction(it.function.line, null, "\$write", listOf(it.args[0])) },
                "print"
        ))

        functionTable.add(LangFunction(
                FUNCTION_PRINTLN,
                listOf(TYPE_STRING),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                { SvExpressionFunction(it.function.line, null, "\$display", listOf(it.args[0])) },
                "println"
        ))
    }
}