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

@top class _print: _module {

    private var clk   = _bool()
    private var reset = _bool()
    private var count = _uint(8)

    @seq fun count() {
        on (posedge(clk)) {
            println("count=$count")
            if (reset) {
                count = uint(0)
            } else {
                count += 1
            }
        }
    }

    @run fun clk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }

    @run fun reset() {
        reset = true
        delay(2)
        reset = false
        delay(16)
        finish()
    }
}