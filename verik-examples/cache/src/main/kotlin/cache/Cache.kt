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

class Cache(
    @In var clk: Boolean,
    var ifRx: TxnIf.TxnRx,
    var ifTx: TxnIf.TxnTx
) : Module() {

    val lines: Unpacked<EXP<INDEX_WIDTH>, Line> = nc()
    var state: State = nc()
    var curOp: Op = nc()
    var curAddr: UbitAddr = nc()
    var curData: UbitData = nc()

    @Seq
    fun update() {
        on(posedge(clk)) {
            ifRx.rspVld = false
            ifTx.rst = false
            ifTx.reqOp = Op.NOP
            if (ifRx.rst) {
                ifTx.rst = true
                state = State.READY
                for (i in 0 until lines.size) {
                    lines[i] = Line(Status.INVALID, u0(), u0())
                }
            } else {
                when (state) {
                    State.READY -> {
                        if (ifRx.reqOp != Op.NOP) {
                            println("cache received op=${ifRx.reqOp} addr=0x${ifRx.reqAddr} data=0x${ifRx.reqData}")
                            curOp = ifRx.reqOp
                            curAddr = ifRx.reqAddr
                            curData = ifRx.reqData

                            val tag = getTag(ifRx.reqAddr)
                            val index = getIndex(ifRx.reqAddr)
                            val line = lines[index]
                            if (line.status != Status.INVALID && line.tag == tag) {
                                print("cache hit index=0x$index tag=0x$tag")
                                println(" line.tag=0x${line.tag} line.status=${line.status}")
                                if (ifRx.reqOp == Op.WRITE) {
                                    lines[index].data = ifRx.reqData
                                    lines[index].status = Status.DIRTY
                                } else {
                                    ifRx.rspVld = true
                                    ifRx.rspData = line.data
                                }
                            } else {
                                print("cache miss index=0x$index tag=0x$tag")
                                println(" line.tag=0x${line.tag} line.status=${line.status}")
                                if (line.status == Status.DIRTY) {
                                    ifTx.reqOp = Op.WRITE
                                    ifTx.reqAddr = cat(line.tag, index)
                                    ifTx.reqData = line.data
                                    state = State.WRITEBACK
                                } else {
                                    ifTx.reqOp = Op.READ
                                    ifTx.reqAddr = ifRx.reqAddr
                                    state = State.FILL
                                }
                            }
                        }
                    }
                    State.WRITEBACK -> {
                        ifTx.reqOp = Op.READ
                        ifTx.reqAddr = curAddr
                        state = State.FILL
                    }
                    State.FILL -> {
                        if (ifTx.rspVld) {
                            val tag = getTag(curAddr)
                            val index = getIndex(curAddr)
                            println("cache fill index=0x$index tag=0x$tag data=0x${ifTx.rspData}")
                            lines[index] = Line(Status.CLEAN, tag, ifTx.rspData)
                            if (curOp == Op.WRITE) {
                                lines[index].data = curData
                                lines[index].status = Status.DIRTY
                            } else {
                                ifRx.rspVld = true
                                ifRx.rspData = ifTx.rspData
                            }
                            state = State.READY
                        }
                    }
                }
            }
        }
    }

    private fun getTag(addr: UbitAddr): UbitTag {
        return addr.slice(i<ADDR_WIDTH>() - i<TAG_WIDTH>())
    }

    private fun getIndex(addr: UbitAddr): UbitIndex {
        return addr.slice(0)
    }
}
