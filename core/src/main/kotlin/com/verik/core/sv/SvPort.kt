package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

enum class SvPortType {
    INPUT,
    OUTPUT;

    fun build() = when(this) {
        INPUT -> "input"
        OUTPUT -> "output"
    }
}

data class SvPort(val portType: SvPortType, val packed: SvRanges, val name: String, val unpacked:SvRanges) {

    fun build(): List<String> {
        return listOf(portType.build(), "logic", packed.build(), name, unpacked.build())
    }
}