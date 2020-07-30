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

data class SvConnection(
        val identifier: String,
        val expression: SvExpression,
        val linePos: LinePos) {

    fun build(): String = ".$identifier(${expression.build()})"
}

data class SvModuleDeclaration(
        val moduleType: String,
        val identifier: String,
        val connections: List<SvConnection>,
        val linePos: LinePos
) {

    fun build(builder: SourceBuilder) {
        if (connections.isEmpty()) {
            builder.label(linePos.line)
            builder.appendln("$moduleType $identifier ();")
        } else {
            builder.label(linePos.line)
            builder.appendln("$moduleType $identifier (")
            indent (builder) {
                for (connection in connections.dropLast(1)) {
                    builder.label(connection.linePos.line)
                    builder.appendln("${connection.build()},")
                }
                builder.label(connections.last().linePos.line)
                builder.appendln(connections.last().build())
            }
            builder.appendln(");")
        }
    }
}