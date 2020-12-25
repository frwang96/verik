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

@top class _multiplier_tb: _module {

    var clk     = _bool()
    var rst     = _bool()
    var in_a    = _ubit(8)
    var in_b    = _ubit(8)
    var in_vld  = _bool()
    var res     = _ubit(16)
    var res_rdy = _bool()

    @make var multiplier = _multiplier() with {
        it.clk    = clk
        it.rst    = rst
        it.in_a   = in_a
        it.in_b   = in_b
        it.in_vld = in_vld
        res       = it.res
        res_rdy   = it.res_rdy
    }

    @run fun clk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @run fun run() {
        rst = true
        in_a = ubit(0)
        in_b = ubit(0)
        in_vld = false
        wait(negedge(clk))
        delay(20)
        rst = false
        delay(1000)
        finish()
    }

    @seq fun test_gen() {
        on (posedge(clk)) {
            random()
        }
    }
}