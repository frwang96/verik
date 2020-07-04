package com.verik.core.sv

import com.verik.core.SourceBuilder
import com.verik.core.indent

// Copyright (c) 2020 Francis Wang

data class SvModule(val name: String, val ports: List<SvPort>, val declarations: List<SvDeclaration>) {

    fun build(builder: SourceBuilder) {
        builder.append("module $name")
        if (ports.isEmpty()) {
            builder.appendln(";")
        } else {
            builder.appendln(" (")
            indent(builder) {
                SvAligner.build(ports.map { it.build() }, ",", "", builder)
            }
            builder.appendln(");")
        }
        indent(builder) {
            builder.appendln("timeunit 1ns/1ns;")
            for (declaration in declarations) {
                builder.appendln(declaration.build())
            }
        }
        builder.appendln("endmodule: $name")
    }
}