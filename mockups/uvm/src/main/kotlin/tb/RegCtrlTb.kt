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

import uvm.base.run_test
import verik.base.*
import verik.data.*

val ADDR_WIDTH = 8
val DATA_WIDTH = 16
val DEPTH = 256
val RESET_VAL = u(16, 0x1234)

class RegBus: Bus() {

    @input var clk = t_Boolean()

    var rst_n  = t_Boolean()
    var addr  = t_Ubit(ADDR_WIDTH)
    var wdata = t_Ubit(DATA_WIDTH)
    var rdata = t_Ubit(DATA_WIDTH)
    var wr    = t_Boolean()
    var sel   = t_Boolean()
    var ready = t_Boolean()
}

class Tb: Module() {

    private var clk = t_Boolean()

    @run fun clk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @make val reg_bus = t_RegBus().with(clk)

    @make val reg_ctrl = t_RegCtrl().with(
        clk   = clk,
        rst_n = reg_bus.rst_n,
        addr  = reg_bus.addr,
        sel   = reg_bus.sel,
        wr    = reg_bus.wr,
        wdata = reg_bus.wdata,
        rdata = reg_bus.rdata,
        ready = reg_bus.ready
    )

    private var t0 = t_Test()
    @run fun run() {
        t0 = i_Test(reg_bus)
        run_test()
    }
}