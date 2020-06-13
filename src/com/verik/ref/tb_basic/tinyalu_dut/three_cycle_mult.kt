package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class three_cycle_mult: Circuit {

    @In  val A           = UNum(8)
    @In  val B           = UNum(8)
    @In  val clk         = Bool()
    @In  val reset       = Bool()
    @In  val start       = Bool()
    @Out val done_mult   = Bool()
    @Out val result_mult = UNum(16)

    val a_int         = UNum(8)
    val b_int         = UNum(8)
    val mult1         = UNum(16)
    val mult2         = UNum(16)
    val done1         = Bool()
    val done2         = Bool()
    val done3         = Bool()
    val done_mult_int = Bool()

    @Seq fun multiplier() {
        on (PosEdge(clk), PosEdge(reset)) {
            if (reset) {
                done_mult_int set false
                done3 set false
                done2 set false
                done1 set false
                (a_int cat b_int) set 0
                (mult1 cat mult2) set 0
                result_mult set 0
            } else {
                a_int set A
                b_int set B
                mult1 set (a_int mul b_int)
                mult2 set mult1
                result_mult set mult2
                done3 set (start && !done_mult_int)
                done2 set (done3 && !done_mult_int)
                done1 set (done2 && !done_mult_int)
                done_mult_int set (done1 && !done_mult_int)
            }
        }
    }

    @Comb fun set_done() {
        done_mult set done_mult_int
    }
}