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

import uvm.base._uvm_phase
import uvm.base._uvm_verbosity
import uvm.base.uvm_error
import uvm.base.uvm_info
import uvm.comps.*
import uvm.seq._uvm_sequence
import uvm.seq._uvm_sequence_item
import uvm.seq.uvm_sequencer
import uvm.tlm1._uvm_analysis_imp
import uvm.tlm1.uvm_analysis_port
import verik.common.*
import verik.common.collections.*
import verik.common.data.*

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

    val num = _int()

    @task override fun body() {
        repeat (num) {
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
        reg_bus.sel = true
        reg_bus.addr = item.addr
        reg_bus.wr = item.wr
        reg_bus.wdata = item.wdata
        wait(posedge(reg_bus.clk))
        while (!reg_bus.ready) {
            uvm_info("DRV", "Wait until ready is high", _uvm_verbosity.LOW)
            wait(posedge(reg_bus.clk))
        }
        reg_bus.sel = false
    }
}

fun driver(reg_bus: _reg_bus) = _driver() with {
    it.reg_bus init reg_bus
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
                item.addr = reg_bus.addr
                item.wr = reg_bus.wr
                item.wdata = reg_bus.wdata

                if (!reg_bus.wr) {
                    wait(posedge(reg_bus.clk))
                    item.rdata = reg_bus.rdata
                }
                uvm_info(get_type_name(), "Monitor found packet $item", _uvm_verbosity.LOW)
                mon_analysis_port.write(item)
            }
        }
    }
}

fun monitor(reg_bus: _reg_bus) = _monitor() with {
    it.reg_bus init reg_bus
}

class _analysis_imp: _uvm_analysis_imp<_reg_item>(_reg_item()) {

    val scoreboard = _scoreboard()

    override fun read(req: _reg_item) {
        scoreboard.read(req)
    }
}

fun analysis_imp(scoreboard: _scoreboard) = _analysis_imp() with {
    it.scoreboard init scoreboard
}

class _scoreboard: _uvm_scoreboard() {

    val analysis_imp = analysis_imp(this)
    val refq = _array(_reg_item(), DEPTH)

    fun read(req: _reg_item) {
        if (req.wr) {
            if (refq[req.addr] == NULL(_reg_item())) {
                refq[req.addr] = req
                uvm_info(get_type_name(), "Store addr=${req.addr} wr=${req.wr} data=${req.wdata}", _uvm_verbosity.LOW)
            }
        } else {
            if (refq[req.addr] == NULL(reg_item())) {
                if (req.rdata != uint(DATA_WIDTH, 0x1234)) {
                    uvm_error(get_type_name(), "First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}")
                } else {
                    uvm_info(get_type_name(), "PASS! First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}", _uvm_verbosity.LOW)
                }
            } else {
                if (req.rdata != refq[req.addr].wdata) {
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
    val d0 = driver(reg_bus)
    val m0 = monitor(reg_bus)
    val s0 = uvm_sequencer(_reg_item())

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        d0.seq_item_port.connect(s0.seq_item_export)
    }
}

fun agent(reg_bus: _reg_bus) = _agent() with {
    it.reg_bus init reg_bus
}

class _env: _uvm_env() {

    val reg_bus = _reg_bus()
    val a0 = agent(reg_bus)
    val sb0 = scoreboard()

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        a0.m0.mon_analysis_port.connect(sb0.analysis_imp)
    }
}

fun env(reg_bus: _reg_bus) = _env() with {
    it.reg_bus init reg_bus
}

class _test: _uvm_test() {

    val reg_bus = _reg_bus()
    val e0 = env(reg_bus)

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
        reg_bus.rstn = false
        repeat(5) { wait(posedge(reg_bus.clk)) }
        reg_bus.rstn = true
        repeat(10) { wait(posedge(reg_bus.clk)) }
    }
}

fun test(reg_bus: _reg_bus) = _test() with {
    it.reg_bus init reg_bus
}
