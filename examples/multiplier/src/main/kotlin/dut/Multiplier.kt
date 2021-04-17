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

package dut

import verik.base.*
import verik.data.*

typealias DATA_WIDTH = _8
typealias RES_WIDTH = MUL<DATA_WIDTH, _2>
typealias COUNTER_WIDTH = LOG<INC<DATA_WIDTH>>

val COUNTER_MAX = u<COUNTER_WIDTH>(i<DATA_WIDTH>())

class Multiplier(
    @input  var clk: Boolean,
    @input  var rst: Boolean,
    @input  var req: MultiplierReq,
    @output var res: Ubit<RES_WIDTH>,
    @output var res_rdy: Boolean
): Module() {

    var a: Ubit<DATA_WIDTH> = d()
    var b: Ubit<DATA_WIDTH> = d()
    var prod: Ubit<DATA_WIDTH> = d()
    var tp: Ubit<DATA_WIDTH> = d()
    var i: Ubit<COUNTER_WIDTH> = d()

    @seq fun mul_step() {
        on (posedge(clk)) {
            if (rst) {
                a = u(0)
                b = u(0)
                prod = u(0)
                tp = u(0)
                i = COUNTER_MAX
            } else {
                if (req.vld) {
                    a = req.a
                    b = req.b
                    prod = u(0)
                    tp = u(0)
                    i = u(0)
                } else if (i < COUNTER_MAX) {
                    val sum = if (b[0]) tp add a else tp.ext()
                    b = b shr 1
                    prod = cat(sum[0], prod.slice<DEC<DATA_WIDTH>>(1))
                    tp = sum.slice(1)
                    i += u(1)
                }
            }
        }
    }

    @com fun set_res () {
        res_rdy = (i == COUNTER_MAX)
        res = cat(tp, prod)
    }
}
