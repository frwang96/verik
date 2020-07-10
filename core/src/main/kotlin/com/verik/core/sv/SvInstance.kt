package com.verik.core.sv

import com.verik.core.LinePos

// Copyright (c) 2020 Francis Wang

enum class SvInstanceUsageType {
    REGULAR,
    INPUT,
    OUTPUT;

    fun isPort() = this != REGULAR

    fun build() = when (this) {
        REGULAR -> ""
        INPUT -> "input"
        OUTPUT -> "output"
    }
}

data class SvInstance(
        val usageType: SvInstanceUsageType,
        val packed: List<SvRange>,
        val identifier: String,
        val unpacked: List<SvRange>,
        val linePos: LinePos
) {

    fun build(): SvAlignerLine {
        return SvAlignerLine(listOf(usageType.build(), "logic", packed.build(), identifier, unpacked.build()), linePos)
    }
}