/*
 * Copyright 2020 Francis Wang
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
import verik.common.base.*
import verik.common.data.*

val ADDR_WIDTH = 8
val DATA_WIDTH = 16
val DEPTH = 256

class _reg_bus: _bus {

    @input var clk = _bool()

    var rstn  = _bool()
    var addr  = _uint(ADDR_WIDTH)
    var wdata = _uint(DATA_WIDTH)
    var rdata = _uint(DATA_WIDTH)
    var wr    = _bool()
    var sel   = _bool()
    var ready = _bool()
}

@top class _tb: _module {

    var clk = _bool()

    @run fun clk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @make val reg_bus = _reg_bus() with {
        it.clk = clk
    }

    @make val reg_ctrl = _reg_ctrl(ADDR_WIDTH, DATA_WIDTH, uint(0x1234)) with {
        it.clk  = clk
        it.rstn = reg_bus.rstn
        it.addr = reg_bus.addr
        it.sel  = reg_bus.sel
        it.wr   = reg_bus.wr

        reg_bus.wdata = it.wdata
        reg_bus.rdata = it.rdata
        reg_bus.ready = it.ready
    }

    var t0 = _test()
    @run fun run() {
        t0 = test(reg_bus)
        run_test()
    }
}