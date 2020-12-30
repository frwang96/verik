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

package verikc.rf.ast

import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.vk.ast.VkModule

data class RfModule(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val ports: List<RfPort>,
    val primaryProperties: List<RfPrimaryProperty>,
    val componentInstances: List<RfComponentInstance>,
    val actionBlocks: List<RfActionBlock>
): RfDeclaration {

    constructor(module: VkModule): this(
        module.line,
        module.identifier,
        module.symbol,
        module.ports.map { RfPort(it) },
        module.primaryProperties.map { RfPrimaryProperty(it) },
        module.componentInstances.map { RfComponentInstance(it) },
        module.actionBlocks.map { RfActionBlock(it) }
    )
}
