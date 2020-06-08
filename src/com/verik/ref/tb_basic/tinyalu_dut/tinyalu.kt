package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class tinyalu: Circuit {

    // IO
    @In  val A      = UNum(8)
    @In  val B      = UNum(8)
    @In  val clk    = Bool()
    @In  val op     = Bit(3)
    @In  val reset  = Bool()
    @In  val start  = Bool()
    @Out val done   = Bool()
    @Out val result = UNum(16)

    // INTERNAL
    @Logic val done_aax      = Bool()
    @Logic val done_mult     = Bool()
    @Logic val result_aax    = UNum(16)
    @Logic val result_mult   = UNum(16)
    @Logic val start_single  = Bool()
    @Logic val start_mult    = Bool()
    @Logic val done_internal = Bool()

    val single_cycle = add_and_xor()
    @Always fun connect_single_cycle() {
        single_cycle.connect(A, B, clk, op, reset)
        single_cycle.start set start_single
        done_aax           set single_cycle.done_aax
        result_aax         set single_cycle.result_aax
    }

    val three_cycle = three_cycle()
    @Always fun connect_three_cycle() {
        three_cycle.connect(A, B, clk, reset)
        three_cycle.start set start_mult
        done_mult         set three_cycle.done_mult
        result_mult       set three_cycle.result_mult
    }

    @Always fun start_demux() {
        when (op[2]) {
            Bit("0") -> {
                start_single set start
                start_mult set false
            }
            Bit("1") -> {
                start_single set false
                start_mult set start
            }
        }
    }

    @Always fun result_mux() {
        result set when (op[2]) {
            Bit("0") -> result_aax
            Bit("1") -> result_mult
            else -> null
        }
    }

    @Always fun done_mux() {
        done_internal set when (op[2]) {
            Bit("0") -> done_aax
            Bit("1") -> done_mult
            else -> null
        }
        done set done_internal
    }
}