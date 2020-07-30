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

package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.indent

data class SvModule(
        val identifier: String,
        val ports: List<SvInstance>,
        val instances: List<SvInstance>,
        val moduleDeclarations: List<SvModuleDeclaration>,
        val continuousAssignments: List<SvContinuousAssignment>,
        val blocks: List<SvBlock>,
        val linePos: LinePos) {

    fun build(builder: SourceBuilder) {
        if (ports.isEmpty()) {
            builder.label(linePos.line)
            builder.appendln("module $identifier;")
        } else {
            builder.label(linePos.line)
            builder.appendln("module $identifier (")

            indent(builder) {
                SvAligner.build(ports.map { it.build() }, ",", "", builder)
            }

            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")

            if (instances.isNotEmpty()) {
                builder.appendln()
                SvAligner.build(instances.map { it.build() }, ";", ";", builder)
            }

            for (moduleDeclaration in moduleDeclarations) {
                builder.appendln()
                moduleDeclaration.build(builder)
            }

            for (continuousAssignment in continuousAssignments) {
                builder.appendln()
                continuousAssignment.build(builder)
            }

            for (block in blocks) {
                builder.appendln()
                block.build(builder)
            }
        }
        builder.appendln()
        builder.appendln("endmodule: $identifier")
    }
}