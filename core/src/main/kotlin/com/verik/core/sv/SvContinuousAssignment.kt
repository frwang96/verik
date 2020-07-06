package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.label

// Copyright (c) 2020 Francis Wang

data class SvContinuousAssignment(val expression: SvExpression, val linePos: LinePos) {

    fun build(sourceBuilder: SourceBuilder) {
        label (sourceBuilder, linePos.line) {
            sourceBuilder.appendln("assign ${expression.build()};")
        }
    }
}

