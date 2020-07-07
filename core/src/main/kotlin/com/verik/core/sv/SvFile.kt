package com.verik.core.sv

import com.verik.core.SourceBuilder

// Copyright (c) 2020 Francis Wang

data class SvFile(val modules: List<SvModule>) {

    fun build(builder: SourceBuilder) {
        if (modules.isNotEmpty()) {
            for (module in modules.dropLast(1)) {
                module.build(builder)
                builder.appendln()
            }
            modules.last().build(builder)
        }
    }
}