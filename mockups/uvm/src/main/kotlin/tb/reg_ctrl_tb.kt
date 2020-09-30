/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tb

import uvm.base.*
import uvm.comps.*
import uvm.seq._uvm_sequence
import uvm.seq._uvm_sequence_item
import uvm.seq.uvm_sequencer
import uvm.tlm1._uvm_analysis_imp
import uvm.tlm1.uvm_analysis_port
import verik.common.*
import verik.common.collections.*
import verik.common.data.*

val ADDR_WIDTH = 8
val DATA_WIDTH = 16
val DEPTH = 256

class _reg_item: _uvm_sequence_item() {

    var addr  = _uint(ADDR_WIDTH)
    var wdata = _uint(DATA_WIDTH)
    var wr    = _bool()
    var rdata = _uint(DATA_WIDTH)

    override fun toString(): String {
        return "addr=$addr wr=$wr wdata=$wdata rdata=$rdata"
    }
}

class _gen_item_seq: _uvm_sequence() {

    var num = _int()

    @task override fun body() {
        for (i in 0 until num) {
            val item = reg_item()
            start_item(item)
            uvm_info("SEQ", "Generate new item: $item", _uvm_verbosity.LOW)
            finish_item(item)
        }
        uvm_info("SEQ", "Done generation of $num items", _uvm_verbosity.LOW)
    }
}

class _driver: _uvm_driver<_reg_item>(_reg_item()) {

    val reg_bus = _reg_bus()

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        forever {
            uvm_info("DRV", "Wait for item from sequencer", _uvm_verbosity.LOW)
            val item = seq_item_port.get_next_item()
            drive_item(item)
        }
    }

    @task fun drive_item(item: _reg_item) {
        reg_bus.sel put true
        reg_bus.addr put item.addr
        reg_bus.wr put item.wr
        reg_bus.wdata put item.wdata
        wait(posedge(reg_bus.clk))
        while (!reg_bus.ready) {
            uvm_info("DRV", "Wait until ready is high", _uvm_verbosity.LOW)
            wait(posedge(reg_bus.clk))
        }
        reg_bus.sel put false
    }
}

fun driver(reg_bus: _reg_bus) = _driver() with {
    it.reg_bus put reg_bus
}

class _monitor: _uvm_monitor() {

    val reg_bus = _reg_bus()
    val mon_analysis_port = uvm_analysis_port(_reg_item())

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        forever {
            wait(posedge(reg_bus.clk))
            if (reg_bus.sel) {
                val item = reg_item()
                item.addr put reg_bus.addr
                item.wr put reg_bus.wr
                item.wdata put reg_bus.wdata

                if (!reg_bus.wr) {
                    wait(posedge(reg_bus.clk))
                    item.rdata put reg_bus.rdata
                }
                uvm_info(get_type_name(), "Monitor found packet $item", _uvm_verbosity.LOW)
                mon_analysis_port.write(item)
            }
        }
    }
}

fun monitor(reg_bus: _reg_bus) = _monitor() with {
    it.reg_bus put reg_bus
}

class _analysis_imp: _uvm_analysis_imp<_reg_item>(_reg_item()) {

    val scoreboard = _scoreboard()

    override fun read(req: _reg_item) {
        scoreboard.read(req)
    }
}

class _scoreboard: _uvm_scoreboard() {

    val analysis_imp = _analysis_imp()
    val refq = _array(_reg_item(), DEPTH)

    fun read(req: _reg_item) {
        if (req.wr) {
            if (refq[req.addr] eq NULL) {
                refq[req.addr] put req
                uvm_info(get_type_name(), "Store addr=${req.addr} wr=${req.wr} data=${req.wdata}", _uvm_verbosity.LOW)
            }
        } else {
            if (refq[req.addr] eq NULL) {
                if (req.rdata neq 0x1234) {
                    uvm_error(get_type_name(), "First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}")
                } else {
                    uvm_info(get_type_name(), "PASS! First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}", _uvm_verbosity.LOW)
                }
            } else {
                if (req.rdata neq refq[req.addr].wdata) {
                    uvm_error(get_type_name(), "addr=${req.addr} exp=0x${refq[req.addr].wdata} act=${req.rdata}")
                } else {
                    uvm_info(get_type_name(), "PASS! addr=${req.addr} exp=0x${refq[req.addr].wdata} act=${req.rdata}", _uvm_verbosity.LOW)
                }
            }
        }
    }
}

class _agent: _uvm_agent() {

    val reg_bus = _reg_bus()
    val d0 = _driver()
    val m0 = _monitor()

    val s0 = uvm_sequencer(_reg_item())

    override fun build_phase(phase: _uvm_phase) {
        super.build_phase(phase)
        d0 put driver(reg_bus)
        m0 put monitor(reg_bus)
    }

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        d0.seq_item_port.connect(s0.seq_item_export)
    }
}

fun agent(reg_bus: _reg_bus) = _agent() with {
    it.reg_bus put reg_bus
}

class _env: _uvm_env() {

    val reg_bus = _reg_bus()
    val a0 = _agent()
    val sb0 = _scoreboard()

    override fun build_phase(phase: _uvm_phase) {
        super.build_phase(phase)
        a0 put agent(reg_bus)
        sb0 put scoreboard()
    }

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        a0.m0.mon_analysis_port.connect(sb0.analysis_imp)
    }
}

fun env(reg_bus: _reg_bus) = _env() with {
    it.reg_bus put reg_bus
}

class _test: _uvm_test() {

    val reg_bus = _reg_bus()
    val e0 = _env()

    override fun build_phase(phase: _uvm_phase) {
        super.build_phase(phase)
        e0 put env(reg_bus)
    }

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        val seq = gen_item_seq()
        phase.raise_objection(this)
        apply_reset()
        seq.start(e0.a0.s0)
        delay(200)
        phase.drop_objection(this)
    }

    @task fun apply_reset() {
        reg_bus.rstn put false
        repeat(5) { wait(posedge(reg_bus.clk)) }
        reg_bus.rstn put true
        repeat(10) { wait(posedge(reg_bus.clk)) }
    }
}

fun test(reg_bus: _reg_bus) = _test() with {
    it.reg_bus put reg_bus
}

class _reg_bus: _bus {

    @input var clk = _bool()

    var rstn  = _bool()
    var addr  = _uint(ADDR_WIDTH)
    var wdata = _uint(DATA_WIDTH)
    var rdata = _uint(DATA_WIDTH)
    var wr    = _bool()
    var sel   = _bool()
    var ready = _bool()
}

@top class _tb: _module {

    var clk = _bool()

    @run fun clk() {
        clk put false
        forever {
            delay(10)
            clk put !clk
        }
    }

    @make val reg_bus = _reg_bus() with {
        it.clk con clk
    }

    @make val reg_ctrl = _reg_ctrl(ADDR_WIDTH, DATA_WIDTH, uint(16, 0x1234)) with {
        it.clk   con clk
        it.addr  con reg_bus.addr
        it.rstn  con reg_bus.rstn
        it.sel   con reg_bus.sel
        it.wr    con reg_bus.wr
        it.wdata con reg_bus.wdata
        it.rdata con reg_bus.rdata
        it.ready con reg_bus.ready
    }

    var t0 = _test()
    @run fun run() {
        t0 put test(reg_bus)
        run_test()
    }
}