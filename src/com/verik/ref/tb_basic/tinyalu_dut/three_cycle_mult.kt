package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class three_cycle: Circuit {

    // IO
    @In  val A           = UNum(8)
    @In  val B           = UNum(8)
    @In  val clk         = Bool()
    @In  val reset       = Bool()
    @In  val start       = Bool()
    @Out val done_mult   = Bool()
    @Out val result_mult = UNum(16)

    // INTERNAL
    @Logic val a_int         = UNum(8)
    @Logic val b_int         = UNum(8)
    @Logic val mult1         = UNum(16)
    @Logic val mult2         = UNum(16)
    @Logic val done1         = Bool()
    @Logic val done2         = Bool()
    @Logic val done3         = Bool()
    @Logic val done_mult_int = Bool()

    @Always fun multiplier() {
        on (PosEdge(clk), PosEdge(reset)) {
            if (reset) {
                done_mult_int set false
                done3 set false
                done2 set false
                done1 set false

                a_int set UNum.of("0")
                b_int set UNum.of("0")
                mult1 set UNum.of("0")
                mult2 set UNum.of("0")
                result_mult set UNum.of("0")
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
        done_mult set done_mult_int
    }
}