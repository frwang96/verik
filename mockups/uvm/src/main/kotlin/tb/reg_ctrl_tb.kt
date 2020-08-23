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

open class _reg_item: _uvm_sequence_item() {

    val addr  = _uint(ADDR_WIDTH)
    val wdata = _uint(DATA_WIDTH)
    val wr    = _bool()
    val rdata       = _uint(DATA_WIDTH)

    override fun toString(): String {
        return "addr=$addr wr=$wr wdata=$wdata rdata=$rdata"
    }
}

open class _gen_item_seq: _uvm_sequence() {

    val num = _int()

    @task override fun body() {
        for (i in 0 until num) {
            val item = reg_item()
            start_item(item)
            item.randomize()
            uvm_info("SEQ", "Generate new item: $item", _uvm_verbosity.LOW)
            finish_item(item)
        }
        uvm_info("SEQ", "Done generation of $num items", _uvm_verbosity.LOW)
    }
}

open class _driver: _uvm_driver<_reg_item>(_reg_item()) {

    val reg_if = _reg_if()

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        forever {
            uvm_info("DRV", "Wait for item from sequencer", _uvm_verbosity.LOW)
            val item = seq_item_port.get_next_item()
            drive_item(item)
        }
    }

    @task fun drive_item(item: _reg_item) {
        reg_if.sel put true
        reg_if.addr put item.addr
        reg_if.wr put item.wr
        reg_if.wdata put item.wdata
        wait(posedge(reg_if.clk))
        while (!reg_if.ready) {
            uvm_info("DRV", "Wait until ready is high", _uvm_verbosity.LOW)
            wait(posedge(reg_if.clk))
        }
        reg_if.sel put false
    }
}

class driver(reg_if: _reg_if): _driver() {

    init {
        this.reg_if put reg_if
    }
}

open class _monitor: _uvm_monitor() {

    val reg_if = _reg_if()
    val mon_analysis_port = uvm_analysis_port(_reg_item())

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        forever {
            wait(posedge(reg_if.clk))
            if (reg_if.sel) {
                val item = reg_item()
                item.addr put reg_if.addr
                item.wr put reg_if.wr
                item.wdata put reg_if.wdata

                if (!reg_if.wr) {
                    wait(posedge(reg_if.clk))
                    item.rdata put reg_if.rdata
                }
                uvm_info(get_type_name(), "Monitor found packet $item", _uvm_verbosity.LOW)
                mon_analysis_port.write(item)
            }
        }
    }
}

class monitor(reg_if: _reg_if): _monitor() {

    init {
        this.reg_if put reg_if
    }
}

open class _analysis_imp: _uvm_analysis_imp<_reg_item>(_reg_item()) {

    val scoreboard = _scoreboard()

    override fun read(req: _reg_item) {
        scoreboard.read(req)
    }
}

open class _scoreboard: _uvm_scoreboard() {

    val analysis_imp = _analysis_imp()
    val refq = _array(_reg_item(), DEPTH)

    fun read(req: _reg_item) {
        if (req.wr) {
            if (refq[req.addr].is_null()) {
                refq[req.addr] put req
                uvm_info(get_type_name(), "Store addr=${req.addr} wr=${req.wr} data=${req.wdata}", _uvm_verbosity.LOW)
            }
        } else {
            if (refq[req.addr].is_null()) {
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

open class _agent: _uvm_agent() {

    val reg_if = _reg_if()
    val d0 = _driver()
    val m0 = _monitor()

    val s0 = uvm_sequencer(_reg_item())

    override fun build_phase(phase: _uvm_phase) {
        super.build_phase(phase)
        d0 put driver(reg_if)
        m0 put monitor(reg_if)
    }

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        d0.seq_item_port.connect(s0.seq_item_export)
    }
}

class agent(reg_if: _reg_if): _agent() {

    init {
        this.reg_if put reg_if
    }
}

open class _env: _uvm_env() {

    val reg_if = _reg_if()
    val a0 = _agent()
    val sb0 = _scoreboard()

    override fun build_phase(phase: _uvm_phase) {
        super.build_phase(phase)
        a0 put agent(reg_if)
        sb0 put scoreboard()
    }

    override fun connect_phase(phase: _uvm_phase) {
        super.connect_phase(phase)
        a0.m0.mon_analysis_port.connect(sb0.analysis_imp)
    }
}

class env(reg_if: _reg_if): _env() {

    init {
        this.reg_if put reg_if
    }
}

open class _test: _uvm_test() {

    val reg_if = _reg_if()
    val e0 = _env()

    override fun build_phase(phase: _uvm_phase) {
        super.build_phase(phase)
        e0 put env(reg_if)
    }

    @task override fun run_phase(phase: _uvm_phase) {
        super.run_phase(phase)
        val seq = gen_item_seq()
        phase.raise_objection(this)
        apply_reset()
        seq randomize { it.num in 20..30 }
        seq.start(e0.a0.s0)
        delay(200)
        phase.drop_objection(this)
    }

    @task fun apply_reset() {
        reg_if.rstn put false
        repeat(5) { wait(posedge(reg_if.clk)) }
        reg_if.rstn put true
        repeat(10) { wait(posedge(reg_if.clk)) }
    }
}

class test(reg_if: _reg_if): _test() {

    init {
        this.reg_if put reg_if
    }
}

open class _reg_if: _interf {

    @input val clk = _bool()

    val rstn  = _bool()
    val addr  = _uint(ADDR_WIDTH)
    val wdata = _uint(DATA_WIDTH)
    val rdata = _uint(DATA_WIDTH)
    val wr    = _bool()
    val sel   = _bool()
    val ready = _bool()
}

@top class _tb: _module {

    val clk = _bool()

    @run fun clk() {
        clk put false
        forever {
            delay(10)
            clk put !clk
        }
    }

    @comp val reg_if = _reg_if() with { clk }

    @comp val reg_ctrl = _reg_ctrl(ADDR_WIDTH, DATA_WIDTH, uint(0x1234)) with {
        clk
        it.addr  con reg_if.addr
        it.rstn  con reg_if.rstn
        it.sel   con reg_if.sel
        it.wr    con reg_if.wr
        it.wdata con reg_if.wdata
        it.rdata con reg_if.rdata
        it.ready con reg_if.ready
    }

    val t0 = _test()
    @run fun run() {
        t0 put test(reg_if)
        run_test()
    }
}