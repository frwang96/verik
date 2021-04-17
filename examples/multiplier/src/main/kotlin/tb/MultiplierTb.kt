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

import dut.DATA_WIDTH
import dut.Multiplier
import dut.MultiplierReq
import dut.RES_WIDTH
import verik.base.*
import verik.data.*

@top object MultiplierTb: Module() {

    var clk: Boolean = d()
    var rst: Boolean = d()
    var req: MultiplierReq = d()
    var res: Ubit<RES_WIDTH> = d()
    var resRdy: Boolean = d()

    @ins var multiplier = Multiplier(
        clk    = clk,
        rst    = rst,
        req    = req,
        res    = res,
        resRdy = resRdy
    )

    @run fun toggleClk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @run fun toggleRst() {
        rst = true
        repeat (2) { wait(negedge(clk)) }
        rst = false
        delay(1000)
        finish()
    }

    @run fun testGen() {
        var a: Ubit<DATA_WIDTH> = u(0)
        var b: Ubit<DATA_WIDTH> = u(0)
        req = MultiplierReq(a, b, false)
        delay(20)
        forever {
            wait(negedge(clk))
            if (resRdy) {
                if (res == a mul b) {
                    println("PASSED $a * $b test function gave $res")
                } else {
                    println("FAILED $a * $b test function gave $res instead of ${a mul b}")
                }
                a = u(random())
                b = u(random())
                req = MultiplierReq(a, b, true)
            } else {
                req = MultiplierReq(u(0), u(0), false)
            }
        }
    }
}