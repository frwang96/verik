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

package verik.core.rf

import verik.core.base.Line
import verik.core.base.Symbol
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.SvConnection
import verik.core.vk.VkConnection

data class RfConnection(
        override val line: Int,
        val receiver: Symbol,
        val expression: RfExpression
): Line {

    fun extract(symbolTable: RfSymbolTable): SvConnection {
        return SvConnection(
                line,
                symbolTable.extractPropertyIdentifier(receiver, line),
                expression.extractAsExpression(symbolTable)
        )
    }

    constructor(connection: VkConnection): this(
            connection.line,
            connection.receiver,
            RfExpression(connection.expression)
    )
}