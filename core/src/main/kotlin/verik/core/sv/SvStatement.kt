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

import verik.core.main.Line
import verik.core.main.SourceBuilder
import verik.core.main.indent

sealed class SvStatement(
        override val line: Int
): Line {

    abstract fun build(builder: SourceBuilder)
}

data class SvExpressionStatement(
        override val line: Int,
        val expression: SvExpression
): SvStatement(line) {

    override fun build(builder: SourceBuilder) {
        builder.label(this)
        builder.appendln("${expression.build()};")
    }
}

data class SvLoopStatement(
        override val line: Int,
        val identifier: String,
        val statements: List<SvStatement>
): SvStatement(line) {

    override fun build(builder: SourceBuilder) {
        builder.label(this)
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
        override val line: Int,
        val expression: SvExpression,
        val ifStatements: List<SvStatement>,
        val elseStatements: List<SvStatement>
): SvStatement(line) {

    override fun build(builder: SourceBuilder) {
        builder.label(this)
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
                builder.label(elseStatements[0])
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