package com.verik.core.sv

import com.verik.core.SourceBuilder
import com.verik.core.indent
import com.verik.core.label

// Copyright (c) 2020 Francis Wang

data class SvModule(
        val name: String,
        val ports: List<SvPort>,
        val continuousAssignments: List<SvContinuousAssignment>,
        val line: Int) {

    fun build(builder: SourceBuilder) {
        if (ports.isEmpty()) {
            label (builder, line) {
                builder.appendln("module $name;")
            }
        } else {
            label (builder, line) {
                builder.appendln("module $name (")
            }
            indent(builder) {
                SvAligner.build(ports.map { it.build() }, ",", "", builder)
            }
            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns / 1ns;")
            for (continuousAssignment in continuousAssignments) {
                builder.appendln()
                continuousAssignment.build(builder)
            }
        }
        builder.appendln()
        builder.appendln("endmodule: $name")
    }
}