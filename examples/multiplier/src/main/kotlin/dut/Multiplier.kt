/*
 * Copyright (c) 2020 Francis Wang
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

val WIDTH = 8

class Multiplier: Module() {

    @input var clk      = t_Boolean()
    @input var rst      = t_Boolean()
    @input var req      = t_MultiplierReq()
    @output var res     = t_Ubit(2 * WIDTH)
    @output var res_rdy = t_Boolean()

    var a    = t_Ubit(WIDTH)
    var b    = t_Ubit(WIDTH)
    var prod = t_Ubit(WIDTH)
    var tp   = t_Ubit(WIDTH)
    var i    = t_Ubit(log(WIDTH) + 1)

    @seq fun mul_step() {
        on (posedge(clk)) {
            if (rst) {
                a = u(0)
                b = u(0)
                prod = u(0)
                tp = u(0)
                i = u(WIDTH)
            } else {
                if (req.vld) {
                    a = req.a
                    b = req.b
                    prod = u(0)
                    tp = u(0)
                    i = u(0)
                } else if (i < u(WIDTH)) {
                    val sum = if (b[0]) {
                        tp add a
                    } else {
                        tp.ext(WIDTH + 1)
                    }
                    b = b shr 1
                    prod = cat(sum[0], prod[WIDTH - 1, 1])
                    tp = sum[WIDTH, 1]
                    i += u(1)
                }
            }
        }
    }

    @com fun set_res () {
        res_rdy = (i == u(WIDTH))
        res = cat(tp, prod)
    }
}
