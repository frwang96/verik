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

package multiplier

import io.verik.core.*

@SimTop
class MultiplierTb : Module() {

    var clk: Boolean = nc()
    var rst: Boolean = nc()
    var req: MultiplierReq = nc()
    var rsp: MultiplierRsp = nc()

    @Make
    val multiplier = Multiplier(
        clk = clk,
        rst = rst,
        req = req,
        rsp = rsp
    )

    @Run
    fun toggleClk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @Run
    fun toggleRst() {
        rst = true
        wait(negedge(clk))
        rst = false
        delay(1000)
        finish()
    }

    @Run
    fun test() {
        var a: Ubit<REQ_WIDTH> = u0()
        var b: Ubit<REQ_WIDTH> = u0()
        req = MultiplierReq(a, b, false)
        delay(20)
        forever {
            wait(negedge(clk))
            if (rsp.vld) {
                if (rsp.result == a mul b) {
                    println("PASSED $a * $b test function gave ${rsp.result}")
                } else {
                    println("FAILED $a * $b test function gave ${rsp.result} instead of ${a mul b}")
                }
                a = randomUbit()
                b = randomUbit()
                req = MultiplierReq(a, b, true)
            } else {
                req = MultiplierReq(u0(), u0(), false)
            }
        }
    }
}
