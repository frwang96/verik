package examples.print

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@top class _print: _module {

    val clk = _bool()
    @initial fun clk() {
        clk put false
        forever {
            wait(1)
            println("clk=$clk")
            clk put !clk
        }
    }

    @initial fun print() {
        wait(16)
        finish()
    }
}