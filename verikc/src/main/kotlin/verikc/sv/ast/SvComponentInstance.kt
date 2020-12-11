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

package verikc.sv.ast

import verikc.base.ast.Line
import verikc.sv.build.SvAlignedBlock
import verikc.sv.build.SvBuildable
import verikc.sv.build.SvSourceBuilder
import verikc.sv.build.indent

data class SvComponentInstance(
        val line: Line,
        val identifier: String,
        val typeIdentifier: String,
        val connections: List<SvConnection>
): SvBuildable {

    override fun build(builder: SvSourceBuilder) {
        builder.label(line)
        if (connections.isNotEmpty()) {
            builder.appendln("$typeIdentifier $identifier (")

            indent(builder) {
                val alignedLines = connections.map { it.build() }
                val alignedBlock = SvAlignedBlock(alignedLines, ",", "")
                alignedBlock.build(builder)
            }

            builder.appendln(");")
        } else {
            builder.appendln("$typeIdentifier $identifier ();")
        }
    }
}
