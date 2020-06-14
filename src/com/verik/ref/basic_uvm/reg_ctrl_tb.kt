package com.verik.ref.basic_uvm

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

const val ADDR_WIDTH = 8
const val DATA_WIDTH = 16
const val DEPTH = 256

class _reg_if: _intf {
    @input val clk = _bool()

    val rstn  = _bool()
    val addr  = _bits(ADDR_WIDTH)
    val wdata = _bits(DATA_WIDTH)
    val rdata = _bits(DATA_WIDTH)
    val wr    = _bool()
    val sel   = _bool()
    val ready = _bool()
}

@main class _tb: _module {

    val clk = _bool()
    @initial fun clk() {
        clk set false
        forever {
            vk_delay(10)
            clk set !clk
        }
    }

    @def val reg_if = _reg_if() con {clk}

    @def val reg_ctrl = _reg_ctrl() con {
        clk
        reg_if.addr  con it.addr
        reg_if.rstn  con it.rstn
        reg_if.sel   con it.sel
        reg_if.wr    con it.wr
        reg_if.wdata con it.wdata
        reg_if.rdata con it.rdata
        reg_if.ready con it.ready
    }
}