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

class _add_and_xor: _module() {
    @input  var clk        = _bool()
    @input  var reset      = _bool()
    @input  var a          = _ubit(LEN)
    @input  var b          = _ubit(LEN)
    @input  var op         = _ubit(3)
    @input  var start      = _bool()
    @output var done_aax   = _bool()
    @output var result_aax = _ubit(2 * LEN)

    @seq fun result() {
        // Synchronous reset
        on (posedge(clk)) {
            if (reset) {
                result_aax = ubit(0)
            } else {
                if (start) {
                    result_aax = when (op) {
                        ubit(0b001) -> (a add b).ext(2 * LEN)
                        ubit(0b010) -> (a and b).ext(2 * LEN)
                        ubit(0b011) -> (a xor b).ext(2 * LEN)
                        else -> _ubit(2 * LEN).to_x()
                    }
                }
            }
        }
    }

    @seq fun done() {
        // Asynchronous reset
        on(posedge(clk), posedge(reset)) {
            done_aax = if (reset) true
            else start && !!op
        }
    }
}
