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
