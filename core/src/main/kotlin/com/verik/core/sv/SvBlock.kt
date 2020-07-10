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
        val sensitivityEntries: List<SvSensitivityEntry>,
        val statements: List<SvStatement>,
        val linePos: LinePos
) {

    fun build(builder: SourceBuilder) {
        builder.label(linePos.line)
        when (type) {
            SvBlockType.ALWAYS_COMB -> {
                if (sensitivityEntries.isNotEmpty()) {
                    throw SvBuildException("sensitivity list not permitted for always_comb block", linePos)
                }
                builder.appendln("always_comb begin")
            }
            SvBlockType.ALWAYS_FF -> {
                if (sensitivityEntries.isEmpty()) {
                    throw SvBuildException("sensitivity list required for always_ff block", linePos)
                }
                val sensitivityString = sensitivityEntries.joinToString(separator = " or ") { it.build() }
                builder.appendln("always_ff @($sensitivityString) begin")
            }
            SvBlockType.INITIAL -> {
                if (sensitivityEntries.isNotEmpty()) {
                    throw SvBuildException("sensitivity list not permitted for initial block", linePos)
                }
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