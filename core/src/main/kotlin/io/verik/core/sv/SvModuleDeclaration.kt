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

import io.verik.core.main.Line
import io.verik.core.main.SourceBuilder
import io.verik.core.main.indent

data class SvConnection(
        override val line: Int,
        val identifier: String,
        val expression: SvExpression
): Line {

    fun build(): String = ".$identifier(${expression.build()})"
}

data class SvModuleDeclaration(
        override val line: Int,
        val moduleType: String,
        val identifier: String,
        val connections: List<SvConnection>
): Line {

    fun build(builder: SourceBuilder) {
        if (connections.isEmpty()) {
            builder.label(this)
            builder.appendln("$moduleType $identifier ();")
        } else {
            builder.label(this)
            builder.appendln("$moduleType $identifier (")
            indent (builder) {
                for (connection in connections.dropLast(1)) {
                    builder.label(connection)
                    builder.appendln("${connection.build()},")
                }
                builder.label(connections.last())
                builder.appendln(connections.last().build())
            }
            builder.appendln(");")
        }
    }
}