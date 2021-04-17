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
import verik.collection.Array
import verik.data.*

class Cache(
    @input val clk: Boolean,
    @bidir val rxBp: MemRxBusPort,
    @bidir val txBp: MemTxBusPort
): Module() {

    var state = State.READY
    val lines = Array<EXP<INDEX_WIDTH>, Line>()

    var curOp: Op = d()
    var curAddr: UbitAddr = d()
    var curData: UbitData = d()

    @seq fun update() {
        on(posedge(clk)) {
            rxBp.rspVld = false
            txBp.rst = false
            txBp.reqOp = Op.INVALID
            if (rxBp.rst) {
                txBp.rst = true
                state = State.READY
                for (i in range(lines.size)) {
                    lines[i] = Line(Status.INVALID, u(0), u(0))
                }
            } else {
                when (state) {
                    State.READY -> {
                        if (rxBp.reqOp != Op.INVALID) {
                            println("cache received op=${rxBp.reqOp} addr=0x${rxBp.reqAddr} data=0x${rxBp.reqData}")
                            curOp = rxBp.reqOp
                            curAddr = rxBp.reqAddr
                            curData = rxBp.reqData

                            val tag = getTag(rxBp.reqAddr)
                            val index = getIndex(rxBp.reqAddr)
                            val line = lines[index]
                            if (line.status != Status.INVALID && line.tag == tag) {
                                println("cache hit index=0x$index tag=0x$tag line.tag=0x${line.tag} line.status=${line.status}")
                                if (rxBp.reqOp == Op.WRITE) {
                                    lines[index].data = rxBp.reqData
                                    lines[index].status = Status.DIRTY
                                } else {
                                    rxBp.rspVld = true
                                    rxBp.rspData = line.data
                                }
                            } else {
                                println("cache miss index=0x$index tag=0x$tag line.tag=0x${line.tag} line.status=${line.status}")
                                if (line.status == Status.DIRTY) {
                                    txBp.reqOp = Op.WRITE
                                    txBp.reqAddr = cat(line.tag, index)
                                    txBp.reqData = line.data
                                    state = State.WRITEBACK
                                } else {
                                    txBp.reqOp = Op.READ
                                    txBp.reqAddr = rxBp.reqAddr
                                    state = State.FILL
                                }
                            }
                        }
                    }
                    State.WRITEBACK -> {
                        txBp.reqOp = Op.READ
                        txBp.reqAddr = curAddr
                        state = State.FILL
                    }
                    State.FILL -> {
                        if (txBp.rspVld) {
                            val tag = getTag(curAddr)
                            val index = getIndex(curAddr)
                            println("cache fill index=0x$index tag=0x$tag data=0x${txBp.rspData}")
                            lines[index] = Line(Status.CLEAN, tag, txBp.rspData)
                            if (curOp == Op.WRITE) {
                                lines[index].data = curData
                                lines[index].status = Status.DIRTY
                            } else {
                                rxBp.rspVld = true
                                rxBp.rspData = txBp.rspData
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