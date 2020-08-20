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

import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.*
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.FUNCTION_INT
import verik.core.lang.LangSymbol.FUNCTION_SINT
import verik.core.lang.LangSymbol.FUNCTION_UINT
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.sv.SvTypeReified

object LangModuleData: LangModule {

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable
    ) {
        typeTable.add(LangType(
                TYPE_BOOL,
                { SvTypeReified("logic", "", "") },
                "_bool"
        ))

        functionTable.add(LangFunction(
                FUNCTION_BOOL,
                listOf(),
                TYPE_BOOL,
                { it.typeReified = ItTypeReified(TYPE_BOOL, ItTypeClass.TYPE, listOf()) },
                { null },
                "_bool"
        ))

        typeTable.add(LangType(
                TYPE_INT,
                { SvTypeReified("integer", "", "") },
                "_int"
        ))

        functionTable.add(LangFunction(
                FUNCTION_INT,
                listOf(),
                TYPE_INT,
                { it.typeReified = ItTypeReified(TYPE_INT, ItTypeClass.TYPE, listOf()) },
                { null },
                "_int"
        ))

        typeTable.add(LangType(
                TYPE_UINT,
                { SvTypeReified("logic", LangTypeExtractorUtil.toPacked(it.args[0]), "" ) },
                "_uint"
        ))

        functionTable.add(LangFunction(
                FUNCTION_UINT,
                listOf(TYPE_INT),
                TYPE_UINT,
                { it.typeReified = ItTypeReified(
                        TYPE_UINT,
                        ItTypeClass.TYPE,
                        listOf(LangFunctionReifierUtil.toInt(it.args[0]))
                ) },
                { null },
                "_uint"
        ))

        typeTable.add(LangType(
                TYPE_SINT,
                { SvTypeReified( "logic signed", LangTypeExtractorUtil.toPacked(it.args[0]), "" ) },
                "_sint"
        ))

        functionTable.add(LangFunction(
                FUNCTION_SINT,
                listOf(TYPE_INT),
                TYPE_SINT,
                { it.typeReified = ItTypeReified(
                        TYPE_SINT,
                        ItTypeClass.TYPE,
                        listOf(LangFunctionReifierUtil.toInt(it.args[0]))
                ) },
                { null },
                "_sint"
        ))
    }
}