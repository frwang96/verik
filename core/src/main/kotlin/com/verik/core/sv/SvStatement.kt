package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.indent

// Copyright (c) 2020 Francis Wang

sealed class SvStatement(open val linePos: LinePos) {

    abstract fun build(builder: SourceBuilder)
}

data class SvExpressionStatement(
        override val linePos: LinePos,
        val expression: SvExpression
): SvStatement(linePos) {

    override fun build(builder: SourceBuilder) {
        builder.label(linePos.line)
        builder.appendln("${expression.build()};")
    }
}

data class SvLoopStatement(
        override val linePos: LinePos,
        val identifier: String,
        val statements: List<SvStatement>
): SvStatement(linePos) {

    override fun build(builder: SourceBuilder) {
        builder.label(linePos.line)
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
        override val linePos: LinePos,
        val expression: SvExpression,
        val ifStatements: List<SvStatement>,
        val elseStatements: List<SvStatement>
): SvStatement(linePos) {

    override fun build(builder: SourceBuilder) {
        builder.label(linePos.line)
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
                builder.label(elseStatements[0].linePos.line)
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