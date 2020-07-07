package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

data class SvRange(val start: Int, val end: Int) {

    fun build() = "[$start:$end]"
}

fun List<SvRange>.build(): String {
    return joinToString(separator = "") { it.build() }
}
