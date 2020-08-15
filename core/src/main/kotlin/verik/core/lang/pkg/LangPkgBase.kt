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

import verik.core.lang.LangFunctionTable
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.lang.LangType
import verik.core.lang.LangTypeClass
import verik.core.lang.LangTypeTable

object LangPkgBase: LangPkg {

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable
    ) {
        typeTable.add(LangType(
                TYPE_UNIT,
                LangTypeClass.UNIT,
                TYPE_UNIT,
                { null },
                "Unit"
        ))

        typeTable.add(LangType(
                TYPE_INT,
                LangTypeClass.INT,
                TYPE_INT,
                { null },
                "Int"
        ))
    }
}