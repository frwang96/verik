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

package verik.core.rf.ast

import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.SvComponentInstance
import verik.core.vk.ast.VkComponentInstance

data class RfComponentInstance(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        override var reifiedType: ReifiedType?,
        val connections: List<RfConnection>
): RfProperty {

    fun extract(symbolTable: RfSymbolTable): SvComponentInstance {
        return SvComponentInstance(
                line,
                identifier,
                symbolTable.extractComponentIdentifier(type, line),
                connections.map { it.extract(symbolTable) }
        )
    }

    constructor(componentInstance: VkComponentInstance): this(
            componentInstance.line,
            componentInstance.identifier,
            componentInstance.symbol,
            componentInstance.type,
            null,
            componentInstance.connections.map { RfConnection(it) }
    )
}