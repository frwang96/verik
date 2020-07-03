package com.verik.ref.basic_tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _tinyalu: _circuit {

    @input  val A      = _uint(8)
    @input  val B      = _uint(8)
    @input  val clk    = _bool()
    @input  val op     = _uint(3)
    @input  val reset  = _bool()
    @input  val start  = _bool()
    @output val done   = _bool()
    @output val result = _uint(16)

    val done_aax      = _bool()
    val done_mult     = _bool()
    val result_aax    = _uint(16)
    val result_mult   = _uint(16)
    val start_single  = _bool()
    val start_mult    = _bool()
    val done_internal = _bool()

    @comp val add_and_xor = _add_and_xor() with {
        A; B; clk; op; reset
        done_aax; result_aax
        it.start con start_single
    }

    @comp val pipelined_mult = _pipelined_mult() with {
        A; B; clk; reset
        done_mult; result_mult
        it.start con start_mult
    }

    @put fun start_demux() {
        if (op[2]) {
            start_single put false
            start_mult put start
        } else {
            start_single put start
            start_mult put false
        }
    }

    @put fun result_mux() {
        result put if (op[2]) result_mult else result_aax
    }

    @put fun done_mux() {
        done_internal put if(op[2]) done_mult else done_aax
        done put done_internal
    }
}