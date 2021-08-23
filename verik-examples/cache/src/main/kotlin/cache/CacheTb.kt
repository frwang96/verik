/*
 * Copyright (c) 2021 Francis Wang
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

package cache

import io.verik.core.ClockingBlock
import io.verik.core.EXP
import io.verik.core.Event
import io.verik.core.In
import io.verik.core.Make
import io.verik.core.Module
import io.verik.core.Out
import io.verik.core.Run
import io.verik.core.Task
import io.verik.core.VArray
import io.verik.core.finish
import io.verik.core.posedge
import io.verik.core.random
import io.verik.core.range
import io.verik.core.u
import io.verik.core.wait

class CacheTb(
    @In var clk: Boolean,
    val tx: TxnIf.TxnTx
) : Module() {

    val mem = VArray<EXP<ADDR_WIDTH>, UbitData>()

    @Make val cb = CacheTbClockingBlock(
        event = posedge(clk),
        rst = tx.rst,
        reqOp = tx.reqOp,
        reqAddr = tx.reqAddr,
        reqData = tx.reqData,
        rspVld = tx.rspVld,
        rspData = tx.rspData
    )

    @Run
    fun runTest() {
        reset()
        repeat(1000) { transact() }
        finish()
    }

    @Task
    fun reset() {
        for (i in range(mem.size)) {
            mem[i] = u(0)
        }
        wait(cb)
        cb.rst = true
        cb.reqOp = Op.NOP
        wait(cb)
        cb.rst = false
    }

    @Task
    fun transact() {
        repeat(3) { wait(cb) }
        if (random(2) == 0) {
            // write mem
            val addr: UbitAddr = u(random())
            val data: UbitData = u(random())
            mem[addr] = data
            println("tb write addr=0x$addr data=0x$data")

            wait(cb)
            cb.reqOp = Op.WRITE
            cb.reqAddr = addr
            cb.reqData = data
            wait(cb)
            cb.reqOp = Op.NOP
        } else {
            // read mem
            val addr: UbitAddr = u(random())
            println("tb read addr=0x$addr")

            wait(cb)
            cb.reqOp = Op.READ
            cb.reqAddr = addr
            wait(cb)
            cb.reqOp = Op.NOP

            while (!cb.rspVld) wait(cb)
            val data = cb.rspData
            val expected = mem[addr]

            if (data == expected) {
                println("tb PASS data=0x$data expected=0x$expected")
            } else {
                println("tb FAIL data=0x$data expected=0x$expected")
            }
        }
    }

    class CacheTbClockingBlock(
        event: Event,
        @Out var rst: Boolean,
        @Out var reqOp: Op,
        @Out var reqAddr: UbitAddr,
        @Out var reqData: UbitData,
        @In var rspVld: Boolean,
        @In var rspData: UbitData
    ) : ClockingBlock(event)
}
