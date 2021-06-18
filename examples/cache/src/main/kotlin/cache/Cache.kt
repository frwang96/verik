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
    var rxp: TxnBus.TxnRxPort,
    var txp: TxnBus.TxnTxPort
) : Module() {

    val lines = VArray<EXP<INDEX_WIDTH>, Line>()
    var state: State = x()
    var curOp: Op = x()
    var curAddr: UbitAddr = x()
    var curData: UbitData = x()

    @Seq
    fun update() {
        on(posedge(clk)) {
            rxp.rspVld = false
            txp.rst = false
            txp.reqOp = Op.NOP
            if (rxp.rst) {
                txp.rst = true
                state = State.READY
                for (i in range(lines.size)) {
                    lines[i] = Line(Status.INVALID, u(0), u(0))
                }
            } else {
                when (state) {
                    State.READY -> {
                        if (rxp.reqOp != Op.NOP) {
                            println("cache received op=${rxp.reqOp} addr=0x${rxp.reqAddr} data=0x${rxp.reqData}")
                            curOp = rxp.reqOp
                            curAddr = rxp.reqAddr
                            curData = rxp.reqData

                            val tag = getTag(rxp.reqAddr)
                            val index = getIndex(rxp.reqAddr)
                            val line = lines[index]
                            if (line.status != Status.INVALID && line.tag == tag) {
                                print("cache hit index=0x$index tag=0x$tag")
                                println(" line.tag=0x${line.tag} line.status=${line.status}")
                                if (rxp.reqOp == Op.WRITE) {
                                    lines[index].data = rxp.reqData
                                    lines[index].status = Status.DIRTY
                                } else {
                                    rxp.rspVld = true
                                    rxp.rspData = line.data
                                }
                            } else {
                                print("cache miss index=0x$index tag=0x$tag")
                                println(" line.tag=0x${line.tag} line.status=${line.status}")
                                if (line.status == Status.DIRTY) {
                                    txp.reqOp = Op.WRITE
                                    txp.reqAddr = cat(line.tag, index)
                                    txp.reqData = line.data
                                    state = State.WRITEBACK
                                } else {
                                    txp.reqOp = Op.READ
                                    txp.reqAddr = rxp.reqAddr
                                    state = State.FILL
                                }
                            }
                        }
                    }
                    State.WRITEBACK -> {
                        txp.reqOp = Op.READ
                        txp.reqAddr = curAddr
                        state = State.FILL
                    }
                    State.FILL -> {
                        if (txp.rspVld) {
                            val tag = getTag(curAddr)
                            val index = getIndex(curAddr)
                            println("cache fill index=0x$index tag=0x$tag data=0x${txp.rspData}")
                            lines[index] = Line(Status.CLEAN, tag, txp.rspData)
                            if (curOp == Op.WRITE) {
                                lines[index].data = curData
                                lines[index].status = Status.DIRTY
                            } else {
                                rxp.rspVld = true
                                rxp.rspData = txp.rspData
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