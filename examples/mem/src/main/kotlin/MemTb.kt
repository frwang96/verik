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
import verik.data.*

class MemTb: Module() {

    @inout val bp = t_MemTestBusPort()

    @run fun run_test() {
        reset()
        write_mem()
        read_mem()
        finish()
    }

    @task fun reset() {
        wait(bp.cp)
        bp.cp.rst = true
        wait(bp.cp)
        bp.cp.rst = false
    }

    @task fun write_mem() {
        wait(bp.cp)
        bp.cp.write_en = true
        for (i in range(exp(ADDR_WIDTH))) {
            bp.cp.addr = u(i)
            bp.cp.data_in = u(i)
            wait(bp.cp)
        }
        bp.cp.write_en = false
    }

    @task fun read_mem() {
        wait(bp.cp)
        for (i in range(exp(ADDR_WIDTH))) {
            bp.cp.addr = u(i)
            repeat(2) { wait(bp.cp) }
            println("addr=0x${u(ADDR_WIDTH, i)} data=0x${bp.cp.data_out}")
        }
    }
}