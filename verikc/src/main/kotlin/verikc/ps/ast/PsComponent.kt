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

import verikc.base.ast.ComponentType
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.vk.ast.VkComponent

data class PsComponent(
    val line: Line,
    val identifier: String,
    val symbol: Symbol,
    val componentType: ComponentType,
    val ports: List<PsPort>,
    val properties: List<PsProperty>,
    val componentInstances: List<PsComponentInstance>,
    val actionBlocks: List<PsActionBlock>,
    val methodBlocks: List<PsMethodBlock>
) {

    constructor(component: VkComponent): this(
        component.line,
        component.identifier,
        component.symbol,
        component.componentType,
        component.ports.map { PsPort(it) },
        component.properties.map { PsProperty(it) },
        component.componentInstances.map { PsComponentInstance(it) },
        component.actionBlocks.map { PsActionBlock(it) },
        component.methodBlocks.map { PsMethodBlock(it) }
    )
}
