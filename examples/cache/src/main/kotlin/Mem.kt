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

class Mem: Module() {

    @input val clk = t_Boolean()
    @inout val bp  = t_MemRxBusPort()

    private var mem = t_Array(exp(ADDR_WIDTH), t_Ubit(DATA_WIDTH))

    @seq fun update() {
        on (posedge(clk)) {
            bp.rsp_vld = false
            if (bp.rst) {
                for (i in range(exp(ADDR_WIDTH))) {
                    mem[i] = u(0)
                }
            } else {
                if (bp.req_op != Op.INVALID) {
                    println("mem received op=${bp.req_op} addr=0x${bp.req_addr} data=0x${bp.req_data}")
                    if (bp.req_op == Op.WRITE) {
                        mem[bp.req_addr] = bp.req_data
                    } else {
                        bp.rsp_data = mem[bp.req_addr]
                        bp.rsp_vld = true
                    }
                }
            }
        }
    }
}