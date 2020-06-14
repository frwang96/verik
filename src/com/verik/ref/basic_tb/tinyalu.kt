package com.verik.ref.basic_tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _tinyalu: _circuit {

    @input  val A      = _uint(8)
    @input  val B      = _uint(8)
    @input  val clk    = _bool()
    @input  val op     = _bits(3)
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

    val add_and_xor = _add_and_xor()
    @connect fun single_cycle() {
        add_and_xor con_name list(A, B, clk, op, reset, done_aax, result_aax)
        add_and_xor.start con start_single
    }

    val pipelined_mult = _pipelined_mult()
    @connect fun three_cycle() {
        pipelined_mult con_name list(A, B, clk, reset, done_mult, result_mult)
        pipelined_mult.start con start_mult
    }

    @comb fun start_demux() {
        if (op[2]) {
            start_single set false
            start_mult set start
        } else {
            start_single set start
            start_mult set false
        }
    }

    @comb fun result_mux() {
        result set if (op[2]) result_mult else result_aax
    }

    @comb fun done_mux() {
        done_internal set if(op[2]) done_mult else done_aax
        done set done_internal
    }
}