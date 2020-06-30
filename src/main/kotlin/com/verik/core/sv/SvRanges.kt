package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

data class SvRanges(val ranges: List<Pair<Int, Int>>) {

    fun build(): String {
        return ranges.joinToString(separator = "") { (start, end) -> "[$start:$end]" }
    }
}