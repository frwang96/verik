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
import verik.core.sv.SvModule
import verik.core.vk.VkModule

data class ItModule(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val ports: List<ItPort>,
        val primaryProperties: List<ItPrimaryProperty>,
        val componentInstances: List<ItComponentInstance>,
        val actionBlocks: List<ItActionBlock>
): ItDeclaration {

    fun extract(symbolTable: ItSymbolTable): SvModule {
        return SvModule(
                line,
                identifier.substring(1),
                ports.map { it.extract(symbolTable) },
                primaryProperties.map { it.extract(symbolTable) },
                componentInstances.map { it.extract(symbolTable) },
                actionBlocks.map { it.extract(symbolTable) }
        )
    }

    constructor(module: VkModule): this(
            module.line,
            module.identifier,
            module.symbol,
            module.ports.map { ItPort(it) },
            module.primaryProperties.map { ItPrimaryProperty(it) },
            module.componentInstances.map { ItComponentInstance(it) },
            module.actionBlocks.map { ItActionBlock(it) }
    )
}