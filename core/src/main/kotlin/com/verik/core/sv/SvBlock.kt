package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.indent

// Copyright (c) 2020 Francis Wang

enum class SvBlockType {
    ALWAYS_COMB,
    ALWAYS_FF,
    INITIAL
}

data class SvBlock (
        val type: SvBlockType,
        val statements: List<SvStatement>,
        val linePos: LinePos
) {

    fun build(builder: SourceBuilder) {
        builder.label(linePos.line)
        when (type) {
            SvBlockType.ALWAYS_COMB -> throw SvBuildException("always_comb block not supported", linePos)
            SvBlockType.ALWAYS_FF -> throw SvBuildException("always_ff block not supported", linePos)
            SvBlockType.INITIAL -> {
                builder.appendln("initial begin")
            }
        }
        indent (builder) {
            for (statement in statements) {
                statement.build(builder)
            }
        }
        builder.appendln("end")
    }
}