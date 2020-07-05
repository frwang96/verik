package com.verik.core

// Copyright (c) 2020 Francis Wang

data class LinePos(val line: Int, val pos: Int) {

    fun advance(string: String): LinePos {
        val lines = string.count { it == '\n' }
        return if (lines == 0) {
            LinePos(line, pos + string.length)
        } else {
            LinePos(line + lines, string.length - string.lastIndexOf('\n') - 1)
        }
    }

    override fun toString(): String {
        return "($line, $pos)"
    }
}