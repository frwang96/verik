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

import io.verik.core.LinePos
import io.verik.core.LinePosException

enum class SvInstanceUsageType {
    REGULAR,
    INPUT,
    OUTPUT;

    fun isPort() = this != REGULAR

    fun build() = when (this) {
        REGULAR -> ""
        INPUT -> "input"
        OUTPUT -> "output"
    }
}

data class SvInstance(
        val usageType: SvInstanceUsageType,
        val packed: Int?,
        val identifier: String,
        val unpacked: List<Int>,
        val linePos: LinePos
) {

    fun build(): SvAlignerLine {
        val packedString = when (packed) {
            null -> ""
            0 -> throw LinePosException("packed dimension cannot be zero", linePos)
            else -> "[${packed-1}:0]"
        }

        val unpackedString = unpacked.joinToString(separator = "") {
            if (it == 0) throw LinePosException("packed dimension cannot be zero", linePos)
            else "[0:${it-1}]"
        }

        return SvAlignerLine(listOf(usageType.build(), "logic", packedString, identifier, unpackedString), linePos)
    }
}