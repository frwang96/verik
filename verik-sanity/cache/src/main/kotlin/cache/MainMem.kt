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
    val ifRx: TxnIf.TxnRx
) : Module() {

    var mem: Unpacked<EXP<ADDR_WIDTH>, UbitData> = nc()

    @Seq
    fun update() {
        on(posedge(clk)) {
            ifRx.rspVld = false
            if (ifRx.rst) {
                for (i in 0 until mem.size) {
                    mem[i] = u0()
                }
            } else {
                if (ifRx.reqOp != Op.NOP) {
                    println("mem received op=${ifRx.reqOp} addr=0x${ifRx.reqAddr} data=0x${ifRx.reqData}")
                    if (ifRx.reqOp == Op.WRITE) {
                        mem[ifRx.reqAddr] = ifRx.reqData
                    } else {
                        ifRx.rspData = mem[ifRx.reqAddr]
                        ifRx.rspVld = true
                    }
                }
            }
        }
    }
}
