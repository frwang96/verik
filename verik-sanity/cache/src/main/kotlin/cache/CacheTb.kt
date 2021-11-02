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
    val ifTb: TxnIf.TxnTb
) : Module() {

    val mem: Unpacked<EXP<ADDR_WIDTH>, UbitData> = nc()
    var pass = true

    @Run
    fun runTest() {
        reset()
        repeat(200) { transact() }
        if (pass)
            finish()
        else
            fatal()
    }

    @Task
    fun reset() {
        for (i in 0 until mem.size) {
            mem[i] = u0()
        }
        wait(ifTb.cb)
        ifTb.cb.rst = true
        ifTb.cb.reqOp = Op.NOP
        wait(ifTb.cb)
        ifTb.cb.rst = false
    }

    @Task
    fun transact() {
        repeat(3) { wait(ifTb.cb) }
        if (random(1) == 0) {
            // write mem
            val addr: UbitAddr = randomUbit()
            val data: UbitData = randomUbit()
            mem[addr] = data
            println("tb write addr=0x$addr data=0x$data")

            wait(ifTb.cb)
            ifTb.cb.reqOp = Op.WRITE
            ifTb.cb.reqAddr = addr
            ifTb.cb.reqData = data
            wait(ifTb.cb)
            ifTb.cb.reqOp = Op.NOP
        } else {
            // read mem
            val addr: UbitAddr = randomUbit()
            println("tb read addr=0x$addr")

            wait(ifTb.cb)
            ifTb.cb.reqOp = Op.READ
            ifTb.cb.reqAddr = addr
            wait(ifTb.cb)
            ifTb.cb.reqOp = Op.NOP

            while (!ifTb.cb.rspVld) wait(ifTb.cb)
            val data = ifTb.cb.rspData
            val expected = mem[addr]

            if (data == expected) {
                println("tb PASS data=0x$data expected=0x$expected")
            } else {
                println("tb FAIL data=0x$data expected=0x$expected")
                pass = false
            }
        }
    }
}
