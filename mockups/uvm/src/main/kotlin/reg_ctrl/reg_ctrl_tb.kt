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

package reg_ctrl

import io.verik.common.*
import io.verik.common.types.*
import uvm.base.*
import uvm.comps.*
import uvm.seq._uvm_sequence
import uvm.seq._uvm_sequence_item
import uvm.seq.uvm_sequencer
import uvm.tlm1.uvm_analysis_imp
import uvm.tlm1.uvm_analysis_port

val ADDR_WIDTH = 8
val DATA_WIDTH = 16
val DEPTH = 256

class _reg_item: _uvm_sequence_item() {
    @rand val addr  = _uint(ADDR_WIDTH)
    @rand val wdata = _uint(DATA_WIDTH)
    @rand val wr    = _bool()
    val rdata       = _uint(DATA_WIDTH)

    override fun toString(): String {
        return "addr=$addr wr=$wr wdata=$wdata rdata=$rdata"
    }
}

class _gen_item_seq: _uvm_sequence() {
    @rand val num = _uint32()

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

class _driver: _uvm_driver<_reg_item>(_reg_item()) {
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

fun driver(reg_if: _reg_if) = driver() with {
    it.reg_if put reg_if
}

class _monitor: _uvm_monitor() {
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

fun monitor(reg_if: _reg_if) = monitor() with {
    it.reg_if put reg_if
}

class _scoreboard: _uvm_scoreboard() {
    val refq = _array(DEPTH, _reg_item())

    val analysis_imp = uvm_analysis_imp(_reg_item()) {
        if (it.wr) {
            if (refq[it.addr].is_null()) {
                refq[it.addr] put it
                uvm_info(get_type_name(), "Store addr=${it.addr} wr=${it.wr} data=${it.wdata}", _uvm_verbosity.LOW)
            }
        } else {
            if (refq[it.addr].is_null()) {
                if (it.rdata neq 0x1234) {
                    uvm_error(get_type_name(), "First time read, addr=${it.addr} exp=0x1234 act=${it.rdata}")
                } else {
                    uvm_info(get_type_name(), "PASS! First time read, addr=${it.addr} exp=0x1234 act=${it.rdata}", _uvm_verbosity.LOW)
                }
            } else {
                if (it.rdata != refq[it.addr].wdata) {
                    uvm_error(get_type_name(), "addr=${it.addr} exp=0x${refq[it.addr].wdata} act=${it.rdata}")
                } else {
                    uvm_info(get_type_name(), "PASS! addr=${it.addr} exp=0x${refq[it.addr].wdata} act=${it.rdata}", _uvm_verbosity.LOW)
                }
            }
        }
    }
}

class _agent: _uvm_agent() {
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

fun agent(reg_if: _reg_if) = agent() with {
    it.reg_if put reg_if
}

class _env: _uvm_env() {
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

fun env(reg_if: _reg_if) = env() with {
    it.reg_if put reg_if
}

class _test: _uvm_test() {
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
        seq.randomize { num in 20..30 }
        seq.start(e0.a0.s0)
        wait(200)
        phase.drop_objection(this)
    }

    @task fun apply_reset() {
        reg_if.rstn put false
        wait(posedge(reg_if.clk), 5)
        reg_if.rstn put true
        wait(posedge(reg_if.clk), 10)
    }
}

fun test(reg_if: _reg_if) = test() with {
    it.reg_if put reg_if
}

class _reg_if: _interf {
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
    @initial fun clk() {
        clk put false
        forever {
            wait(10)
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
    @initial fun run() {
        t0 put test(reg_if)
        run_test()
    }
}