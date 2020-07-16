package examples.print

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@top class _print: _module {

    val clk   = _bool()
    val reset = _bool()
    val count = _uint(8)

    @reg fun count() {
        on (posedge(clk)) {
            println("count=$count")
            if (reset) {
                count reg 0
            } else {
                count reg count + 1
            }
        }
    }

    @initial fun clk() {
        clk put false
        forever {
            wait(1)
            clk put !clk
        }
    }

    @initial fun reset() {
        reset put true
        wait(4)
        reset put false
        wait(64)
        finish()
    }
}