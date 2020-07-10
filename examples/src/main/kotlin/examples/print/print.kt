package examples.print

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@top class _print: _module {

    val clk = _bool()
    @initial fun clk() {
        clk put false
    }

    @initial fun print() {
        vk_wait(1)
        vk_println("clk=$clk")
        vk_finish()
    }
}