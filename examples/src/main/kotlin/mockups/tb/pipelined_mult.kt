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

package mockups.tb

import io.verik.common.*
import io.verik.types.*

class _pipelined_mult: _circuit {
    @input  val a           = _uint(8)
    @input  val b           = _uint(8)
    @input  val clk         = _bool()
    @input  val reset       = _bool()
    @input  val start       = _bool()
    @output val done_mult   = _bool()
    @output val result_mult = _uint(16)

    val a_int         = _uint(8)
    val b_int         = _uint(8)
    val mult1         = _uint(16)
    val mult2         = _uint(16)
    val done1         = _bool()
    val done2         = _bool()
    val done3         = _bool()
    val done_mult_int = _bool()

    @reg fun pipelined_mult() {
        on (posedge(clk), posedge(reset)) {
            if (reset) {
                done_mult_int reg false
                done3 reg false
                done2 reg false
                done1 reg false
                (a_int cat b_int) reg 0
                (mult1 cat mult2) reg 0
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

    @put fun put_done() {
        done_mult put done_mult_int
    }
}