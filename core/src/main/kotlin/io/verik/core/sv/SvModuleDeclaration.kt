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
import io.verik.core.SourceBuilder
import io.verik.core.indent

data class SvConnection(
        val identifier: String,
        val expression: SvExpression,
        val fileLine: FileLine) {

    fun build(): String = ".$identifier(${expression.build()})"
}

data class SvModuleDeclaration(
        val moduleType: String,
        val identifier: String,
        val connections: List<SvConnection>,
        val fileLine: FileLine
) {

    fun build(builder: SourceBuilder) {
        if (connections.isEmpty()) {
            builder.label(fileLine.line)
            builder.appendln("$moduleType $identifier ();")
        } else {
            builder.label(fileLine.line)
            builder.appendln("$moduleType $identifier (")
            indent (builder) {
                for (connection in connections.dropLast(1)) {
                    builder.label(connection.fileLine.line)
                    builder.appendln("${connection.build()},")
                }
                builder.label(connections.last().fileLine.line)
                builder.appendln(connections.last().build())
            }
            builder.appendln(");")
        }
    }
}