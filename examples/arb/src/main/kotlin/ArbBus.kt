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

class ArbBus(
    @input var clk: Boolean
): Bus() {

    var rst     : Boolean = d()
    var request : Ubit<_2> = d()
    var grant   : Ubit<_2> = d()

    @ins val cp = ArbClockPort(
        event   = posedge(clk),
        grant   = grant,
        request = request
    )

    @ins val testBp = ArbTestBusPort(
        rst = rst,
        cp  = cp
    )

    @ins val dutBp = ArbDutBusPort(
        clk     = clk,
        rst     = rst,
        request = request,
        grant   = grant
    )
}

class ArbClockPort(
    event: Event,
    @input  var grant: Ubit<_2>,
    @output var request: Ubit<_2>
): ClockPort(event)

class ArbTestBusPort(
    @output var rst: Boolean,
    @bidir  var cp: ArbClockPort
): BusPort()

class ArbDutBusPort(
    @input  var clk: Boolean,
    @input  var rst: Boolean,
    @input  var request: Ubit<_2>,
    @output var grant: Ubit<_2>
): BusPort()
