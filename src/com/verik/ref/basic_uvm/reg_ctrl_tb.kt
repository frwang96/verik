package com.verik.ref.basic_uvm

import com.verik.common.*
import com.verik.uvm.base.*
import com.verik.uvm.comps.*
import com.verik.uvm.seq._uvm_sequence
import com.verik.uvm.seq._uvm_sequence_item
import com.verik.uvm.seq._uvm_sequencer
import com.verik.uvm.tlm1._uvm_analysis_imp
import com.verik.uvm.tlm1._uvm_analysis_port

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

class _driver(val vif: _reg_if): _uvm_driver<_reg_item, Nothing>() {

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        forever {
            val item = _reg_item()
            uvm_info("DRV", "Wait for item from sequencer", _uvm_verbosity.LOW)
            seq_item_port.get_next_item(item)
            drive_item(item)
        }
    }

    @task fun drive_item(item: _reg_item) {
        vif.sel set true
        vif.addr set item.addr
        vif.wr set item.wr
        vif.wdata set item.wdata
        vk_wait_on(posedge(vif.clk))
        while (!vif.ready) {
            uvm_info("DRV", "Wait until ready is high", _uvm_verbosity.LOW)
            vk_wait_on(posedge(vif.clk))
        }
        vif.sel set false
    }
}

class _monitor(val vif: _reg_if): _uvm_monitor() {
    val mon_analysis_port = _uvm_analysis_port<_reg_item>()

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        forever {
            vk_wait_on(posedge(vif.clk))
            if (vif.sel) {
                val item = _reg_item()
                item.addr set vif.addr
                item.wr set vif.wr
                item.wdata set vif.wdata

                if (!vif.wr) {
                    vk_wait_on(posedge(vif.clk))
                    item.rdata set vif.rdata
                }
                uvm_info(get_type_name(), "Monitor found packet $item", _uvm_verbosity.LOW)
                mon_analysis_port.write(item)
            }
        }
    }
}

class _scoreboard: _uvm_scoreboard() {
    val analysis_imp = _uvm_analysis_imp(this::write)
    val refq = _array(DEPTH, _reg_item())

    fun write(item: _reg_item) {
        if (item.wr) {
            if (refq[item.addr].is_null()) {
                refq[item.addr] set item
                uvm_info(get_type_name(), "Store addr=${item.addr} wr=${item.wr} data=${item.wdata}", _uvm_verbosity.LOW)
            }
        } else {
            if (refq[item.addr].is_null()) {
                if (item.rdata != _bits.of("'h1234")) {
                    uvm_error(get_type_name(), "First time read, addr=${item.addr} exp=0x1234 act=${item.rdata}")
                } else {
                    uvm_info(get_type_name(), "PASS! First time read, addr=${item.addr} exp=0x1234 act=${item.rdata}", _uvm_verbosity.LOW)
                }
            } else {
                if (item.rdata != refq[item.addr].wdata) {
                    uvm_error(get_type_name(), "addr=${item.addr} exp=0x${refq[item.addr].wdata} act=${item.rdata}")
                } else {
                    uvm_info(get_type_name(), "PASS! addr=${item.addr} exp=0x${refq[item.addr].wdata} act=${item.rdata}", _uvm_verbosity.LOW)
                }
            }
        }
    }
}

class _agent(val vif: _reg_if): _uvm_agent() {
    val d0 = _driver(_reg_if())
    val m0 = _monitor(_reg_if())
    val s0 = _uvm_sequencer<_reg_item, Nothing>()

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        d0.seq_item_port.connect(s0.seq_item_export)
    }
}

class _env(val vif: _reg_if): _uvm_env() {
    val a0 = _agent(_reg_if())
    val sb0 = _scoreboard()

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        a0.m0.mon_analysis_port.connect(sb0.analysis_imp)
    }
}

@test class _test: _uvm_test() {
    val vif = _reg_if()
    val e0 = _env(vif)

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        val seq = _gen_item_seq()
        phase.raise_objection(this)
        apply_reset()
        seq.randomize()
        phase.drop_objection(this)
    }

    @task fun apply_reset() {
        vif.rstn set false
        repeat (5) { vk_wait_on(posedge(vif.clk)) }
        vif.rstn set true
        repeat (10) { vk_wait_on(posedge(vif.clk)) }
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

    @initial fun run() {
        run_test()
    }
}