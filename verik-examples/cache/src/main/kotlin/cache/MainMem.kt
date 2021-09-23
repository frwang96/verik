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
    val rx: TxnIf.TxnRx
) : Module() {

    var mem: Unpacked<EXP<ADDR_WIDTH>, UbitData> = nc()

    @Seq
    fun update() {
        on(posedge(clk)) {
            rx.rspVld = false
            if (rx.rst) {
                for (i in 0 until mem.size) {
                    mem[i] = zeroes()
                }
            } else {
                if (rx.reqOp != Op.NOP) {
                    println("mem received op=${rx.reqOp} addr=0x${rx.reqAddr} data=0x${rx.reqData}")
                    if (rx.reqOp == Op.WRITE) {
                        mem[rx.reqAddr] = rx.reqData
                    } else {
                        rx.rspData = mem[rx.reqAddr]
                        rx.rspVld = true
                    }
                }
            }
        }
    }
}
