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

import verik.core.symbol.Symbol
import verik.core.vkx.VkxPortType

enum class ItPortType {
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT;

    companion object {

        operator fun invoke(portType: VkxPortType): ItPortType {
            return when (portType) {
                VkxPortType.INPUT -> INPUT
                VkxPortType.OUTPUT -> OUTPUT
                VkxPortType.INOUT -> INOUT
                VkxPortType.INTERF -> INTERF
                VkxPortType.MODPORT -> MODPORT
            }
        }
    }
}

data class ItPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val portType: ItPortType,
        val typeInstance: ItTypeInstance
): ItDeclaration
