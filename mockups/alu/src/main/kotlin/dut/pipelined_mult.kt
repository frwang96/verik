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

import verik.base.*
import verik.data.*

class _pipelined_mult: _module {
    @input  var clk         = _bool()
    @input  var reset       = _bool()
    @input  var a           = _ubit(LEN)
    @input  var b           = _ubit(LEN)
    @input  var start       = _bool()
    @output var done_mult   = _bool()
    @output var result_mult = _ubit(2 * LEN)

    private var a_int         = _ubit(LEN)
    private var b_int         = _ubit(LEN)
    private var mult1         = _ubit(2 * LEN)
    private var mult2         = _ubit(2 * LEN)
    private var done1         = _bool()
    private var done2         = _bool()
    private var done3         = _bool()

    @seq fun pipelined_mult() {
        on (posedge(clk), posedge(reset)) {
            if (reset) {
                done_mult = false
                done3 = false
                done2 = false
                done1 = false
                a_int = ubit(0)
                b_int = ubit(0)
                mult1 = ubit(0)
                mult2 = ubit(0)
                result_mult = ubit(0)
            } else {
                a_int = a
                b_int = b
                mult1 = a_int mul b_int
                mult2 = mult1
                result_mult = mult2
                done3 = start && !done_mult
                done2 = done3 && !done_mult
                done1 = done2 && !done_mult
                done_mult = done1 && !done_mult
            }
        }
    }
}