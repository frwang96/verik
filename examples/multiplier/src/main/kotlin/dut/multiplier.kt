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

class _multiplier: _module() {

    @input var clk      = _bool()
    @input var rst      = _bool()
    @input var in_a     = _ubit(8)
    @input var in_b     = _ubit(8)
    @input var in_vld   = _bool()
    @output var res     = _ubit(16)
    @output var res_rdy = _bool()

    var a    = _ubit(8)
    var b    = _ubit(8)
    var prod = _ubit(8)
    var tp   = _ubit(8)
    var i    = _ubit(4)

    var sum = _ubit(9)

    @com fun sum() {
        if (b[0]) {
            sum = tp add a
        } else {
            sum = cat(false, tp)
        }
    }

    @seq fun mul_step() {
        on (posedge(clk)) {
            if (rst) {
                a = ubit(0)
                b = ubit(0)
                prod = ubit(0)
                tp = ubit(0)
                i = ubit(8)
            } else {
                if (in_vld) {
                    a = in_a
                    b = in_b
                    prod = ubit(0)
                    tp = ubit(0)
                    i = ubit(0)
                } else if (i < ubit(8)) {
                    b = b sr 1
                    prod = cat(sum[0], prod[7, 1])
                    tp = sum[8, 1]
                    i += ubit(1)
                }
            }
        }
    }

    @com fun set_res () {
        res_rdy = (i == ubit(8))
        res = cat(tp, prod)
    }
}
