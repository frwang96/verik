package examples.print

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@top class _print: _module {

    val clk   = _bool()
    val count = _uint(8)

    @initial fun clk() {
        clk put false
        forever {
            wait(1)
            println("count=$count")
            clk put !clk
        }
    }

    @reg fun count() {
        on (posedge(clk)) {
            count reg count + 1
        }
    }

    @initial fun main() {
        wait(64)
        finish()
    }
}