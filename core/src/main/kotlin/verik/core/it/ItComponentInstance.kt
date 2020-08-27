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

import verik.core.base.Symbol
import verik.core.it.symbol.ItSymbolTable
import verik.core.sv.SvComponentInstance
import verik.core.vk.VkComponentInstance

data class ItComponentInstance(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        override var reifiedType: ItReifiedType?,
        val connections: List<ItConnection>
): ItProperty {

    fun extract(symbolTable: ItSymbolTable): SvComponentInstance {
        return SvComponentInstance(
                line,
                identifier,
                symbolTable.extractTypeIdentifier(type, line),
                connections.map { it.extract(symbolTable) }
        )
    }

    constructor(componentInstance: VkComponentInstance): this(
            componentInstance.line,
            componentInstance.identifier,
            componentInstance.symbol,
            componentInstance.type,
            null,
            componentInstance.connections.map { ItConnection(it) }
    )
}