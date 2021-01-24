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
import verik.data.*

class MemBus: Bus() {

    @input private var clk = t_Boolean()

    private var rst      = t_Boolean()
    private var req_op   = t_Op()
    private var req_addr = t_Ubit(ADDR_WIDTH)
    private var req_data = t_Ubit(DATA_WIDTH)
    private var rsp_vld  = t_Boolean()
    private var rsp_data = t_Ubit(DATA_WIDTH)

    @make val cp = t_MemClockPort().with(
        event    = posedge(clk),
        rst      = rst,
        req_op   = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld  = rsp_vld,
        rsp_data = rsp_data
    )

    @make val tb_bp = t_MemTbBusPort().with(cp)

    @make val tx_bp = t_MemTxBusPort().with(
        rst      = rst,
        req_op   = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld  = rsp_vld,
        rsp_data = rsp_data
    )

    @make val rx_bp = t_MemRxBusPort().with(
        rst      = rst,
        req_op   = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld  = rsp_vld,
        rsp_data = rsp_data
    )
}

class MemClockPort: ClockPort() {

    @output var rst = t_Boolean()
    @output var req_op   = t_Op()
    @output var req_addr = t_Ubit(ADDR_WIDTH)
    @output var req_data = t_Ubit(DATA_WIDTH)
    @input  var rsp_vld  = t_Boolean()
    @input  var rsp_data = t_Ubit(DATA_WIDTH)
}

class MemTbBusPort: BusPort() {

    @inout val cp = t_MemClockPort()
}

class MemTxBusPort: BusPort() {

    @output var rst = t_Boolean()
    @output var req_op   = t_Op()
    @output var req_addr = t_Ubit(ADDR_WIDTH)
    @output var req_data = t_Ubit(DATA_WIDTH)
    @input  var rsp_vld  = t_Boolean()
    @input  var rsp_data = t_Ubit(DATA_WIDTH)
}

class MemRxBusPort: BusPort() {

    @input  var rst = t_Boolean()
    @input  var req_op   = t_Op()
    @input  var req_addr = t_Ubit(ADDR_WIDTH)
    @input  var req_data = t_Ubit(DATA_WIDTH)
    @output var rsp_vld  = t_Boolean()
    @output var rsp_data = t_Ubit(DATA_WIDTH)
}
