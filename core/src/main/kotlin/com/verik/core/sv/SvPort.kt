package com.verik.core.sv

import com.verik.core.LinePos

// Copyright (c) 2020 Francis Wang

enum class SvPortType {
    INPUT,
    OUTPUT;

    fun build() = when(this) {
        INPUT -> "input"
        OUTPUT -> "output"
    }
}

data class SvPort(
        val portType: SvPortType,
        val packed: List<SvRange>,
        val identifier: String,
        val unpacked: List<SvRange>,
        val linePos: LinePos
) {

    fun build(): SvAlignerLine {
        return SvAlignerLine(listOf(portType.build(), "logic", packed.build(), identifier, unpacked.build()), linePos)
    }
}