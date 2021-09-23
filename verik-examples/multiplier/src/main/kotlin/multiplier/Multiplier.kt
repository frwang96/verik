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

class Multiplier(
    @In var clk: Boolean,
    @In var rst: Boolean,
    @In var req: MultiplierReq,
    @Out var rsp: MultiplierRsp
) : Module() {

    var a: Ubit<`8`> = nc()
    var b: Ubit<REQ_WIDTH> = nc()
    var prod: Ubit<REQ_WIDTH> = nc()
    var tp: Ubit<REQ_WIDTH> = nc()
    var i: Ubit<INCLOG<REQ_WIDTH>> = nc()

    @Seq
    fun step() {
        on(posedge(clk)) {
            if (rst) {
                a = zeroes()
                b = zeroes()
                prod = zeroes()
                tp = zeroes()
                i = u<REQ_WIDTH>()
            } else {
                if (req.vld) {
                    a = req.a
                    b = req.b
                    prod = zeroes()
                    tp = zeroes()
                    i = zeroes()
                } else if (i <= u<REQ_WIDTH>()) {
                    val sum = if (b[0]) {
                        tp add a
                    } else {
                        tp.ext()
                    }
                    b = b shr 1
                    prod = cat(sum[0], prod.slice<DEC<REQ_WIDTH>>(1))
                    tp = sum.slice(1)
                    i += u(1)
                }
            }
        }
    }

    @Com
    fun setReq() {
        rsp.vld = (i == u<REQ_WIDTH>())
        rsp.result = cat(tp, prod)
    }
}
