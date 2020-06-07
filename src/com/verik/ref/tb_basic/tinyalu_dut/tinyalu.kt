package com.verik.ref.tb_basic.tinyalu_dut

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@Circuit class tinyalu: Component {

    // IO
    @In  var A      = Unsigned(8)
    @In  var B      = Unsigned(8)
    @In  var clk    = Bool()
    @In  var op     = Bit(3)
    @In  var reset  = Bool()
    @In  var start  = Bool()
    @Out var done   = Bool()
    @Out var result = Unsigned(16)

    // INTERNAL
    @Wire var done_aax      = Bool()
    @Wire var done_mult     = Bool()
    @Wire var result_aax    = Unsigned(16)
    @Wire var result_mult   = Unsigned(16)
    @Wire var start_single  = Bool()
    @Wire var start_mult    = Bool()
    @Wire var done_internal = Bool()

    val single_cycle = add_and_xor()
    @Always fun connect_single_cycle() {
        val m = single_cycle
        m.A        con A
        m.B        con B
        m.clk      con clk
        m.op       con op
        m.reset    con reset
        m.start    con start_single
        done_aax   con m.done_aax
        result_aax con m.result_aax
    }

    val three_cycle = three_cycle()
    @Always fun connect_three_cycle() {
        val m = three_cycle
        m.A         con A
        m.B         con B
        m.clk       con clk
        m.reset     con reset
        m.start     con start_mult
        done_mult   con m.done_mult
        result_mult con m.result_mult
    }

    @Always fun start_demux() {
        when (op[2]) {
            Bit("0") -> {
                start_single con start
                start_mult con false
            }
            Bit("1") -> {
                start_single con false
                start_mult con start
            }
        }
    }

    @Always fun result_mux() {
        result con when (op[2]) {
            Bit("0") -> result_aax
            Bit("1") -> result_mult
            else -> null
        }
    }

    @Always fun done_mux() {
        done_internal con when (op[2]) {
            Bit("0") -> done_aax
            Bit("1") -> done_mult
            else -> null
        }
        done con done_internal
    }
}