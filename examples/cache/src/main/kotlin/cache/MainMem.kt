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

class MainMem(
    @In val clk: Boolean,
    val rxp: TxnBus.TxnRxPort
) : Module() {

    var mem = VArray<EXP<ADDR_WIDTH>, UbitData>()

    @Seq
    fun update() {
        on (posedge(clk)) {
            rxp.rspVld = false
            if (rxp.rst) {
                for (i in range(mem.size)) {
                    mem[i] = u(0)
                }
            } else {
                if (rxp.reqOp != Op.NOP) {
                    println("mem received op=${rxp.reqOp} addr=0x${rxp.reqAddr} data=0x${rxp.reqData}")
                    if (rxp.reqOp == Op.WRITE) {
                        mem[rxp.reqAddr] = rxp.reqData
                    } else {
                        rxp.rspData = mem[rxp.reqAddr]
                        rxp.rspVld = true
                    }
                }
            }
        }
    }
}