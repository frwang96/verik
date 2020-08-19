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

package verik.core.it

import verik.core.lang.Lang
import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.sv.SvTypeReified

enum class ItTypeClass {
    UNIT,
    INT,
    TYPE,
    INSTANCE
}

data class ItTypeReified(
        val type: Symbol,
        val typeClass: ItTypeClass,
        val args: List<Int>
) {

    fun toInstance(line: Line): ItTypeReified {
        if (typeClass != ItTypeClass.TYPE) {
            throw LineException("type expression expected", line)
        }
        return ItTypeReified(
                type,
                ItTypeClass.INSTANCE,
                args
        )
    }

    fun extract(line: Line): SvTypeReified {
        return Lang.typeTable.extract(this, line.line)
    }
}