package com.verik.ref.basic_uvm

import com.verik.common.*
import com.verik.uvm.base._uvm_verbosity
import com.verik.uvm.base.uvm_info
import com.verik.uvm.seq._uvm_sequence
import com.verik.uvm.seq._uvm_sequence_item

// Copyright (c) 2020 Francis Wang

const val ADDR_WIDTH = 8
const val DATA_WIDTH = 16
const val DEPTH = 256

class _reg_item: _uvm_sequence_item() {
    @rand val addr  = _bits(ADDR_WIDTH)
    @rand val wdata = _bits(DATA_WIDTH)
    @rand val wr    = _bool()
    val rdata       = _bits(DATA_WIDTH)

    override fun toString(): String {
        return "addr=$addr wr=$wr wdata=$wdata rdata=$rdata"
    }
}

class _gen_item_seq: _uvm_sequence() {

    @rand val num = 0

    @task override fun body() {
        for (i in 0 until num) {
            val item = _reg_item()
            start_item(item)
            item.randomize()
            uvm_info("SEQ", "Generate new item: $item", _uvm_verbosity.LOW)
            finish_item(item)
        }
        uvm_info("SEQ", "Done generation of $num items", _uvm_verbosity.LOW)
    }
}

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

    @def val reg_if = _reg_if() con { clk }

    @def val reg_ctrl = _reg_ctrl() con {
        clk
        it.addr  con reg_if.addr
        it.rstn  con reg_if.rstn
        it.sel   con reg_if.sel
        it.wr    con reg_if.wr
        it.wdata con reg_if.wdata
        it.rdata con reg_if.rdata
        it.ready con reg_if.ready
    }
}