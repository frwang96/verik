package mockups.tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _add_and_xor: _circuit {
    @input  val a          = _uint(8)
    @input  val b          = _uint(8)
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
                        uint(0b001) -> ext(16, a add b)
                        uint(0b010) -> ext(16, a and b)
                        uint(0b011) -> ext(16, a xor b)
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
