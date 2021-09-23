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

import io.verik.core.*

class CacheTb(
    @In var clk: Boolean,
    val tx: TxnIf.TxnTx
) : Module() {

    val mem: Unpacked<EXP<ADDR_WIDTH>, UbitData> = nc()

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
        for (i in 0 until mem.size) {
            mem[i] = zeroes()
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
        override val event: Event,
        @Out var rst: Boolean,
        @Out var reqOp: Op,
        @Out var reqAddr: UbitAddr,
        @Out var reqData: UbitData,
        @In var rspVld: Boolean,
        @In var rspData: UbitData
    ) : ClockingBlock()
}
