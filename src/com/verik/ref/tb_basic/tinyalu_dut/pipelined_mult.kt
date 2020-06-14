package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _pipelined_mult: _circuit {

    @input  val A           = _uint(8)
    @input  val B           = _uint(8)
    @input  val clk         = _bool()
    @input  val reset       = _bool()
    @input  val start       = _bool()
    @output val done_mult   = _bool()
    @output val result_mult = _uint(16)

    val a_int         = _uint(8)
    val b_int         = _uint(8)
    val mult1         = _uint(16)
    val mult2         = _uint(16)
    val done1         = _bool()
    val done2         = _bool()
    val done3         = _bool()
    val done_mult_int = _bool()

    @seq fun pipelined_mult() {
        on (posedge(clk), posedge(reset)) {
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

    @comb fun set_done() {
        done_mult set done_mult_int
    }
}