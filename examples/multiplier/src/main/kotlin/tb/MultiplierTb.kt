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

import dut.*
import verik.base.*
import verik.data.*

class MultiplierTb: Module() {

    private var clk     = t_Boolean()
    private var rst     = t_Boolean()
    private var req     = t_MultiplierReq()
    private var res     = t_Ubit(2 * WIDTH)
    private var res_rdy = t_Boolean()

    private var expected = t_Ubit(2 * WIDTH)

    @make var multiplier = t_Multiplier().with(
        clk     = clk,
        rst     = rst,
        req     = req,
        res     = res,
        res_rdy = res_rdy
    )

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
        req = i_MultiplierReq(u(0), u(0), false)
        expected = u(0)
        delay(20)
        forever {
            wait(negedge(clk))
            if (res_rdy) {
                if (res == expected) {
                    println("PASSED ${req.a} * ${req.b} test function gave $res")
                } else {
                    println("FAILED ${req.a} * ${req.b} test function gave $res instead of $expected")
                }
                req = i_MultiplierReq(u(random()), u(random()), true)
                expected = req.a mul req.b
            } else {
                req = i_MultiplierReq(u(0), u(0), false)
            }
        }
    }
}