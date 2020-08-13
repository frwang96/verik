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

package io.verik.core.svx

import io.verik.core.main.Line

enum class SvxPortType {
    INPUT,
    OUTPUT;

    fun build(): String {
        return when (this) {
            INPUT -> "input"
            OUTPUT -> "output"
        }
    }
}

data class SvxPort(
        override val line: Int,
        val portType: SvxPortType,
        val contentType: SvxType,
        val identifier: String
): Line {

    fun build(): SvxAlignerLine {
        return SvxAlignerLine(line, listOf(
                portType.build(),
                contentType.identifier,
                contentType.packed,
                identifier,
                contentType.unpacked
        ))
    }
}