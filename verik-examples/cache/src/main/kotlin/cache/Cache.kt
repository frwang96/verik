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
    var rx: TxnIf.TxnRx,
    var tx: TxnIf.TxnTx
) : Module() {

    val lines = Unpacked<EXP<INDEX_WIDTH>, Line>()
    var state: State = x()
    var curOp: Op = x()
    var curAddr: UbitAddr = x()
    var curData: UbitData = x()

    @Seq
    fun update() {
        on(posedge(clk)) {
            rx.rspVld = false
            tx.rst = false
            tx.reqOp = Op.NOP
            if (rx.rst) {
                tx.rst = true
                state = State.READY
                for (i in 0 until lines.size) {
                    lines[i] = Line(Status.INVALID, u(0), u(0))
                }
            } else {
                when (state) {
                    State.READY -> {
                        if (rx.reqOp != Op.NOP) {
                            println("cache received op=${rx.reqOp} addr=0x${rx.reqAddr} data=0x${rx.reqData}")
                            curOp = rx.reqOp
                            curAddr = rx.reqAddr
                            curData = rx.reqData

                            val tag = getTag(rx.reqAddr)
                            val index = getIndex(rx.reqAddr)
                            val line = lines[index]
                            if (line.status != Status.INVALID && line.tag == tag) {
                                print("cache hit index=0x$index tag=0x$tag")
                                println(" line.tag=0x${line.tag} line.status=${line.status}")
                                if (rx.reqOp == Op.WRITE) {
                                    lines[index].data = rx.reqData
                                    lines[index].status = Status.DIRTY
                                } else {
                                    rx.rspVld = true
                                    rx.rspData = line.data
                                }
                            } else {
                                print("cache miss index=0x$index tag=0x$tag")
                                println(" line.tag=0x${line.tag} line.status=${line.status}")
                                if (line.status == Status.DIRTY) {
                                    tx.reqOp = Op.WRITE
                                    tx.reqAddr = cat(line.tag, index)
                                    tx.reqData = line.data
                                    state = State.WRITEBACK
                                } else {
                                    tx.reqOp = Op.READ
                                    tx.reqAddr = rx.reqAddr
                                    state = State.FILL
                                }
                            }
                        }
                    }
                    State.WRITEBACK -> {
                        tx.reqOp = Op.READ
                        tx.reqAddr = curAddr
                        state = State.FILL
                    }
                    State.FILL -> {
                        if (tx.rspVld) {
                            val tag = getTag(curAddr)
                            val index = getIndex(curAddr)
                            println("cache fill index=0x$index tag=0x$tag data=0x${tx.rspData}")
                            lines[index] = Line(Status.CLEAN, tag, tx.rspData)
                            if (curOp == Op.WRITE) {
                                lines[index].data = curData
                                lines[index].status = Status.DIRTY
                            } else {
                                rx.rspVld = true
                                rx.rspData = tx.rspData
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
