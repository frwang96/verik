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

package tb

import dut.WIDTH
import dut.t_Multiplier
import verik.base.*
import verik.data.*

class MultiplierTB: Module() {

    private var clk     = t_Boolean()
    private var rst     = t_Boolean()
    private var in_a    = t_Ubit(WIDTH)
    private var in_b    = t_Ubit(WIDTH)
    private var in_vld  = t_Boolean()
    private var res     = t_Ubit(2 * WIDTH)
    private var res_rdy = t_Boolean()

    private var expected = t_Ubit(2 * WIDTH)

    @make var multiplier = t_Multiplier() with {
        it.clk    = clk
        it.rst    = rst
        it.in_a   = in_a
        it.in_b   = in_b
        it.in_vld = in_vld
        res       = it.res
        res_rdy   = it.res_rdy
    }

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @run fun toggle_rst() {
        rst = true
        wait(negedge(clk))
        rst = false
        delay(1000)
        finish()
    }

    @run fun test_gen() {
        in_a = u(0)
        in_b = u(0)
        expected = u(0)
        delay(20)
        forever {
            wait(negedge(clk))
            if (res_rdy) {
                if (res == expected) {
                    println("PASSED $in_a * $in_b test function gave $res")
                } else {
                    println("FAILED $in_a * $in_b test function gave $res instead of $expected")
                }
                in_a = u(random())
                in_b = u(random())
                in_vld = true
                expected = in_a mul in_b
            } else {
                in_vld = false
            }
        }
    }
}