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

import verik.common.*
import verik.common.collections.*
import verik.common.data.*

class _reg_ctrl(
    val ADDR_WIDTH: _int,
    val DATA_WIDTH: _int,
    val RESET_VAL: _uint = _uint(DATA_WIDTH)
): _module {
    @input  var clk   = _bool()
    @input  var rstn  = _bool()
    @input  var addr  = _uint(ADDR_WIDTH)
    @input  var sel   = _bool()
    @input  var wr    = _bool()
    @input  var wdata = _uint(DATA_WIDTH)
    @output var rdata = _uint(DATA_WIDTH)
    @output var ready = _bool()

    var ctrl = _array(_uint(DATA_WIDTH), DEPTH)

    var ready_dly = seq (posedge(clk)) {
        if (!rstn) true else ready
    }

    var ready_pe  = com {
        !ready && ready_dly
    }

    @seq fun read_write() {
        on (posedge(clk)) {
            if (!rstn) {
                ctrl for_indices { ctrl[it] *= RESET_VAL }
            }
            else {
                if (sel and ready) {
                    if (wr) ctrl[addr] *= wdata
                    else rdata *= ctrl[addr]
                } else {
                    rdata *= 0
                }
            }
        }
    }

    @seq fun reg_ready() {
        on (posedge(clk)) {
            if (!rstn) ready *= true
            else {
                if (sel and ready_pe) ready *= true
                if (sel and ready and !wr) ready *= false
            }
        }
    }
}
