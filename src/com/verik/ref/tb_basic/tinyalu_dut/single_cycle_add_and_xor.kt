package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class add_and_xor: Circuit {

    // IO
    @In       var A          = Unsigned(8)
    @In       var B          = Unsigned(8)
    @In       var clk        = Bool()
    @In       var op         = Bit(3)
    @In       var reset      = Bool()
    @In       var start      = Bool()
    @Out @Reg var done_aax   = Bool()
    @Out @Reg var result_aax = Unsigned(16)

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
