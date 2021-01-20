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

class _multiplier: _module() {

    @input var clk      = _bool()
    @input var rst      = _bool()
    @input var in_a     = _ubit(WIDTH)
    @input var in_b     = _ubit(WIDTH)
    @input var in_vld   = _bool()
    @output var res     = _ubit(2 * WIDTH)
    @output var res_rdy = _bool()

    var a    = _ubit(WIDTH)
    var b    = _ubit(WIDTH)
    var prod = _ubit(WIDTH)
    var tp   = _ubit(WIDTH)
    var i    = _ubit(log(WIDTH) + 1)

    @seq fun mul_step() {
        on (posedge(clk)) {
            if (rst) {
                a = u(0)
                b = u(0)
                prod = u(0)
                tp = u(0)
                i = u(WIDTH)
            } else {
                if (in_vld) {
                    a = in_a
                    b = in_b
                    prod = u(0)
                    tp = u(0)
                    i = u(0)
                } else if (i < u(WIDTH)) {
                    val sum = if (b[0]) {
                        tp add a
                    } else {
                        tp.ext(WIDTH + 1)
                    }
                    b = b sr 1
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
