package com.verik.core

// Copyright (c) 2020 Francis Wang

class LinePosException(msg: String, val linePos: LinePos): Exception(
        if (linePos == LinePos.ZERO) msg
        else "$linePos $msg"
)

data class LinePos(val line: Int, val pos: Int) {

    override fun toString(): String {
        return "($line, $pos)"
    }

    companion object {
        val ZERO = LinePos(0, 0)
    }
}