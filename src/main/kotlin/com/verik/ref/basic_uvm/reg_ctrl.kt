package com.verik.ref.basic_uvm

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _reg_ctrl(
    val ADDR_WIDTH: Int  = 8,
    val DATA_WIDTH: Int  = 16,
    val DEPTH: Int       = 256,
    val RESET_VAL: _bits = _bits.of("'h1234")
): _circuit {

    @input  val clk   = _bool()
    @input  val rstn  = _bool()
    @input  val addr  = _bits(ADDR_WIDTH)
    @input  val sel   = _bool()
    @input  val wr    = _bool()
    @input  val wdata = _bits(DATA_WIDTH)
    @output val rdata = _bits(DATA_WIDTH)
    @output val ready = _bool()

    val ctrl      = _array(DEPTH, _bits(DATA_WIDTH))
    val ready_dly = _bool()
    val ready_pe  = _bool() set (!ready and ready_dly)

    @seq fun read_write() {
        on (posedge(clk)) {
            if (!rstn) ctrl for_each { it put RESET_VAL }
            else {
                if (sel and ready) {
                    if (wr) ctrl[addr] put wdata
                    else rdata put ctrl[addr]
                } else {
                    rdata put 0
                }
            }
        }
    }

    @seq fun put_ready() {
        on (posedge(clk)) {
            if (!rstn) ready put true
            else {
                if (sel and ready_pe) ready put true
                if (sel and ready and !wr) ready put false
            }
        }
    }

    @seq fun put_ready_dly() {
        on (posedge(clk)) {
            if (!rstn) ready_dly put true
            else ready_dly put ready
        }
    }
}
