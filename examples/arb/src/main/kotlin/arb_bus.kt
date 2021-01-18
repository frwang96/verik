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

class _arb_bus: _bus() {

    @input var clk = _bool()

    var rst     = _bool()
    var request = _ubit(2)
    var grant   = _ubit(2)

    @make val dut_bp = _arb_dut_bp() with {
        it.clk = clk
        it.rst = rst
        it.request = request
        grant = it.grant
    }
}

class _arb_dut_bp: _busport() {

    @input var clk     = _bool()
    @input var rst     = _bool()
    @input var request = _ubit(2)
    @output var grant  = _ubit(2)
}
