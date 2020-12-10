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

import verikc.lang.*
import verikc.lang.LangSymbol.FUNCTION_FINISH
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.sv.ast.*

object LangModuleSystem: LangModule {

    override fun load(list: LangEntryList) {
        list.addFunction(
                "finish",
                null,
                listOf(),
                listOf(),
                TYPE_UNIT,
                { TYPE_REIFIED_UNIT },
                { SvExpressionFunction(
                        it.function.line,
                        null,
                        "\$finish",
                        listOf()
                ) },
                FUNCTION_FINISH
        )
    }
}