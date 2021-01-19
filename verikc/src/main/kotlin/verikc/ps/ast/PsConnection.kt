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
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.vk.ast.VkConnection

data class PsConnection(
    val line: Line,
    val portSymbol: Symbol,
    val portType: PortType,
    val expression: PsExpression
) {

    constructor(connection: VkConnection): this(
        connection.line,
        connection.portSymbol,
        connection.getPortTypeNotNull(),
        PsExpression(connection.expression)
    )
}
