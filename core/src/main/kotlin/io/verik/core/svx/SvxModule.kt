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
import io.verik.core.main.SourceBuilder
import io.verik.core.main.indent

data class SvxModule(
        override val line: Int,
        val identifier: String,
        val ports: List<SvxPort>,
        val actionBlocks: List<SvxActionBlock>
): Line {

    fun build(builder: SourceBuilder) {
        if (ports.isEmpty()) {
            builder.label(this)
            builder.appendln("module $identifier;")
        } else {
            builder.label(this)
            builder.appendln("module $identifier (")

            indent(builder) {
                SvxAligner.build(ports.map { it.build() }, ",", "", builder)
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