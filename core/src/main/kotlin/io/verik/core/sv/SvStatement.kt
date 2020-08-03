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

sealed class SvStatement(open val fileLine: FileLine) {

    abstract fun build(builder: SourceBuilder)
}

data class SvExpressionStatement(
        override val fileLine: FileLine,
        val expression: SvExpression
): SvStatement(fileLine) {

    override fun build(builder: SourceBuilder) {
        builder.label(fileLine.line)
        builder.appendln("${expression.build()};")
    }
}

data class SvLoopStatement(
        override val fileLine: FileLine,
        val identifier: String,
        val statements: List<SvStatement>
): SvStatement(fileLine) {

    override fun build(builder: SourceBuilder) {
        builder.label(fileLine.line)
        builder.appendln("$identifier begin")
        indent(builder) {
            for (statement in statements) {
                statement.build(builder)
            }
        }
        builder.appendln("end")
    }
}

data class SvConditionalStatement(
        override val fileLine: FileLine,
        val expression: SvExpression,
        val ifStatements: List<SvStatement>,
        val elseStatements: List<SvStatement>
): SvStatement(fileLine) {

    override fun build(builder: SourceBuilder) {
        builder.label(fileLine.line)
        builder.appendln("if (${expression.build()}) begin")
        indent(builder) {
            for (statement in ifStatements) {
                statement.build(builder)
            }
        }

        if (elseStatements.isEmpty()) {
            builder.appendln("end")
        } else {
            if (elseStatements.size == 1 && elseStatements[0] is SvConditionalStatement) {
                builder.label(elseStatements[0].fileLine.line)
                builder.append("end else ")
                elseStatements[0].build(builder)
            } else {
                builder.appendln("end else begin")
                indent(builder) {
                    for (statement in elseStatements) {
                        statement.build(builder)
                    }
                }
                builder.appendln("end")
            }
        }
    }
}