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
import verik.collection.Array
import verik.data.*

typealias ADDR_WIDTH = _8
typealias DATA_WIDTH = _16
typealias DEPTH = _256

val RESET_VAL: Ubit<DATA_WIDTH> = u(0x1234)

class RegCtrl(
    @input  var clk: Boolean,
    @input  var rstN: Boolean,
    @input  var addr: Ubit<ADDR_WIDTH>,
    @input  var sel: Boolean,
    @input  var wr: Boolean,
    @input  var wdata: Ubit<DATA_WIDTH>,
    @output var rdata: Ubit<DATA_WIDTH>,
    @output var ready: Boolean
): Module() {

    val ctrl = Array<DEPTH, Ubit<DATA_WIDTH>>()

    var readyPe: Boolean = d()
    var readyDly: Boolean = d()

    @com fun setReadyPe() {
        readyPe = !ready && readyDly
    }

    @seq fun setReadyDly() {
        on (posedge(clk)) {
            readyDly = if (!rstN) true else ready
        }
    }

    @seq fun setReady() {
        on (posedge(clk)) {
            if (!rstN) ready = true
            else {
                if (sel and readyPe) ready = true
                if (sel and ready and !wr) ready = false
            }
        }
    }

    @seq fun readWrite() {
        on (posedge(clk)) {
            if (!rstN) {
                for (n in range(ctrl.size)) {
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
