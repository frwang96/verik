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
import verik.collection.*
import verik.data.*

class RegCtrl: Module() {

    @input  var clk   = t_Boolean()
    @input  var rst_n = t_Boolean()
    @input  var addr  = t_Ubit(ADDR_WIDTH)
    @input  var sel   = t_Boolean()
    @input  var wr    = t_Boolean()
    @input  var wdata = t_Ubit(DATA_WIDTH)
    @output var rdata = t_Ubit(DATA_WIDTH)
    @output var ready = t_Boolean()

    val ctrl = t_Array(DEPTH, t_Ubit(DATA_WIDTH))

    var ready_pe  = t_Boolean()
    var ready_dly = t_Boolean()

    @com fun set_ready_pe() {
        ready_pe = !ready && ready_dly
    }

    @seq fun set_ready_dly() {
        on (posedge(clk)) {
            ready_dly = if (!rst_n) true else ready
        }
    }

    @seq fun set_ready() {
        on (posedge(clk)) {
            if (!rst_n) ready = true
            else {
                if (sel and ready_pe) ready = true
                if (sel and ready and !wr) ready = false
            }
        }
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
                    rdata = u(0)
                }
            }
        }
    }
}
