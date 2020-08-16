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

package verik.core.itx

import verik.core.it.ItPort
import verik.core.it.ItPortType
import verik.core.it.ItTypeReified
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

data class ItxPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val typeReified: ItTypeReified,
        val portType: ItPortType
): ItxProperty {

    companion object {

        operator fun invoke(port: ItPort): ItxPort {
            val typeReified = port.typeReified
                    ?: throw LineException("type of port has not been reified", port)

            return ItxPort(
                    port.line,
                    port.identifier,
                    port.symbol,
                    typeReified,
                    port.portType
            )
        }
    }
}