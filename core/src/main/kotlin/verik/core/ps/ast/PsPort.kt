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

package verik.core.ps.ast

import verik.core.base.ast.LineException
import verik.core.base.ast.PortType
import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.rf.ast.RfPort

data class PsPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val reifiedType: ReifiedType,
        val portType: PortType
): PsProperty {

    companion object {

        operator fun invoke(port: RfPort): PsPort {
            val reifiedType = port.reifiedType
                    ?: throw LineException("port ${port.symbol} has not been reified", port)

            return PsPort(
                    port.line,
                    port.identifier,
                    port.symbol,
                    reifiedType,
                    port.portType
            )
        }
    }
}