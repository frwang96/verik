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
import io.verik.common.types.*

class _add_and_xor: _module {
    @input  val a          = _uint(8)
    @input  val b          = _uint(8)
    @input  val clk        = _bool()
    @input  val op         = _uint(3)
    @input  val reset      = _bool()
    @input  val start      = _bool()
    @output val done_aax   = _bool()
    @output val result_aax = _uint(16)

    @reg fun reg_result() {
        on (posedge(clk)) {
            if (reset) { // Synchronous reset
                result_aax reg 0
            } else {
                if (start) {
                    result_aax reg when (op) {
                        uint(0b001) -> ext(16, a add b)
                        uint(0b010) -> ext(16, a and b)
                        uint(0b011) -> ext(16, a xor b)
                        else -> null
                    }
                }
            }
        }
    }

    @reg fun reg_done() {
        on(posedge(clk), posedge(reset)) {
            done_aax reg if (reset) true else start && !!op
        }
    }
}
