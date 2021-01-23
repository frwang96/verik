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
    private var write_en = t_Boolean()
    private var addr     = t_Ubit(ADDR_WIDTH)
    private var data_in  = t_Ubit(DATA_WIDTH)
    private var data_out = t_Ubit(DATA_WIDTH)

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }

    @make val cp = t_MemClockPort().with(
        event    = posedge(clk),
        rst      = rst,
        write_en = write_en,
        addr     = addr,
        data_in  = data_in,
        data_out = data_out
    )

    @make val test_bp = t_MemTestBusPort().with(cp)

    @make val dut_bp = t_MemDutBusPort().with(
        clk      = clk,
        rst      = rst,
        write_en = write_en,
        addr     = addr,
        data_in  = data_in,
        data_out = data_out
    )
}

class MemClockPort: ClockPort() {

    @output var rst      = t_Boolean()
    @output var write_en = t_Boolean()
    @output var addr     = t_Ubit(ADDR_WIDTH)
    @output var data_in  = t_Ubit(DATA_WIDTH)
    @input  var data_out = t_Ubit(DATA_WIDTH)
}

class MemTestBusPort: BusPort() {

    @inout val cp = t_MemClockPort()
}

class MemDutBusPort: BusPort() {

    @input  var clk      = t_Boolean()
    @input  var rst      = t_Boolean()
    @input  var write_en = t_Boolean()
    @input  var addr     = t_Ubit(ADDR_WIDTH)
    @input  var data_in  = t_Ubit(DATA_WIDTH)
    @output var data_out = t_Ubit(DATA_WIDTH)
}
