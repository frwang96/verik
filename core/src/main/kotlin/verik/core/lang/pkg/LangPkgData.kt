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

package verik.core.lang.pkg

import verik.core.lang.LangFunction
import verik.core.lang.LangFunctionTable
import verik.core.lang.LangSymbol.FUN_BOOL_TYPE
import verik.core.lang.LangSymbol.FUN_SINT_TYPE
import verik.core.lang.LangSymbol.FUN_UINT_TYPE
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangType
import verik.core.lang.LangTypeTable

object LangPkgData: LangPkg {

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable
    ) {
        typeTable.add(LangType(
                TYPE_BOOL,
                "_bool"
        ))

        functionTable.add(LangFunction(
                FUN_BOOL_TYPE,
                listOf(),
                TYPE_BOOL,
                "_bool"
        ))

        typeTable.add(LangType(
                TYPE_UINT,
                "_uint"
        ))

        functionTable.add(LangFunction(
                FUN_UINT_TYPE,
                listOf(TYPE_INT),
                TYPE_UINT,
                "_uint"
        ))

        typeTable.add(LangType(
                TYPE_SINT,
                "_sint"
        ))

        functionTable.add(LangFunction(
                FUN_SINT_TYPE,
                listOf(TYPE_INT),
                TYPE_SINT,
                "_sint"
        ))
    }
}