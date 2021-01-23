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

    private var clk      = t_Boolean()
    private var rst      = t_Boolean()
    private var in_vld   = t_Boolean()
    private var addr     = t_Ubit(ADDR_WIDTH)
    private var write    = t_Boolean()
    private var in_data  = t_Ubit(DATA_WIDTH)
    private var out_vld  = t_Boolean()
    private var out_data = t_Ubit(DATA_WIDTH)

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }

    @make val cp = t_MemClockPort().with(
        event    = posedge(clk),
        out_vld  = out_vld,
        out_data = out_data,
        rst      = rst,
        in_vld   = in_vld,
        write    = write,
        addr     = addr,
        in_data  = in_data
    )

    @make val tb_bp = t_MemTbBusPort().with(cp)

    @make val tx_bp = t_MemTxBusPort().with(
        clk      = clk,
        out_vld  = out_vld,
        out_data = out_data,
        rst      = rst,
        in_vld   = in_vld,
        write    = write,
        addr     = addr,
        in_data  = in_data
    )

    @make val rx_bp = t_MemRxBusPort().with(
        clk      = clk,
        rst      = rst,
        in_vld   = in_vld,
        write    = write,
        addr     = addr,
        in_data  = in_data,
        out_vld  = out_vld,
        out_data = out_data
    )
}

class MemClockPort: ClockPort() {

    @input  var out_vld  = t_Boolean()
    @input  var out_data = t_Ubit(DATA_WIDTH)
    @output var rst      = t_Boolean()
    @output var in_vld   = t_Boolean()
    @output var write    = t_Boolean()
    @output var addr     = t_Ubit(ADDR_WIDTH)
    @output var in_data  = t_Ubit(DATA_WIDTH)
}

class MemTbBusPort: BusPort() {

    @inout val cp = t_MemClockPort()
}

class MemTxBusPort: BusPort() {

    @input  var clk      = t_Boolean()
    @input  var out_vld  = t_Boolean()
    @input  var out_data = t_Ubit(DATA_WIDTH)
    @output var rst      = t_Boolean()
    @output var in_vld  = t_Boolean()
    @output var write    = t_Boolean()
    @output var addr     = t_Ubit(ADDR_WIDTH)
    @output var in_data  = t_Ubit(DATA_WIDTH)
}

class MemRxBusPort: BusPort() {

    @input  var clk      = t_Boolean()
    @input  var rst      = t_Boolean()
    @input  var in_vld   = t_Boolean()
    @input  var write    = t_Boolean()
    @input  var addr     = t_Ubit(ADDR_WIDTH)
    @input  var in_data  = t_Ubit(DATA_WIDTH)
    @output var out_vld  = t_Boolean()
    @output var out_data = t_Ubit(DATA_WIDTH)
}
