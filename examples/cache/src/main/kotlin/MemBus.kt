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
    private var reqOp: Op = d()
    private var reqAddr: UbitAddr = d()
    private var reqData: UbitData = d()
    private var rspVld: Boolean = d()
    private var rspData: UbitData = d()

    @ins val cp = MemClockPort(
        event   = posedge(clk),
        rst     = rst,
        reqOp   = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld  = rspVld,
        rspData = rspData
    )

    @ins val tbBp = MemTbBusPort(cp)

    @ins val txBp = MemTxBusPort(
        rst     = rst,
        reqOp   = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld  = rspVld,
        rspData = rspData
    )

    @ins val rxBp = MemRxBusPort(
        rst     = rst,
        reqOp   = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld  = rspVld,
        rspData = rspData
    )
}

class MemClockPort(
    event: Event,
    @output var rst: Boolean,
    @output var reqOp: Op,
    @output var reqAddr: UbitAddr,
    @output var reqData: UbitData,
    @input  var rspVld: Boolean,
    @input  var rspData: UbitData
): ClockPort(event)

class MemTbBusPort(
    @bidir val cp: MemClockPort
): BusPort()

class MemTxBusPort(
    @output var rst: Boolean,
    @output var reqOp: Op,
    @output var reqAddr: UbitAddr,
    @output var reqData: UbitData,
    @input  var rspVld: Boolean,
    @input  var rspData: UbitData
): BusPort()

class MemRxBusPort(
    @input  var rst: Boolean,
    @input  var reqOp: Op,
    @input  var reqAddr: UbitAddr,
    @input  var reqData: UbitData,
    @output var rspVld: Boolean,
    @output var rspData: UbitData
): BusPort()