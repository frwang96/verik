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

package io.verik.core.sv

import io.verik.core.Line
import io.verik.core.LineException

enum class SvInstancePortType {
    NONE,
    INPUT,
    OUTPUT;

    fun isPort() = this != NONE

    fun build() = when (this) {
        NONE -> ""
        INPUT -> "input"
        OUTPUT -> "output"
    }
}

data class SvInstanceDeclaration(
        override val line: Int,
        val portType: SvInstancePortType,
        val packed: Int?,
        val identifier: String,
        val unpacked: List<Int>
): Line {

    fun build(): SvAlignerLine {
        val packedString = when (packed) {
            null -> ""
            0 -> throw LineException("packed dimension cannot be zero", this)
            else -> "[${packed-1}:0]"
        }

        val unpackedString = unpacked.joinToString(separator = "") {
            if (it == 0) throw LineException("packed dimension cannot be zero", this)
            else "[0:${it-1}]"
        }

        return SvAlignerLine(line, listOf(portType.build(), "logic", packedString, identifier, unpackedString))
    }
}