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

package dut

import verik.common.*
import verik.common.data.*

class _pipelined_mult: _module {
    @input  var a           = _uint(LEN)
    @input  var b           = _uint(LEN)
    @input  var clk         = _bool()
    @input  var reset       = _bool()
    @input  var start       = _bool()
    @output var done_mult   = _bool()
    @output var result_mult = _uint(2 * LEN)

    var a_int         = _uint(LEN)
    var b_int         = _uint(LEN)
    var mult1         = _uint(2 * LEN)
    var mult2         = _uint(2 * LEN)
    var done1         = _bool()
    var done2         = _bool()
    var done3         = _bool()
    var done_mult_int = _bool()

    @seq fun pipelined_mult() {
        on (posedge(clk), posedge(reset)) {
            if (reset) {
                done_mult_int reg false
                done3 reg false
                done2 reg false
                done1 reg false
                cat(a_int, b_int) reg 0
                cat(mult1, mult2) reg 0
                result_mult reg 0
            } else {
                a_int reg a
                b_int reg b
                mult1 reg (a_int mul b_int)
                mult2 reg mult1
                result_mult reg mult2
                done3 reg (start && !done_mult_int)
                done2 reg (done3 && !done_mult_int)
                done1 reg (done2 && !done_mult_int)
                done_mult_int reg (done1 && !done_mult_int)
            }
        }
    }

    @comb fun done() {
        done_mult put done_mult_int
    }
}