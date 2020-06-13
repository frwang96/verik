package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class tinyalu: Circuit {

    @In  val A      = UNum(8)
    @In  val B      = UNum(8)
    @In  val clk    = Bool()
    @In  val op     = Bits(3)
    @In  val reset  = Bool()
    @In  val start  = Bool()
    @Out val done   = Bool()
    @Out val result = UNum(16)

    val done_aax      = Bool()
    val done_mult     = Bool()
    val result_aax    = UNum(16)
    val result_mult   = UNum(16)
    val start_single  = Bool()
    val start_mult    = Bool()
    val done_internal = Bool()

    val single_cycle = add_and_xor()
    @Connect fun single_cycle() {
        single_cycle con listOf(A, B, clk, op, reset, done_aax, result_aax)
        single_cycle.start con start_single
    }

    val three_cycle = three_cycle_mult()
    @Connect fun three_cycle() {
        three_cycle con listOf(A, B, clk, reset, done_mult, result_mult)
        three_cycle.start con start_mult
    }

    @Comb fun start_demux() {
        if (op[2]) {
            start_single set false
            start_mult set start
        } else {
            start_single set start
            start_mult set false
        }
    }

    @Comb fun result_mux() {
        result set if (op[2]) result_mult else result_aax
    }

    @Comb fun done_mux() {
        done_internal set if(op[2]) done_mult else done_aax
        done set done_internal
    }
}