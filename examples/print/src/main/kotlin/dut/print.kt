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

@top class _print: _module {

    val clk   = _bool()
    val reset = _bool()
    val count = _uint(8)

    @seq fun count() {
        on (posedge(clk)) {
            println("count=$count")
            if (reset) {
                count reg 0
            } else {
                count reg count + 1
            }
        }
    }

    @run fun clk() {
        clk put false
        forever {
            delay(1)
            clk put !clk
        }
    }

    @run fun reset() {
        reset put true
        delay(2)
        reset put false
        delay(16)
        finish()
    }
}