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

class ArbBus: Bus() {

    @input var clk = t_Boolean()

    var rst     = t_Boolean()
    var request = t_Ubit(2)
    var grant   = t_Ubit(2)

    @make val cp = t_ArbClockPort().with(
        event   = posedge(clk),
        grant   = grant,
        request = request
    )

    @make val test_bp = t_ArbTestBusPort().with(
        rst = rst,
        cp  = cp
    )

    @make val dut_bp = t_ArbDutBusPort().with(
        clk     = clk,
        rst     = rst,
        request = request,
        grant   = grant
    )
}

class ArbClockPort: ClockPort() {

    @input var grant    = t_Ubit(2)
    @output var request = t_Ubit(2)
}

class ArbTestBusPort: BusPort() {

    @output var rst = t_Boolean()
    @inout val cp   = t_ArbClockPort()
}

class ArbDutBusPort: BusPort() {

    @input var clk     = t_Boolean()
    @input var rst     = t_Boolean()
    @input var request = t_Ubit(2)
    @output var grant  = t_Ubit(2)
}
