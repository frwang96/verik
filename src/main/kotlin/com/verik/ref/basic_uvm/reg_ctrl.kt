package com.verik.ref.basic_uvm

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _reg_ctrl(
    val ADDR_WIDTH: Int,
    val DATA_WIDTH: Int,
    val RESET_VAL: _uint = _uint(DATA_WIDTH)
): _circuit {
    @input  val clk   = _bool()
    @input  val rstn  = _bool()
    @input  val addr  = _uint(ADDR_WIDTH)
    @input  val sel   = _bool()
    @input  val wr    = _bool()
    @input  val wdata = _uint(DATA_WIDTH)
    @output val rdata = _uint(DATA_WIDTH)
    @output val ready = _bool()

    val ctrl      = _array(DEPTH, _uint(DATA_WIDTH))
    val ready_dly = _bool()
    val ready_pe  = !ready && ready_dly

    @reg fun read_write() {
        on (posedge(clk)) {
            if (!rstn) ctrl for_each { it reg RESET_VAL }
            else {
                if (sel and ready) {
                    if (wr) ctrl[addr] reg wdata
                    else rdata reg ctrl[addr]
                } else {
                    rdata reg 0
                }
            }
        }
    }

    @reg fun reg_ready() {
        on (posedge(clk)) {
            if (!rstn) ready reg true
            else {
                if (sel and ready_pe) ready reg true
                if (sel and ready and !wr) ready reg false
            }
        }
    }

    @reg fun reg_ready_dly() {
        on (posedge(clk)) {
            if (!rstn) ready_dly reg true
            else ready_dly reg ready
        }
    }
}
