/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.lang.util

import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_CON_BUSPORT_BUSPORT
import verikc.lang.LangSymbol.FUNCTION_CON_BUS_BUS
import verikc.lang.LangSymbol.FUNCTION_CON_CLOCKPORT_CLOCKPORT
import verikc.lang.LangSymbol.FUNCTION_CON_DATA_DATA

object LangSymbolUtil {

    fun isConFunction(functionSymbol: Symbol): Boolean {
        return functionSymbol in listOf(
            FUNCTION_CON_BUS_BUS,
            FUNCTION_CON_BUSPORT_BUSPORT,
            FUNCTION_CON_CLOCKPORT_CLOCKPORT,
            FUNCTION_CON_DATA_DATA
        )
    }
}