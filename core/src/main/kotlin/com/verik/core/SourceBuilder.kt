package com.verik.core

import java.util.stream.IntStream


// Copyright (c) 2020 Francis Wang

inline fun indent(builder: SourceBuilder, block: () -> Unit) {
    builder.indent++
    block()
    builder.indent--
}

class SourceBuilder {

    private val builder = StringBuilder()
    private var newLine = true
    var indent = 0

    fun append(string: String) {
        appendStream(string.chars())
    }

    fun appendln(string: String) {
        appendStream(string.chars())
        appendStream(IntStream.of('\n'.toInt()))
    }

    fun appendln() {
        appendStream(IntStream.of('\n'.toInt()))
    }

    override fun toString() = builder.toString()

    private fun appendStream(chars: IntStream) {
        for (char in chars) {
            if (char == '\n'.toInt()) {
                builder.append("\n")
                newLine = true
            } else {
                if (newLine) {
                    builder.append("  ".repeat(indent))
                    newLine = false
                }
                builder.appendCodePoint(char)
            }
        }
    }
}