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

package verik.core.sv

import verik.core.main.Line

enum class SvPortType {
    INPUT,
    OUTPUT;

    fun build(): String {
        return when (this) {
            INPUT -> "input"
            OUTPUT -> "output"
        }
    }
}

data class SvPort(
        override val line: Int,
        val portType: SvPortType,
        val typeInstance: SvTypeInstance,
        val identifier: String
): Line {

    fun build(): SvAlignedLine {
        return SvAlignedLine(
                line,
                listOf(
                        portType.build(),
                        typeInstance.identifier,
                        typeInstance.packed,
                        identifier,
                        typeInstance.unpacked
                )
        )
    }
}