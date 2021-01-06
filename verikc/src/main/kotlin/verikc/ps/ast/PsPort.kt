/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.ps.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.ge.ast.GePort

data class PsPort(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val typeGenerified: TypeGenerified,
    val portType: PortType
): PsProperty {

    companion object {

        operator fun invoke(port: GePort): PsPort {
            val typeGenerified = port.typeGenerified
                ?: throw LineException("port ${port.symbol} has not been generified", port.line)

            return PsPort(
                port.line,
                port.identifier,
                port.symbol,
                typeGenerified,
                port.portType
            )
        }
    }
}
