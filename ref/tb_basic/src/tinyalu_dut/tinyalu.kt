package tinyalu_dut

import verik.*

// Copyright (c) 2020 Francis Wang

@Synthesizable @Module class tinyalu {

    // IO
    @Input  var A             = Unsigned(8)
    @Input  var B             = Unsigned(8)
    @Input  var clk           = Bool()
    @Input  var op            = Bit(3)
    @Input  var reset         = Bool()
    @Input  var start         = Bool()
    @Output var done          = Bool()
    @Output var result        = Unsigned(16)

    // INTERNAL
    @Wire   var done_aax      = Bool()
    @Wire   var done_mult     = Bool()
    @Wire   var result_aax    = Unsigned(16)
    @Wire   var result_mult   = Unsigned(16)
    @Wire   var start_single  = Bool()
    @Wire   var start_mult    = Bool()
    @Wire   var done_internal = Bool()

    @Module val single_cycle = add_and_xor()
    @Always fun connect_single_cycle() {
        val m = single_cycle
        m.A          set A
        m.B          set B
        m.clk        set clk
        m.op         set op
        m.reset      set reset
        m.start      set start_single
        m.done_aax   get done_aax
        m.result_aax get result_aax
    }

    @Module val three_cycle = three_cycle()
    @Always fun connect_three_cycle() {
        val m = three_cycle
        m.A           set A
        m.B           set B
        m.clk         set clk
        m.reset       set reset
        m.start       set start_mult
        m.done_mult   get done_mult
        m.result_mult get result_mult
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
        done = done_internal
    }
}