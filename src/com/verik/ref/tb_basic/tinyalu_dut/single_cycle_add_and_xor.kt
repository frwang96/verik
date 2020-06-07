package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@Synthesizable @Module class add_and_xor {

    // IO
    @Input     var A          = Unsigned(8)
    @Input     var B          = Unsigned(8)
    @Input     var clk        = Bool()
    @Input     var op         = Bit(3)
    @Input     var reset      = Bool()
    @Input     var start      = Bool()
    @OutputReg var done_aax   = Bool()
    @OutputReg var result_aax = Unsigned(16)

    @Always fun single_cycle_ops() {
        on (PosEdge(clk)) {
            if (reset) { // Synchronous reset
                result_aax set Unsigned("0")
            } else {
                if (start) {
                    result_aax set when (op) {
                        Bit("3'b001") -> A + B
                        Bit("3'b010") -> A and B
                        Bit("3'b011") -> A xor B
                        else -> null
                    }
                }
            }
        }
    }

    @Always fun set_done() {
        on(PosEdge(clk), PosEdge(reset)) {
            if (reset) { // Asynchronous reset
                done_aax set true
            } else {
                done_aax set (start && op != Bit("3'b000"))
            }
        }
    }
}
