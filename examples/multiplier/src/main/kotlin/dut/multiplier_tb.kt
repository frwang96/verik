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

    private var clk     = _bool()
    private var rst     = _bool()
    private var in_a    = _ubit(8)
    private var in_b    = _ubit(8)
    private var in_vld  = _bool()
    private var res     = _ubit(16)
    private var res_rdy = _bool()

    private var expected = _ubit(16)

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
        wait(negedge(clk))
        rst = false
        delay(1000)
        finish()
    }

    @run fun test_gen() {
        in_a = ubit(0)
        in_b = ubit(0)
        expected = ubit(0)
        delay(20)
        forever {
            wait(negedge(clk))
            if (res_rdy) {
                if (res == expected) {
                    println("PASSED $in_a * $in_b test function gave $res")
                } else {
                    println("FAILED $in_a * $in_b test function gave $res instead of $expected")
                }
                in_a = ubit(random())
                in_b = ubit(random())
                in_vld = true
                expected = in_a mul in_b
            } else {
                in_vld = false
            }
        }
    }
}