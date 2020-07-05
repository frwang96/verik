package com.verik.core.sv

import com.verik.core.SourceBuilder

// Copyright (c) 2020 Francis Wang

data class SvFile(val module: SvModule) {

    fun build(builder: SourceBuilder) {
        module.build(builder)
    }
}