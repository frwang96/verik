package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder

// Copyright (c) 2020 Francis Wang

data class SvContinuousAssignment(val expression: SvExpression, val linePos: LinePos) {

    fun build(builder: SourceBuilder) {
        builder.label(linePos.line)
        builder.appendln("assign ${expression.build()};")
    }
}

