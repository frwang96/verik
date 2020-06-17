package com.verik.ref.basic_tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _add_and_xor: _circuit {

    @input  val A          = _uint(8)
    @input  val B          = _uint(8)
    @input  val clk        = _bool()
    @input  val op         = _bits(3)
    @input  val reset      = _bool()
    @input  val start      = _bool()
    @output val done_aax   = _bool()
    @output val result_aax = _uint(16)

    @seq fun and_and_xor() {
        on (posedge(clk)) {
            if (reset) { // Synchronous reset
                result_aax put 0
            } else {
                if (start) {
                    result_aax put when (op) {
                        _bits.of("3b'001") -> ext(16, A add B)
                        _bits.of("3b'010") -> ext(16, A and B)
                        _bits.of("3b'011") -> ext(16, A xor B)
                        else -> null
                    }
                }
            }
        }
    }

    @seq fun put_done() {
        on(posedge(clk), posedge(reset)) {
            if (reset) { // Asynchronous reset
                done_aax put true
            } else {
                done_aax put (start && !!op)
            }
        }
    }
}
