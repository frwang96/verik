package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.indent

// Copyright (c) 2020 Francis Wang

data class SvModule(
        val identifier: String,
        val ports: List<SvPort>,
        val moduleDeclarations: List<SvModuleDeclaration>,
        val continuousAssignments: List<SvContinuousAssignment>,
        val linePos: LinePos) {

    fun build(builder: SourceBuilder) {
        if (ports.isEmpty()) {
            builder.label(linePos.line)
            builder.appendln("module $identifier;")
        } else {
            builder.label(linePos.line)
            builder.appendln("module $identifier (")
            indent(builder) {
                SvAligner.build(ports.map { it.build() }, ",", "", builder)
            }
            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")
            for (moduleDeclaration in moduleDeclarations) {
                builder.appendln()
                moduleDeclaration.build(builder)
            }
            for (continuousAssignment in continuousAssignments) {
                builder.appendln()
                continuousAssignment.build(builder)
            }
        }
        builder.appendln()
        builder.appendln("endmodule: $identifier")
    }
}