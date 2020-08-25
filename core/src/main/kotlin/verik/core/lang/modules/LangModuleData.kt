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
import verik.core.lang.LangEntryList
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.FUNCTION_INT
import verik.core.lang.LangSymbol.FUNCTION_SINT
import verik.core.lang.LangSymbol.FUNCTION_UINT
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_DATA
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.extract.LangExtractorUtil
import verik.core.lang.reify.LangReifierUtil
import verik.core.sv.SvTypeReified

object LangModuleData: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
                "_data",
                TYPE_INSTANCE,
                "_instance",
                { null },
                TYPE_DATA
        )

        list.addType(
                "_bool",
                TYPE_DATA,
                "_data",
                { SvTypeReified("logic", "", "") },
                TYPE_BOOL
        )

        list.addFunction(
                "_bool",
                null,
                listOf(),
                TYPE_BOOL,
                { it.typeReified = ItTypeReified(TYPE_BOOL, ItTypeClass.TYPE, listOf()) },
                { null },
                FUNCTION_BOOL
        )

        list.addType(
                "_int",
                TYPE_DATA,
                "_data",
                { SvTypeReified("integer", "", "") },
                TYPE_INT
        )

        list.addFunction(
                "_int",
                null,
                listOf(),
                TYPE_INT,
                { it.typeReified = ItTypeReified(TYPE_INT, ItTypeClass.TYPE, listOf()) },
                { null },
                FUNCTION_INT
        )

        list.addType(
                "_uint",
                TYPE_DATA,
                "_data",
                { SvTypeReified("logic", LangExtractorUtil.toPacked(it.args[0]), "" ) },
                TYPE_UINT
        )

        list.addFunction(
                "_uint",
                null,
                listOf(TYPE_INT),
                TYPE_UINT,
                { it.typeReified = ItTypeReified(
                        TYPE_UINT,
                        ItTypeClass.TYPE,
                        listOf(LangReifierUtil.toInt(it.args[0]))
                ) },
                { null },
                FUNCTION_UINT
        )

        list.addType(
                "_sint",
                TYPE_DATA,
                "_data",
                { SvTypeReified( "logic signed", LangExtractorUtil.toPacked(it.args[0]), "" ) },
                TYPE_SINT
        )

        list.addFunction(
                "_sint",
                null,
                listOf(TYPE_INT),
                TYPE_SINT,
                { it.typeReified = ItTypeReified(
                        TYPE_SINT,
                        ItTypeClass.TYPE,
                        listOf(LangReifierUtil.toInt(it.args[0]))
                ) },
                { null },
                FUNCTION_SINT
        )
    }
}