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

import verik.base.*
import verik.collections.*
import verik.data.*

class _reg_ctrl(
    private val ADDR_WIDTH: _int,
    private val DATA_WIDTH: _int,
    private val RESET_VAL: _ubit
): _module {

    @input  var clk   = _bool()
    @input  var rst_n  = _bool()
    @input  var addr  = _ubit(ADDR_WIDTH)
    @input  var sel   = _bool()
    @input  var wr    = _bool()
    @input  var wdata = _ubit(DATA_WIDTH)
    @output var rdata = _ubit(DATA_WIDTH)
    @output var ready = _bool()

    private var ctrl = _array(_ubit(DATA_WIDTH), DEPTH)

    private var ready_dly = seq (posedge(clk)) {
        if (!rst_n) true else ready
    }

    private var ready_pe  = com {
        !ready && ready_dly
    }

    @seq fun read_write() {
        on (posedge(clk)) {
            if (!rst_n) {
                for (n in range(ctrl.SIZE)) {
                    ctrl[n] = RESET_VAL
                }
            }
            else {
                if (sel and ready) {
                    if (wr) ctrl[addr] = wdata
                    else rdata = ctrl[addr]
                } else {
                    rdata = ubit(0)
                }
            }
        }
    }

    @seq fun reg_ready() {
        on (posedge(clk)) {
            if (!rst_n) ready = true
            else {
                if (sel and ready_pe) ready = true
                if (sel and ready and !wr) ready = false
            }
        }
    }
}
