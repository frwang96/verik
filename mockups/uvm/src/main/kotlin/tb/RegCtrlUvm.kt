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

import uvm.base.UvmPhase
import uvm.base.UvmVerbosity
import uvm.base.uvmError
import uvm.base.uvmInfo
import uvm.comps.*
import uvm.seq.UvmSequence
import uvm.seq.UvmSequenceItem
import uvm.seq.UvmSequencer
import uvm.tlm1.UvmAnalysisImp
import uvm.tlm1.UvmAnalysisPort
import verik.base.*
import verik.collection.Array
import verik.data.*

class RegItem: UvmSequenceItem() {

    var addr: Ubit<ADDR_WIDTH> = d()
    var wdata: Ubit<DATA_WIDTH> = d()
    var wr: Boolean = d()
    var rdata: Ubit<DATA_WIDTH> = d()

    override fun toString(): String {
        return "addr=$addr wr=$wr wdata=$wdata rdata=$rdata"
    }
}

class GenItemSeq(val num: Int): UvmSequence() {

    @task override fun body() {
        repeat (num) {
            val item = RegItem()
            startItem(item)
            uvmInfo("SEQ", "Generate new item: $item", UvmVerbosity.LOW)
            finishItem(item)
        }
        uvmInfo("SEQ", "Done generation of $num items", UvmVerbosity.LOW)
    }
}

class Driver(val regBus: RegBus): UvmDriver<RegItem>() {

    @task override fun runPhase(phase: UvmPhase) {
        super.runPhase(phase)
        forever {
            uvmInfo("DRV", "Wait for item from sequencer", UvmVerbosity.LOW)
            val item = seqItemPort.getNextItem()
            driveItem(item)
        }
    }

    @task fun driveItem(item: RegItem) {
        regBus.sel = true
        regBus.addr = item.addr
        regBus.wr = item.wr
        regBus.wdata = item.wdata
        wait(posedge(regBus.clk))
        while (!regBus.ready) {
            uvmInfo("DRV", "Wait until ready is high", UvmVerbosity.LOW)
            wait(posedge(regBus.clk))
        }
        regBus.sel = false
    }
}

class Monitor(val regBus: RegBus): UvmMonitor() {

    var monAnalysisPort = UvmAnalysisPort<RegItem>()

    @task override fun runPhase(phase: UvmPhase) {
        super.runPhase(phase)
        forever {
            wait(posedge(regBus.clk))
            if (regBus.sel) {
                val item = RegItem()
                item.addr = regBus.addr
                item.wr = regBus.wr
                item.wdata = regBus.wdata

                if (!regBus.wr) {
                    wait(posedge(regBus.clk))
                    item.rdata = regBus.rdata
                }
                uvmInfo(getTypeName(), "Monitor found packet $item", UvmVerbosity.LOW)
                monAnalysisPort.write(item)
            }
        }
    }
}

class AnalysisImp(val scoreboard: Scoreboard): UvmAnalysisImp<RegItem>() {

    override fun read(req: RegItem) {
        scoreboard.read(req)
    }
}

class Scoreboard: UvmScoreboard() {

    var analysisImp = AnalysisImp(this)
    val refq = Array<DEPTH, RegItem>()

    fun read(req: RegItem) {
        if (req.wr) {
            if (refq[req.addr] == n<RegItem>()) {
                refq[req.addr] = req
                uvmInfo(getTypeName(), "Store addr=${req.addr} wr=${req.wr} data=${req.wdata}", UvmVerbosity.LOW)
            }
        } else {
            if (refq[req.addr] == n<RegItem>()) {
                if (req.rdata != u<DATA_WIDTH>(0x1234)) {
                    uvmError(getTypeName(), "First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}")
                } else {
                    uvmInfo(
                        getTypeName(),
                        "PASS! First time read, addr=${req.addr} exp=0x1234 act=${req.rdata}",
                        UvmVerbosity.LOW
                    )
                }
            } else {
                if (req.rdata != refq[req.addr].wdata) {
                    uvmError(getTypeName(), "addr=${req.addr} exp=0x${refq[req.addr].wdata} act=${req.rdata}")
                } else {
                    uvmInfo(
                        getTypeName(),
                        "PASS! addr=${req.addr} exp=0x${refq[req.addr].wdata} act=${req.rdata}",
                        UvmVerbosity.LOW
                    )
                }
            }
        }
    }
}

class Agent(val regBus: RegBus): UvmAgent() {

    var d0 = Driver(regBus)
    var m0 = Monitor(regBus)
    var s0 = UvmSequencer<RegItem>()

    override fun connectPhase(phase: UvmPhase) {
        super.connectPhase(phase)
        d0.seqItemPort.connect(s0.seqItemExport)
    }
}

class Env(val regBus: RegBus): UvmEnv() {

    var sb0 = Scoreboard()
    var a0 = Agent(regBus)

    override fun connectPhase(phase: UvmPhase) {
        super.connectPhase(phase)
        a0.m0.monAnalysisPort.connect(sb0.analysisImp)
    }
}

class Test(val regBus: RegBus): UvmTest() {

    var e0 = Env(regBus)

    @task override fun runPhase(phase: UvmPhase) {
        super.runPhase(phase)
        phase.raiseObjection(this)
        applyReset()
        GenItemSeq(100).start(e0.a0.s0)
        delay(200)
        phase.dropObjection(this)
    }

    @task fun applyReset() {
        regBus.rstN = false
        repeat(5) { wait(posedge(regBus.clk)) }
        regBus.rstN = true
        repeat(10) { wait(posedge(regBus.clk)) }
    }
}
