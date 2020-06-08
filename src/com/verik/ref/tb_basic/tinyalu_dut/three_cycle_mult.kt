package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class three_cycle: Circuit {

    // IO
    @In       var A           = Unsigned(8)
    @In       var B           = Unsigned(8)
    @In       var clk         = Bool()
    @In       var reset       = Bool()
    @In       var start       = Bool()
    @Out      var done_mult   = Bool()
    @Out @Reg var result_mult = Unsigned(16)

    // INTERNAL
    @Reg var a_int         = Unsigned(8)
    @Reg var b_int         = Unsigned(8)
    @Reg var mult1         = Unsigned(16)
    @Reg var mult2         = Unsigned(16)
    @Reg var done1         = Bool()
    @Reg var done2         = Bool()
    @Reg var done3         = Bool()
    @Reg var done_mult_int = Bool()

    @Always fun multiplier() {
        on (PosEdge(clk), PosEdge(reset)) {
            if (reset) {
                done_mult_int set false
                done3 set false
                done2 set false
                done1 set false

                a_int set Unsigned("0")
                b_int set Unsigned("0")
                mult1 set Unsigned("0")
                mult2 set Unsigned("0")
                result_mult set Unsigned("0")
            } else {
                a_int set A
                b_int set B
                mult1 set a_int * b_int
                mult2 set mult1
                result_mult set mult2
                done3 set (start && !done_mult_int)
                done2 set (done3 && !done_mult_int)
                done1 set (done2 && !done_mult_int)
                done_mult_int set (done1 && !done_mult_int)
            }
        }
    }

    @Always fun set_done() {
        done_mult con done_mult_int
    }
}