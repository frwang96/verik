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

import verik.base.*
import verik.collection.*
import verik.data.*

class CacheTb: Module() {

    @inout val bp = t_MemTbBusPort()

    private val mem = t_Array(exp(ADDR_WIDTH), t_Ubit(DATA_WIDTH))

    @run fun run_test() {
        reset()
        repeat(200) { transact() }
        finish()
    }

    @task fun reset() {
        for (i in range(exp(ADDR_WIDTH))) {
            mem[i] = u(0)
        }
        wait(bp.cp)
        bp.cp.rst = true
        wait(bp.cp)
        bp.cp.rst = false
    }

    @task fun transact() {
        if (random(2) == 0) {
            // write mem
            val addr = u(ADDR_WIDTH, random())
            val data = u(DATA_WIDTH, random())
            mem[addr] = data

            wait(bp.cp)
            bp.cp.in_vld = true
            bp.cp.addr = addr
            bp.cp.write = true
            bp.cp.in_data = data
            wait(bp.cp)
            bp.cp.in_vld = false
        } else {
            // read mem
            val addr = u(ADDR_WIDTH, random())

            wait(bp.cp)
            bp.cp.in_vld = true
            bp.cp.addr = addr
            bp.cp.write = false
            wait(bp.cp)
            bp.cp.in_vld = false

            while (!bp.cp.out_vld) wait(bp.cp)
            val data = bp.cp.out_data
            val expected = mem[addr]

            if (data == expected) {
                println("PASS data=0x$data expected=0x$expected")
            } else {
                println("FAIL data=0x$data expected=0x$expected")
            }
        }
    }
}