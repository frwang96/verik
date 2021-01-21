/*
 * Copyright (c) 2020 Francis Wang
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
import uvm.seq.UvmSequence
import uvm.seq.UvmSequenceItem
import uvm.seq.i_UvmSequencer
import uvm.tlm1.UvmAnalysisImp
import uvm.tlm1.i_UvmAnalysisPort
import verik.base.*
import verik.collection.*
import verik.data.*

class RegItem: UvmSequenceItem() {

    var addr  = t_Ubit(ADDR_WIDTH)
    var wdata = t_Ubit(DATA_WIDTH)
    var wr    = t_Boolean()
    var rdata = t_Ubit(DATA_WIDTH)

    override fun to_string(): String {
        return "addr=$addr wr=$wr wdata=$wdata rdata=$rdata"
    }
}

class GenItemSeq: UvmSequence() {

    private val num = t_Int()

    @task override fun body() {
        repeat (num) {
            val item = i_RegItem()
            start_item(item)
            uvm_info("SEQ", "Generate new item: $item", UvmVerbosity.LOW)
            finish_item(item)
        }
        uvm_info("SEQ", "Done generation of $num items", UvmVerbosity.LOW)
    }
}

class Driver: UvmDriver<RegItem>(t_RegItem()) {

    private val reg_bus = t_RegBus()

    fun init(reg_bus: RegBus) {
        this.reg_bus init reg_bus
    }

    @task override fun run_phase(phase: UvmPhase) {
        super.run_phase(phase)
        forever {
            uvm_info("DRV", "Wait for item from sequencer", UvmVerbosity.LOW)
            val item = seq_item_port.get_next_item()
            drive_item(item)
        }
    }

    @task fun drive_item(item: RegItem) {
        reg_bus.sel = true
        reg_bus.addr = item.addr
        reg_bus.wr = item.wr
        reg_bus.wdata = item.wdata
        wait(posedge(reg_bus.clk))
        while (!reg_bus.ready) {
            uvm_info("DRV", "Wait until ready is high", UvmVerbosity.LOW)
            wait(posedge(reg_bus.clk))
        }
        reg_bus.sel = false
    }
}

class Monitor: UvmMonitor() {

    private val reg_bus = t_RegBus()

    val mon_analysis_port = i_UvmAnalysisPort(RegItem())

    fun init(reg_bus: RegBus) {
        this.reg_bus init reg_bus
    }

    @task override fun run_phase(phase: UvmPhase) {
        super.run_phase(phase)
        forever {
            wait(posedge(reg_bus.clk))
            if (reg_bus.sel) {
                val item = i_RegItem()
                item.addr = reg_bus.addr
                item.wr = reg_bus.wr
                item.wdata = reg_bus.wdata

                if (!reg_bus.wr) {
                    wait(posedge(reg_bus.clk))
                    item.rdata = reg_bus.rdata
                }
                uvm_info(get_type_name(), "Monitor found packet $item", UvmVerbosity.LOW)
                mon_analysis_port.write(item)
            }
        }
    }
}

class AnalysisImp: UvmAnalysisImp<RegItem>(RegItem()) {

    private val scoreboard = t_Scoreboard()

    fun init(scoreboard: Scoreboard) {
        this.scoreboard init scoreboard
    }

    override fun read(req: RegItem) {
        scoreboard.read(req)
    }
}

class Scoreboard: UvmScoreboard() {

    val analysis_imp = i_AnalysisImp(this)

    private val refq = t_Array(DEPTH, RegItem())

    fun read(req: RegItem) {
        if (req.wr) {
            if (refq[req.addr].is_null()) {
                refq[req.addr] = req
                uvm_info(get_type_name(), "Store addr=${req.addr} wr=${req.wr} data=${req.wdata}", UvmVerbosity.LOW)
            }
        } else {
            if (refq[req.addr].is_null()) {
                if (req.rdata != u(0x1234)) {
                    uvm_error(get_type_name(), "First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}")
                } else {
                    uvm_info(
                        get_type_name(),
                        "PASS! First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}",
                        UvmVerbosity.LOW
                    )
                }
            } else {
                if (req.rdata != refq[req.addr].wdata) {
                    uvm_error(get_type_name(), "addr=${req.addr} exp=0x${refq[req.addr].wdata} act=${req.rdata}")
                } else {
                    uvm_info(
                        get_type_name(),
                        "PASS! addr=${req.addr} exp=0x${refq[req.addr].wdata} act=${req.rdata}",
                        UvmVerbosity.LOW
                    )
                }
            }
        }
    }
}

class Agent: UvmAgent() {

    private val reg_bus = t_RegBus()
    private val d0 = i_Driver(reg_bus)

    val m0 = i_Monitor(reg_bus)
    val s0 = i_UvmSequencer(RegItem())

    fun init(reg_bus: RegBus) {
        this.reg_bus init reg_bus
    }

    override fun connect_phase(phase: UvmPhase) {
        super.connect_phase(phase)
        d0.seq_item_port.connect(s0.seq_item_export)
    }
}

class Env: UvmEnv() {

    private val reg_bus = t_RegBus()
    private val sb0 = i_Scoreboard()

    val a0 = i_Agent(reg_bus)

    fun init(reg_bus: RegBus) {
        this.reg_bus init reg_bus
    }

    override fun connect_phase(phase: UvmPhase) {
        super.connect_phase(phase)
        a0.m0.mon_analysis_port.connect(sb0.analysis_imp)
    }
}

class Test: UvmTest() {

    private val reg_bus = t_RegBus()
    private val e0 = i_Env(reg_bus)

    fun init(reg_bus: RegBus) {
        this.reg_bus init reg_bus
    }

    @task override fun run_phase(phase: UvmPhase) {
        super.run_phase(phase)
        val seq = i_GenItemSeq()
        phase.raise_objection(this)
        apply_reset()
        seq.start(e0.a0.s0)
        delay(200)
        phase.drop_objection(this)
    }

    @task fun apply_reset() {
        reg_bus.rst_n = false
        repeat(5) { wait(posedge(reg_bus.clk)) }
        reg_bus.rst_n = true
        repeat(10) { wait(posedge(reg_bus.clk)) }
    }
}
