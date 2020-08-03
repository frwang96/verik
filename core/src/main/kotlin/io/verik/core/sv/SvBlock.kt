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

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.SourceBuilder
import io.verik.core.indent

enum class SvBlockType {
    ALWAYS_COMB,
    ALWAYS_FF,
    INITIAL
}

data class SvBlock (
        val type: SvBlockType,
        val sensitivityEntries: List<SvSensitivityEntry>,
        val statements: List<SvStatement>,
        val fileLine: FileLine
) {

    fun build(builder: SourceBuilder) {
        builder.label(fileLine.line)
        when (type) {
            SvBlockType.ALWAYS_COMB -> {
                if (sensitivityEntries.isNotEmpty()) {
                    throw FileLineException("sensitivity list not permitted for always_comb block", fileLine)
                }
                builder.appendln("always_comb begin")
            }
            SvBlockType.ALWAYS_FF -> {
                if (sensitivityEntries.isEmpty()) {
                    throw FileLineException("sensitivity list required for always_ff block", fileLine)
                }
                val sensitivityString = sensitivityEntries.joinToString(separator = " or ") { it.build() }
                builder.appendln("always_ff @($sensitivityString) begin")
            }
            SvBlockType.INITIAL -> {
                if (sensitivityEntries.isNotEmpty()) {
                    throw FileLineException("sensitivity list not permitted for initial block", fileLine)
                }
                builder.appendln("initial begin")
            }
        }
        indent (builder) {
            for (statement in statements) {
                statement.build(builder)
            }
        }
        builder.appendln("end")
    }
}