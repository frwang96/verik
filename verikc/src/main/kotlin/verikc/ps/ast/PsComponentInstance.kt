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
import verikc.vk.ast.VkComponentInstance

data class PsComponentInstance(
    val property: PsProperty,
    val componentType: ComponentType,
    val clockPortEventExpression: PsExpression?,
    val connections: List<PsConnection>
) {

    constructor(componentInstance: VkComponentInstance): this(
        PsProperty(componentInstance.property),
        componentInstance.getComponentTypeNotNull(),
        componentInstance.clockPortEventExpression?.let { PsExpression(it) },
        componentInstance.connections.map { PsConnection(it) }
    )
}
