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

import verik.core.lang.LangFunctionTable
import verik.core.lang.LangSymbol.TYPE_ANY
import verik.core.lang.LangSymbol.TYPE_CLASS
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_MODULE
import verik.core.lang.LangType
import verik.core.lang.LangTypeTable

object LangModuleCommon: LangModule {

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable
    ) {
        typeTable.add(LangType(
                TYPE_INSTANCE,
                TYPE_ANY,
                { null },
                "_instance"
        ))

        typeTable.add(LangType(
                TYPE_MODULE,
                TYPE_ANY,
                { null },
                "_module"
        ))

        typeTable.add(LangType(
                TYPE_CLASS,
                TYPE_ANY,
                { null },
                "_class"
        ))
    }
}