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

import verik.core.base.Line

data class SvModule(
        override val line: Int,
        val identifier: String,
        val ports: List<SvPort>,
        val actionBlocks: List<SvActionBlock>
): Line, SvBuildable {

    override fun build(builder: SvSourceBuilder) {
        if (ports.isEmpty()) {
            builder.label(this)
            builder.appendln("module $identifier;")
        } else {
            builder.label(this)
            builder.appendln("module $identifier (")

            indent(builder) {
                val alignedLines = ports.map { it.build() }
                val alignedBlock = SvAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }

            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")

            for (actionBlock in actionBlocks) {
                builder.appendln()
                actionBlock.build(builder)
            }
        }
        builder.appendln()
        builder.appendln("endmodule: $identifier")
    }
}