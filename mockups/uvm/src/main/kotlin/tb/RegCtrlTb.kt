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

    var rstN: Boolean = d()
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

    @ins val regBus = RegBus(clk)

    @ins val regCtrl = RegCtrl(
        clk   = clk,
        rstN  = regBus.rstN,
        addr  = regBus.addr,
        sel   = regBus.sel,
        wr    = regBus.wr,
        wdata = regBus.wdata,
        rdata = regBus.rdata,
        ready = regBus.ready
    )

    @run fun run() {
        Test(regBus)
    }
}