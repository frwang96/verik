package com.verik.core.sv

import com.verik.core.SourceBuilder
import com.verik.core.label

// Copyright (c) 2020 Francis Wang

data class SvContinuousAssignment(val expression: SvExpression, val line: Int) {

    fun build(sourceBuilder: SourceBuilder) {
        label (sourceBuilder, line) {
            sourceBuilder.appendln("assign ${expression.build()};")
        }
    }
}

