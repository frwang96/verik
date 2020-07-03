package com.verik.ref.basic_tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _add_and_xor: _circuit {

    @input  val A          = _uint(8)
    @input  val B          = _uint(8)
    @input  val clk        = _bool()
    @input  val op         = _uint(3)
    @input  val reset      = _bool()
    @input  val start      = _bool()
    @output val done_aax   = _bool()
    @output val result_aax = _uint(16)

    @reg fun reg_result() {
        on (posedge(clk)) {
            if (reset) { // Synchronous reset
                result_aax reg 0
            } else {
                if (start) {
                    result_aax reg when (op) {
                        uint(0b001) -> ext(16, A add B)
                        uint(0b010) -> ext(16, A and B)
                        uint(0b011) -> ext(16, A xor B)
                        else -> null
                    }
                }
            }
        }
    }

    @reg fun reg_done() {
        on(posedge(clk), posedge(reset)) {
            done_aax reg if (reset) true else start && !!op
        }
    }
}
