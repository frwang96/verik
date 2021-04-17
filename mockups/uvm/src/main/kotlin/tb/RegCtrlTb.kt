/*
 * Copyright (c) 2020 Francis Wang
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

package tb

import verik.base.*
import verik.data.*

class RegBus(
    @input var clk: Boolean
): Bus() {

    var rst_n: Boolean = d()
    var addr: Ubit<ADDR_WIDTH> = d()
    var wdata: Ubit<DATA_WIDTH> = d()
    var rdata: Ubit<DATA_WIDTH> = d()
    var wr: Boolean = d()
    var sel: Boolean = d()
    var ready: Boolean = d()
}

@top object Tb: Module() {

    var clk = false

    @run fun clk() {
        forever {
            delay(10)
            clk = !clk
        }
    }

    @ins val reg_bus = RegBus(clk)

    @ins val reg_ctrl = RegCtrl(
        clk   = clk,
        rst_n = reg_bus.rst_n,
        addr  = reg_bus.addr,
        sel   = reg_bus.sel,
        wr    = reg_bus.wr,
        wdata = reg_bus.wdata,
        rdata = reg_bus.rdata,
        ready = reg_bus.ready
    )

    @run fun run() {
        Test(reg_bus)
    }
}