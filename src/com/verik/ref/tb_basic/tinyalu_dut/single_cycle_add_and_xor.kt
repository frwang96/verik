package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class add_and_xor: Circuit {

    // IO
    @In  val A          = UNum(8)
    @In  val B          = UNum(8)
    @In  val clk        = Bool()
    @In  val op         = Bits(3)
    @In  val reset      = Bool()
    @In  val start      = Bool()
    @Out val done_aax   = Bool()
    @Out val result_aax = UNum(16)

    @Always fun single_cycle_ops() {
        on (PosEdge(clk)) {
            if (reset) { // Synchronous reset
                result_aax set Value(0)
            } else {
                if (start) {
                    result_aax set when (op) {
                        Value(Bits(3), "001") -> ext(16, A add B)
                        Value(Bits(3), "010") -> ext(16, A and B)
                        Value(Bits(3), "011") -> ext(16, A xor B)
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
                done_aax set (start && !!op)
            }
        }
    }
}
