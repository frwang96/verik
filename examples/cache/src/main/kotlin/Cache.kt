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
    @bidir val rx_bp: MemRxBusPort,
    @bidir val tx_bp: MemTxBusPort
): Module() {

    var state = State.READY
    val lines = Array<EXP<INDEX_WIDTH>, Line>()

    var cur_op: Op = d()
    var cur_addr: UbitAddr = d()
    var cur_data: UbitData = d()

    @seq fun update() {
        on(posedge(clk)) {
            rx_bp.rsp_vld = false
            tx_bp.rst = false
            tx_bp.req_op = Op.INVALID
            if (rx_bp.rst) {
                tx_bp.rst = true
                state = State.READY
                for (i in range(lines.size)) {
                    lines[i] = Line(Status.INVALID, u(0), u(0))
                }
            } else {
                when (state) {
                    State.READY -> {
                        if (rx_bp.req_op != Op.INVALID) {
                            println("cache received op=${rx_bp.req_op} addr=0x${rx_bp.req_addr} data=0x${rx_bp.req_data}")
                            cur_op   = rx_bp.req_op
                            cur_addr = rx_bp.req_addr
                            cur_data = rx_bp.req_data

                            val tag = get_tag(rx_bp.req_addr)
                            val index = get_index(rx_bp.req_addr)
                            val line = lines[index]
                            if (line.status != Status.INVALID && line.tag == tag) {
                                println("cache hit index=0x$index tag=0x$tag line.tag=0x${line.tag} line.status=${line.status}")
                                if (rx_bp.req_op == Op.WRITE) {
                                    lines[index].data = rx_bp.req_data
                                    lines[index].status = Status.DIRTY
                                } else {
                                    rx_bp.rsp_vld = true
                                    rx_bp.rsp_data = line.data
                                }
                            } else {
                                println("cache miss index=0x$index tag=0x$tag line.tag=0x${line.tag} line.status=${line.status}")
                                if (line.status == Status.DIRTY) {
                                    tx_bp.req_op = Op.WRITE
                                    tx_bp.req_addr = cat(line.tag, index)
                                    tx_bp.req_data = line.data
                                    state = State.WRITEBACK
                                } else {
                                    tx_bp.req_op = Op.READ
                                    tx_bp.req_addr = rx_bp.req_addr
                                    state = State.FILL
                                }
                            }
                        }
                    }
                    State.WRITEBACK -> {
                        tx_bp.req_op = Op.READ
                        tx_bp.req_addr = cur_addr
                        state = State.FILL
                    }
                    State.FILL -> {
                        if (tx_bp.rsp_vld) {
                            val tag = get_tag(cur_addr)
                            val index = get_index(cur_addr)
                            println("cache fill index=0x$index tag=0x$tag data=0x${tx_bp.rsp_data}")
                            lines[index] = Line(Status.CLEAN, tag, tx_bp.rsp_data)
                            if (cur_op == Op.WRITE) {
                                lines[index].data = cur_data
                                lines[index].status = Status.DIRTY
                            } else {
                                rx_bp.rsp_vld = true
                                rx_bp.rsp_data = tx_bp.rsp_data
                            }
                            state = State.READY
                        }
                    }
                }
            }
        }
    }

    private fun get_tag(addr: UbitAddr): UbitTag {
        return addr.slice(i<ADDR_WIDTH>() - i<TAG_WIDTH>())
    }

    private fun get_index(addr: UbitAddr): UbitIndex {
        return addr.slice(0)
    }
}