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

class _add_and_xor: _module {
    @input  var a          = _uint(LEN)
    @input  var b          = _uint(LEN)
    @input  var clk        = _bool()
    @input  var op         = _uint(3)
    @input  var reset      = _bool()
    @input  var start      = _bool()
    @output var done_aax   = _bool()
    @output var result_aax = _uint(2 * LEN)

    @seq fun result() {
        on (posedge(clk)) {
            if (reset) { // Synchronous reset
                result_aax reg 0
            } else {
                if (start) {
                    when (op) {
                        uint(3, 0b001) -> result_aax reg ext(2 * LEN, a add b)
                        uint(3, 0b010) -> result_aax reg ext(2 * LEN, a and b)
                        uint(3, 0b011) -> result_aax reg ext(2 * LEN, a xor b)
                        else -> result_aax reg X
                    }
                }
            }
        }
    }

    @seq fun done() {
        on(posedge(clk), posedge(reset)) {
            done_aax reg if (reset) true else start && !!op
        }
    }
}
