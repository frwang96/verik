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

class MemBus(
    @input var clk: Boolean
): Bus() {

    private var rst: Boolean = d()
    private var req_op: Op = d()
    private var req_addr: UbitAddr = d()
    private var req_data: UbitData = d()
    private var rsp_vld: Boolean = d()
    private var rsp_data: UbitData = d()

    @ins val cp = MemClockPort(
        event    = posedge(clk),
        rst      = rst,
        req_op   = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld  = rsp_vld,
        rsp_data = rsp_data
    )

    @ins val tb_bp = MemTbBusPort(cp)

    @ins val tx_bp = MemTxBusPort(
        rst      = rst,
        req_op   = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld  = rsp_vld,
        rsp_data = rsp_data
    )

    @ins val rx_bp = MemRxBusPort(
        rst      = rst,
        req_op   = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld  = rsp_vld,
        rsp_data = rsp_data
    )
}

class MemClockPort(
    event: Event,
    @output var rst: Boolean,
    @output var req_op: Op,
    @output var req_addr: UbitAddr,
    @output var req_data: UbitData,
    @input  var rsp_vld: Boolean,
    @input  var rsp_data: UbitData
): ClockPort(event)

class MemTbBusPort(
    @bidir val cp: MemClockPort
): BusPort()

class MemTxBusPort(
    @output var rst: Boolean,
    @output var req_op: Op,
    @output var req_addr: UbitAddr,
    @output var req_data: UbitData,
    @input  var rsp_vld: Boolean,
    @input  var rsp_data: UbitData
): BusPort()

class MemRxBusPort(
    @input  var rst: Boolean,
    @input  var req_op: Op,
    @input  var req_addr: UbitAddr,
    @input  var req_data: UbitData,
    @output var rsp_vld: Boolean,
    @output var rsp_data: UbitData
): BusPort()