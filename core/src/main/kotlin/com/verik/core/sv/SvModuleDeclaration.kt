package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.indent

// Copyright (c) 2020 Francis Wang

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