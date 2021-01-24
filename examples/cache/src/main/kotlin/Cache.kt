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

class Cache: Module() {

    @input val clk   = t_Boolean()
    @inout val rx_bp = t_MemRxBusPort()
    @inout val tx_bp = t_MemTxBusPort()

    private var state = t_State()

    private val lines = t_Array(exp(INDEX_WIDTH), t_Line())

    private var cur_op   = t_Op()
    private var cur_addr = t_Ubit(ADDR_WIDTH)
    private var cur_data = t_Ubit(DATA_WIDTH)

    @seq fun update() {
        on(posedge(clk)) {
            rx_bp.rsp_vld = false
            tx_bp.rst = false
            tx_bp.req_op = Op.INVALID
            if (rx_bp.rst) {
                tx_bp.rst = true
                state = State.READY
                for (i in range(exp(INDEX_WIDTH))) {
                    lines[i] = i_Line(Status.INVALID, u(0), u(0))
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
                            lines[index] = i_Line(Status.CLEAN, tag, tx_bp.rsp_data)
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

    private fun get_tag(addr: Ubit): Ubit {
        type(addr, t_Ubit(ADDR_WIDTH))
        type(t_Ubit(TAG_WIDTH))
        return addr[ADDR_WIDTH - 1, ADDR_WIDTH - TAG_WIDTH]
    }

    private fun get_index(addr: Ubit): Ubit {
        type(addr, t_Ubit(ADDR_WIDTH))
        type(t_Ubit(INDEX_WIDTH))
        return addr[INDEX_WIDTH - 1, 0]
    }
}